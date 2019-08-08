package org.joget.plugin.base;

import freemarker.cache.URLTemplateLoader;
import freemarker.template.*;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.felix.framework.Felix;
import org.apache.felix.framework.util.StringMap;
import org.joget.commons.util.*;
import org.joget.plugin.property.model.PropertyEditable;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service methods used to manage plugins
 * 
 */
public class PluginManager implements ApplicationContextAware {

    private Felix felix = null;
    private String baseDirectory = SetupManager.getBaseSharedDirectory() + File.separator + "app_plugins";
    private ApplicationContext applicationContext;
    @SuppressWarnings("rawtypes")
	private Map<Class, Map<String, Plugin>> pluginCache = new HashMap<Class, Map<String, Plugin>>();
    private Set<String> blackList;
    private Set<String> scanPackageList;
    @SuppressWarnings("rawtypes")
	private Map<String, Class> osgiPluginClassCache = new HashMap<String, Class>();
    private List<String> noOsgiPluginClassCache = new ArrayList<String>();
    private Map<String, Template> templateCache = new HashMap<String, Template>();
    private List<String> noResourceBundleCache = new ArrayList<String>();
    private Map<String, ResourceBundle> resourceBundleCache = new HashMap<String, ResourceBundle>();
    
    public final static String ESCAPE_JAVASCRIPT = "javascript";
    
    /**
     * Used by system to initialize Plugin manager
     */
    public PluginManager() {
        init();
    }

    /**
     * Used by system to initialize Plugin manager
     */
    public PluginManager(String baseDirectory) {
        if (baseDirectory != null) {
            this.baseDirectory = baseDirectory;
        }
        init();
    }

    /**
     * Used by system to retrieves a list of black list plugin classname 
     */
    public Set<String> getBlackList() {
        return blackList;
    }

    /**
     * Used by system to sets a list of black list plugin classname 
     * @param blackList 
     */
    public void setBlackList(Set<String> blackList) {
        this.blackList = blackList;
    }

    /**
     * Used by system to retrieves a list of custom scanning packages
     * @return 
     */
    public Set<String> getScanPackageList() {
        return scanPackageList;
    }

    /**
     * Used by system to sets a list of custom scanning packages
     * @param scanPackageList
     */
    public void setScanPackageList(Set<String> scanPackageList) {
        this.scanPackageList = scanPackageList;
    }

    /**
     * Retrieves plugin base directory from system setup
     */
    public String getBaseDirectory() {
        try {
            String dataFileBasePath = SetupManager.getSettingValue("dataFileBasePath");
            if (dataFileBasePath != null && dataFileBasePath.length() > 0) {
                return dataFileBasePath + File.separator + "plugins";
            } else {
                return baseDirectory;
            }
        } catch (Exception ex) {
            return baseDirectory;
        }
    }

    /**
     * Initializes the plugin manager
     */
    @SuppressWarnings("unchecked")
	protected void init() {
        Properties config = new Properties();
        try {
            config.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException ex) {
            LogUtil.error(PluginManager.class.getName(), ex, "");
        }

        // workaround for log4j classloading issues
        System.setProperty("log4j.ignoreTCL", "true");
        
        // Create a case-insensitive configuration property map.
        @SuppressWarnings("rawtypes")
		Map configMap = new StringMap();
        configMap.putAll(config);
        // Configure the Felix instance to be embedded.

        // Explicitly specify the directory to use for caching bundles.
        String targetCache = "target/felix-cache/";
        String tempDir = System.getProperty("java.io.tmpdir");
        File targetDir = new File(tempDir, targetCache);
        File targetCacheDir = new File(targetDir, "cache");

        if (HostManager.isVirtualHostEnabled()) {
            // locate empty cache directory to use
            boolean proceed = false;
            int count = 0;
            while (!proceed) {
                // check for existing cache
                String dirName = "cache" + count;
                targetCacheDir = new File(targetDir, dirName);
                File[] bundles = targetCacheDir.listFiles();
                proceed = bundles == null || bundles.length <= 1;
                count++;
            }
        }
        // set configuration
        configMap.put("org.osgi.framework.storage", targetCacheDir.getAbsolutePath());
        configMap.put("felix.log.level", "0");
        configMap.put("org.osgi.framework.storage.clean", "onFirstInit");
        configMap.put("felix.cache.locking", "false");

        try {
            if (felix == null) {
                felix = new Felix(configMap);
                felix.start();
            }
            //refresh();
            LogUtil.info(PluginManager.class.getName(), "PluginManager initialized");
        } catch (Exception ex) {
            LogUtil.error(PluginManager.class.getName(), ex, "Could not create framework");
        }

    }

    /**
     * Find and install plugins from the baseDirectory
     */
    public void refresh() {
        uninstallAll(false);
        installBundles();
    }

    protected void installBundles() {

        Collection<URL> urlList = new ArrayList<URL>();
        File baseDirFile = new File(getBaseDirectory());
        recurseDirectory(urlList, baseDirFile);

        Collection<Bundle> bundleList = new ArrayList<Bundle>();
        for (URL url : urlList) {
            // install the JAR file as a bundle
            String location = url.toExternalForm();
            Bundle bundle = installBundle(location);
            if (bundle != null) {
                bundleList.add(bundle);
            }

        }

        for (Bundle bundle : bundleList) {
            startBundle(bundle);
        }


    }

    protected void recurseDirectory(Collection<URL> urlList, File baseDirFile) {
        File[] files = baseDirFile.listFiles();
        if (files != null) {
            for (File file : files) {
                //LogUtil.info(getClass().getName(), " -" + file.getName());
                if (file.isFile() && file.getName().toLowerCase().endsWith(".jar")) {
                    try {
                        urlList.add(file.toURI().toURL());
                        LogUtil.debug(PluginManager.class.getName(), " found jar " + file.toURI().toURL());
                    } catch (MalformedURLException ex) {
                        LogUtil.error(PluginManager.class.getName(), ex, "");
                    }
                } else if (file.isDirectory()) {
                    recurseDirectory(urlList, file);
                }
            }
        }
    }

    protected Bundle installBundle(String location) {
        try {
            BundleContext context = felix.getBundleContext();
            Bundle newBundle = context.installBundle(location);
            if (newBundle.getSymbolicName() == null) {
                newBundle.uninstall();
                newBundle = null;
            } else {
                newBundle.update();
            }
            // clear cache
            pluginCache.clear();
            osgiPluginClassCache.clear();
            noOsgiPluginClassCache.clear();
            templateCache.clear();
            resourceBundleCache.clear();
            noResourceBundleCache.clear();
            return newBundle;
        } catch (Exception be) {
            LogUtil.error(PluginManager.class.getName(), be, "Failed bundle installation from " + location + ": " + be.toString());
            return null;
        }
    }

    protected boolean startBundle(Bundle bundle) {
        try {
            final BundleContext context = felix.getBundleContext();

            //bundle.update();
            bundle.start();

            // execute event onInstall
                Arrays.stream(bundle.getRegisteredServices())
                        .filter(Objects::nonNull)
                        .map(context::getService)
                        .filter(o -> o instanceof Plugin)
                        .map(o -> (Plugin) o)
                        .forEach(p -> p.onInstall(applicationContext));

            LogUtil.info(PluginManager.class.getName(), "Bundle " + bundle.getSymbolicName() + " started");
        } catch (Exception be) {
            LogUtil.error(PluginManager.class.getName(), be, "Failed bundle start for " + bundle + ": " + be.toString());
            return true;
        }
        return false;
    }

    /**
     * List registered plugins
     * @return
     */
    public Collection<Plugin> list() {
        return list(null);
    }

    /**
     * Returns a list of plugins, both from the OSGI container and the classpath.
     * Plugins from the OSGI container will take priority if there are conflicting classes.
     * @param clazz Optional filter for type of plugins to return, null will return all.
     * @return
     */
    public Collection<Plugin> list(@SuppressWarnings("rawtypes") Class clazz) {
        // lookup in cache
        @SuppressWarnings("rawtypes")
		Class classFilter = (clazz != null) ? clazz : Plugin.class;
        Map<String, Plugin> pluginMap = pluginCache.get(classFilter);
        if (pluginMap == null) {
            // load plugins
            pluginMap = internalLoadPluginMap(clazz);

            // store in cache
            pluginCache.put(classFilter, pluginMap);
        }
        Collection<Plugin> pluginList = new ArrayList<Plugin>();
        pluginList.addAll(pluginMap.values());
        return pluginList;
    }

    /**
     * Returns a list of plugins from the OSGI container only.
     * @param clazz Optional filter for type of plugins to return, null will return all.
     * @return
     */
    @SuppressWarnings("unchecked")
	public Collection<Plugin> listOsgiPlugin(@SuppressWarnings("rawtypes") Class clazz) {
        Map<String, Plugin> pluginMap = new TreeMap<String, Plugin>();

        // find OSGI plugins
        Collection<Plugin> pluginList = loadOsgiPlugins();
        for (Plugin plugin : pluginList) {
            if (clazz == null || clazz.isAssignableFrom(plugin.getClass())) {
                pluginMap.put(plugin.getName(), plugin);
            }
        }

        return pluginMap.values();
    }

    /**
     * Returns a map of plugins with class name as key, both from the OSGI container and the classpath.
     * Plugins from the OSGI container will take priority if there are conflicting classes.
     * @param clazz Optional filter for type of plugins to return, null will return all.
     * @return
     */
    public Map<String, Plugin> loadPluginMap(@SuppressWarnings("rawtypes") Class clazz) {
        Map<String, Plugin> pluginMap = new HashMap<String, Plugin>();
        for (Plugin plugin : list(clazz)) {
            pluginMap.put(ClassUtils.getUserClass(plugin).getName(), plugin);
        }
        return pluginMap;
    }

    /**
     * Returns a list of plugins, both from the OSGI container and the classpath.
     * Plugins from the OSGI container will take priority if there are conflicting classes.
     * @param clazz Optional filter for type of plugins to return, null will return all.
     * @return A Map of name=pluginObject
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	protected Map<String, Plugin> internalLoadPluginMap(Class clazz) {
        Map<String, Plugin> pluginMap = new TreeMap<String, Plugin>();

        Class classFilter = (clazz != null) ? clazz : Plugin.class;

        // find plugins in classpath
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(classFilter));
        Set<BeanDefinition> components = provider.findCandidateComponents("org.joget");
        if (scanPackageList != null) {
            for (String scanPackage: scanPackageList) {
                components.addAll(provider.findCandidateComponents(scanPackage));
            }
        }
        // sort plugins
		List<BeanDefinition> componentList = new ArrayList(components);
        componentList.sort(Comparator.comparing(BeanDefinition::getBeanClassName));
        for (BeanDefinition component : componentList) {
            String beanClassName = component.getBeanClassName();
            if (blackList == null || !blackList.contains(beanClassName)) {
                try {
                    Class<? extends Plugin> beanClass = Class.forName(beanClassName).asSubclass(Plugin.class);
                    Plugin plugin = beanClass.newInstance();
                    pluginMap.put(plugin.getName(), plugin);
                } catch (Exception ex) {
                    LogUtil.warn(PluginManager.class.getName(), " Error loading plugin class  " + beanClassName);
                }
            }
        }

        // find OSGI plugins
        Collection<Plugin> pluginList = loadOsgiPlugins();
        for (Plugin plugin : pluginList) {
            if (clazz == null || clazz.isAssignableFrom(ClassUtils.getUserClass(plugin))) {
                if (blackList == null || !blackList.contains(ClassUtils.getUserClass(plugin).getName())) {
                    pluginMap.put(plugin.getName(), plugin);
                }
            }
        }

        LogUtil.debug(PluginManager.class.getName(), " Loaded plugins from classpath and OSGI container");
        return pluginMap;
    }

    /**
     * Load all plugins from the OSGI container
     * @return
     */
    protected Collection<Plugin> loadOsgiPlugins() {
        Collection<Plugin> list = new ArrayList<>();
        BundleContext context = felix.getBundleContext();
        Bundle[] bundles = context.getBundles();
        for (Bundle b : bundles) {
            @SuppressWarnings("rawtypes")
			ServiceReference[] refs = b.getRegisteredServices();
            if (refs != null) {
                for (@SuppressWarnings("rawtypes") ServiceReference sr : refs) {
                    LogUtil.debug(PluginManager.class.getName(), " bundle service: " + sr);
                    @SuppressWarnings("unchecked")
					Object obj = context.getService(sr);
                    if (obj instanceof Plugin) {
                        list.add((Plugin) obj);
                    }
                    context.ungetService(sr);
                }
            }
        }
        return list;
    }

    /**
     * Disable plugin
     * @param name
     */
    public boolean disable(String name) {
        boolean result = false;
        BundleContext context = felix.getBundleContext();
        @SuppressWarnings("rawtypes")
		ServiceReference sr = context.getServiceReference(name);
        if (sr != null) {
            try {
                sr.getBundle().stop();
                context.ungetService(sr);
                result = true;
            } catch (Exception ex) {
                LogUtil.error(PluginManager.class.getName(), ex, "");
            }
        }
        return result;
    }

    /**
     * Install a new plugin
     * @return
     */
    public boolean upload(String filename, InputStream in) {
        String location = null;
        File outputFile = null;
        try {
            // check filename
            if (filename == null || filename.trim().length() == 0) {
                throw new PluginException("Invalid plugin name");
            }
            if (!filename.endsWith(".jar")) {
                filename += ".jar";
            }

            // write file
            outputFile = new File(getBaseDirectory(), filename);
            File outputDir = outputFile.getParentFile();
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            try(FileOutputStream out = new FileOutputStream(outputFile)) {
                BufferedInputStream bin = new BufferedInputStream(in);
                int len = 0;
                byte[] buffer = new byte[4096];
                while ((len = bin.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
                out.flush();
                location = outputFile.toURI().toURL().toExternalForm();
            }

            // validate jar file
            boolean isValid = false;
            try (JarFile jarFile = new JarFile(outputFile)) {
                isValid = true;
            } catch (IOException ex) {
                //delete invalid file
                try {
                    outputFile.delete();
                } catch (Exception e) {
                    LogUtil.error(PluginManager.class.getName(), ex, "");
                }

                LogUtil.error(PluginManager.class.getName(), ex, "");
                throw new PluginException("Invalid jar file");
            } catch (Exception ex) {
                LogUtil.error(PluginManager.class.getName(), ex, "");
                throw new PluginException("Invalid jar file");
            }

            // install
            if (location != null && isValid) {
                Bundle newBundle = installBundle(location);
                if (newBundle != null) {
                    startBundle(newBundle);
                }
            }

            return true;
        } catch (Exception ex) {
            LogUtil.error(PluginManager.class.getName(), ex, "");
            throw new PluginException("Unable to write plugin file", ex);
        }
    }

    /**
     * Uninstall/remove all plugin, option to deleting the plugin file
     * @param deleteFiles
     * @return
     */
    public void uninstallAll(boolean deleteFiles) {
        Collection<Plugin> pluginList = this.list();
        for (Plugin plugin : pluginList) {
            uninstall(ClassUtils.getUserClass(plugin).getName(), deleteFiles);
        }
    }

    /**
     * Uninstall/remove a plugin, and delete the plugin file
     * @param name
     * @return
     */
    public boolean uninstall(String name) {
        return uninstall(name, true);
    }

    /**
     * Uninstall/remove a plugin, option to deleting the plugin file
     * @param name
     * @return
     */
    public boolean uninstall(String name, boolean deleteFile) {
        boolean result = false;
        BundleContext context = felix.getBundleContext();
        @SuppressWarnings("rawtypes")
		ServiceReference sr = context.getServiceReference(name);
        if (sr != null) {
            try {
                Bundle bundle = sr.getBundle();
                bundle.stop();
                bundle.uninstall();

                // execute event onUninstall
                Arrays.stream(bundle.getRegisteredServices())
                        .filter(Objects::nonNull)
                        .map(context::getService)
                        .filter(o -> o instanceof Plugin)
                        .map(o -> (Plugin)o)
                        .forEach(p -> p.onUninstall(applicationContext));

                String location = bundle.getLocation();
                context.ungetService(sr);

                // delete location
                if (deleteFile) {
                    File file = new File(new URI(location));
                    @SuppressWarnings("unused")
					boolean deleted = file.delete();
                }
                result = true;

                // clear cache
                pluginCache.clear();
            } catch (Exception ex) {
                LogUtil.error(PluginManager.class.getName(), ex, "");
            }
        }
        return result;
    }

    /**
     * Returns a plugin, from either the OSGI container and the classpath.
     * Plugins from the OSGI container will take priority if there are conflicting classes.
     * @param name Class name of the required plugin
     * @return
     */
    public Plugin getPlugin(String name) {
        if (blackList != null && blackList.contains(name)) {
            return null;
        }
        
        if (name != null && name.trim().length() > 0 && !"null".equalsIgnoreCase(name)) {
            Plugin plugin = loadOsgiPlugin(name);
            if (plugin == null) {
                plugin = loadClassPathPlugin(name);
            }
            return plugin;
        } else {
            return null;
        }
    }

    /**
     * Retrieve a plugin from the OSGI container
     * @param name Fully qualified class name for the required plugin
     * @return
     */
    protected Plugin loadOsgiPlugin(String name) {
        Plugin plugin = null;
        if (!noOsgiPluginClassCache.contains(name)) {
            try {
                @SuppressWarnings("rawtypes")
				Class clazz = osgiPluginClassCache.get(name);
                if (clazz == null) {
                    BundleContext context = felix.getBundleContext();

                    @SuppressWarnings("rawtypes")
					ServiceReference sr = context.getServiceReference(name);
                    if (sr != null) {
                        clazz = sr.getBundle().loadClass(name);
                        osgiPluginClassCache.put(name, clazz);
                        context.ungetService(sr);
                    }
                }

                if (clazz != null) {
                    Object obj = clazz.newInstance();
                    boolean isPlugin = obj instanceof Plugin;
                    LogUtil.debug(PluginManager.class.getName(), " plugin obj " + obj + " class: " + obj.getClass().getName() + " " + isPlugin);
                    LogUtil.debug(PluginManager.class.getName(), " plugin classloader: " + obj.getClass().getClassLoader());
                    LogUtil.debug(PluginManager.class.getName(), " current classloader: " + Plugin.class.getClassLoader());
                    if (isPlugin) {
                        plugin = (Plugin) obj;
                    }
                } else {
                    noOsgiPluginClassCache.add(name);
                }
            } catch (Exception ex) {
                LogUtil.error(PluginManager.class.getName(), ex, "");
                throw new PluginException("Plugin " + name + " could not be retrieved", ex);
            }
        }
        return plugin;
    }

    /**
     * Retrieve a plugin using the system classloader
     * @param name Fully qualified class name for the required plugin
     * @return
     */
    protected Plugin loadClassPathPlugin(String name) {
        Plugin plugin = null;
        try {
            @SuppressWarnings("rawtypes")
			Class clazz = Class.forName(name);
            Object obj = clazz.newInstance();
            plugin = (Plugin) obj;
        } catch (Exception ex) {
            LogUtil.debug(PluginManager.class.getName(), "plugin " + name + " not found in classpath");
        }
        return plugin;
    }

    /**
     * Retrieves an InputStream to a resource from a plugin. The plugin may either be from OSGI container or system classpath.
     * @param pluginName
     * @param resourceUrl
     * @return
     * @throws IOException
     */
    public InputStream getPluginResource(String pluginName, String resourceUrl) throws IOException {
        InputStream result = null;

        URL url = getPluginResourceURL(pluginName, resourceUrl);
        if (url != null) {
            // get inputstream from url
            if (url != null) {
                result = url.openConnection().getInputStream();
            }
        }

        return result;
    }

    /**
     * Reads a resource from a plugin. java.util.Formatter text patterns supported.
     * @param pluginName
     * @param resourceUrl
     * @param arguments
     * @param removeNewLines
     * @param translationPath
     * @return null if the resource is not found or in the case of an exception
     * @see java.util.Formatter
     */
    public String readPluginResourceAsString(String pluginName, String resourceUrl, Object[] arguments, boolean removeNewLines, String translationPath) {
        String output = null;
        InputStream input = null;
        ByteArrayOutputStream stream = null;
        if (pluginName != null && resourceUrl != null) {
            try {
                input = getPluginResource(pluginName, resourceUrl);
                if (input != null) {
                    // write output
                    stream = new ByteArrayOutputStream();
                    byte[] bbuf = new byte[65536];
                    int length = 0;
                    while ((input != null) && ((length = input.read(bbuf)) != -1)) {
                        stream.write(bbuf, 0, length);
                    }
                }
            } catch (Exception e) {
                LogUtil.error(PluginManager.class.getName(), e, "Error reading resource ");
            } finally {
                try {
                    if (stream != null) {
                        stream.flush();
                        stream.close();
                    }
                    if (input != null) {
                        input.close();
                    }
                } catch (Exception e) {
                    LogUtil.error(PluginManager.class.getName(), e, "Error closing IO");
                }
            }
            
            // set and return output
            if (stream != null) {
                output = new String(stream.toByteArray());

                if (arguments != null && arguments.length > 0) {
                    // format arguments
                    output = String.format(output, arguments);
                }

                if (removeNewLines) {
                    // compress by removing new lines
                    output = output.replace('\n', ' ');
                    output = output.replace('\r', ' ');
                    output = output.trim();
                }
            }

            String escapeType = null;

            if (resourceUrl.endsWith(".json") || resourceUrl.endsWith(".js")) {
                escapeType = ESCAPE_JAVASCRIPT;
            }

            output = processPluginTranslation(output, pluginName, translationPath, escapeType);
        }

        return output;
    }

    /**
     * Reads a message bundle from a plugin.
     * @param pluginName
     * @param translationPath
     * @return null if the resource bundle is not found or in the case of an exception
     */
    public ResourceBundle getPluginMessageBundle(String pluginName, String translationPath) {
        String cacheKey = pluginName + "_" + translationPath;
        if (!noResourceBundleCache.contains(cacheKey)) {
            ResourceBundle bundle = resourceBundleCache.get(cacheKey);
            if (bundle == null) {
                // get plugin
                Plugin plugin = getPlugin(pluginName);
                if (plugin != null) {

                    LocaleResolver localeResolver = (LocaleResolver) getBean("localeResolver");
                    Locale locale = localeResolver.resolveLocale(getHttpServletRequest());

                    try {
                        bundle = ResourceBundle.getBundle(translationPath, locale, plugin.getClass().getClassLoader());
                        if (bundle != null) {
                            resourceBundleCache.put(cacheKey, bundle);
                        } else {
                            noResourceBundleCache.add(cacheKey);
                        }
                    } catch (Exception e) {
                        LogUtil.debug(PluginManager.class.getName(), translationPath + " translation file not found");
                        noResourceBundleCache.add(cacheKey);
                    }
                } else {
                    noResourceBundleCache.add(cacheKey);
                }
            }
            return bundle;
        }
        return null;
    }
    
    /**
     * Method used to parse the message key to message in a content based on plugin
     * message bundle
     * @param content
     * @param pluginName
     * @param translationPath
     * @return 
     */
    public String processPluginTranslation(String content, String pluginName, String translationPath) {
        return processPluginTranslation(content, pluginName, translationPath, null);
    }

    /**
     * Method used to parse the message key to message in a content based on plugin
     * message bundle. Option to escape javascript in the message 
     * @param content
     * @param pluginName
     * @param translationPath
     * @param escapeType
     * @return 
     */
    public String processPluginTranslation(String content, String pluginName, String translationPath, String escapeType) {
        if (!(content != null && content.indexOf("@@") >= 0)) {
            return content;
        }

        Pattern pattern = Pattern.compile("\\@@([^@@^\"^ ])*\\.([^@@^\"])*\\@@");
        Matcher matcher = pattern.matcher(content);

        List<String> keyList = new ArrayList<String>();
        while (matcher.find()) {
            keyList.add(matcher.group());
        }

        if (!keyList.isEmpty()) {
            ResourceBundle bundle = null;
            
            if (translationPath != null && !translationPath.isEmpty()) {
                bundle = getPluginMessageBundle(pluginName, translationPath);
            }

            for (String key : keyList) {
                String tempKey = key.replaceAll("@@", "");
                String label = null;

                if (bundle != null && bundle.containsKey(tempKey)) {
                    label = bundle.getString(tempKey);
                } else if (ResourceBundleUtil.getMessage(tempKey) != null) {
                    label = ResourceBundleUtil.getMessage(tempKey);
                }

                if (label != null) {
                    if (ESCAPE_JAVASCRIPT.equals(escapeType)) {
                        label = StringEscapeUtils.escapeJavaScript(label);
                    }
                    content = content.replaceAll(StringUtil.escapeRegex(key), StringUtil.escapeRegex(label));
                }
            }
        }

        return content;
    }

    /**
     * Method used to get message from plugin message bundle
     * @param key
     * @param pluginName
     * @param translationPath
     * @return 
     */
    public String getMessage(String key, String pluginName, String translationPath) {
        return processPluginTranslation("@@" + key + "@@", pluginName, translationPath);
    }

    /**
     * Method used to gets freemarker template from plugin jar
     * @param data
     * @param pluginName
     * @param templatePath
     * @param translationPath
     * @return 
     */
    @SuppressWarnings("unchecked")
	public String getPluginFreeMarkerTemplate(@SuppressWarnings("rawtypes") Map data, final String pluginName, final String templatePath, String translationPath) {
        // add request into data model
        if (!data.containsKey("request")) {
            try {
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
                if (request != null) {
                    data.put("request", request);
                } else {
                    Map<String, String> r = new HashMap<String, String>();
                    r.put("contextPath", "/kecak");
                    data.put("request", r);
                }
            } catch (NoClassDefFoundError e) {
                // ignore if servlet request is not available
            }
        }
        
        String result = "";
        try {
            String cacheKey = pluginName + "_" + templatePath;
            Template temp = templateCache.get(cacheKey);
            if (temp == null) {

                // init configuration
                Configuration configuration = new Configuration();

                configuration.setObjectWrapper(new DefaultObjectWrapper() {
                    // override object wrapper for Maps to support list-ordered maps

                    @SuppressWarnings("rawtypes")
					@Override
                    public TemplateModel wrap(Object obj) throws TemplateModelException {
                        if (obj instanceof Map) {
                            return new ListOrderedHash((Map) obj, this);
                        } else {
                            return super.wrap(obj);
                        }
                    }
                });

                // set template loader
                configuration.setTemplateLoader(new URLTemplateLoader() {

                    @Override
                    protected URL getURL(String string) {
                        URL url = getPluginResourceURL(pluginName, templatePath);
                        return url;
                    }

                    @Override
                    public long getLastModified(Object templateSource) {
                        return 0;
                    }

                });

                // Get or create a template
                temp = configuration.getTemplate(templatePath);
                templateCache.put(cacheKey, temp);
            }

            // Merge data-model with template
            Writer out = new StringWriter();
            temp.process(data, out);
            out.flush();
            result = out.toString();

            result = processPluginTranslation(result, pluginName, translationPath);

        } catch (Exception ex) {
            LogUtil.error(PluginManager.class.getName(), ex, "");
            result = ex.toString();
        }
        return result;
    }

    /**
     * Retrieves a URL to a resource from a plugin. The plugin may either be from OSGI container or system classpath.
     * @param pluginName
     * @param resourceUrl
     * @return
     */
    public URL getPluginResourceURL(String pluginName, String resourceUrl) {
        URL url = null;

        // get plugin
        Plugin plugin = getPlugin(pluginName);

        if (plugin != null) {
            // get class loader for plugin
            ClassLoader loader = plugin.getClass().getClassLoader();

            // get resource url, remove first /
            if (resourceUrl.startsWith("/")) {
                resourceUrl = resourceUrl.substring(1);
            }
            url = loader.getResource(resourceUrl);
        }

        return url;
    }

    /**
     * Execute a plugin
     * @param name The fully qualified class name of the plugin
     * @param properties
     * @return
     */
    public Object execute(String name, @SuppressWarnings("rawtypes") Map properties) {
        Object result = null;
        Plugin plugin = getPlugin(name);
        if (plugin != null) {
            result = plugin.execute(properties);
            LogUtil.info(PluginManager.class.getName(), " Executed plugin " + plugin + ": " + result);
        } else {
            LogUtil.info(PluginManager.class.getName(), " Plugin " + name + " not found");
        }
        return result;
    }

    /**
     * Method used by Felix Framework to Stop the plugin manager
     */
    public synchronized void shutdown() {
        if (felix != null) {
            try {
                uninstallAll(false);
                felix.stop();
            } catch (Exception ex) {
                LogUtil.error(PluginManager.class.getName(), ex, "Could not stop Felix");
            }
            felix = null;
        }
    }

    /**
     * Method used by Felix Framework to shutdown the plugin manager
     */
    @Override
    public void finalize() {
        shutdown();
    }

    /**
     * Method used to test a plugin
     * 
     * @param name
     * @param location
     * @param properties
     * @param override
     * @return 
     */
    public Object testPlugin(String name, String location, @SuppressWarnings("rawtypes") Map properties, boolean override) {
        LogUtil.info(PluginManager.class.getName(), "====testPlugin====");
        // check for existing plugin
        Plugin plugin = getPlugin(name);
        boolean existing = (plugin != null);
        boolean install = (location != null && location.trim().length() > 0);

        // install plugin
        if (install && (!existing || override)) {
            try {
                LogUtil.info(PluginManager.class.getName(), " ===install=== ");
                File file = new File(location);
                if (file.exists()) {
                    try(InputStream in = new FileInputStream(file)) {
                        upload(file.getName(), in);
                    }
                }
            } catch (Exception ex) {
                LogUtil.error(PluginManager.class.getName(), ex, "");
            }
        }

        // execute plugin
        LogUtil.info(PluginManager.class.getName(), " ===execute=== ");
        Object result = execute(name, properties);
        LogUtil.info(PluginManager.class.getName(), "  result: " + result);

        // uninstall plugin
        if (install && (!existing || override)) {
            LogUtil.info(PluginManager.class.getName(), " ===uninstall=== ");
            uninstall(name);
        }
        LogUtil.info(PluginManager.class.getName(), "====testPlugin end====");

        return result;
    }

    /**
     * Methods used by Felix Framework
     * @param args 
     */
    public static void main(String[] args) {
//        String pluginDirectory = "target/wflow-bundles";
        PluginManager pm = new PluginManager();

        try {
            LogUtil.info(PluginManager.class.getName(), " ===Plugin List=== ");
            for (Plugin p : pm.list()) {
                LogUtil.info(PluginManager.class.getName(), " plugin: " + p.getName() + "; " + p.getClass().getName());
            }
            String samplePluginFile = "../wflow-plugins/wflow-plugin-sample/target/wflow-plugin-sample.jar";
            String samplePlugin = "org.joget.plugin.sample.SamplePlugin";

            File file = new File(samplePluginFile);
            LogUtil.info(PluginManager.class.getName(), " ===Install SamplePlugin=== ");
            try(FileInputStream in = new FileInputStream(file)) {
                pm.upload(file.getName(), in);
            } catch (Exception ex) {
                LogUtil.error(PluginManager.class.getName(), ex, "");
            }

            LogUtil.info(PluginManager.class.getName(), " ===Plugin List after install=== ");
            for (Plugin p : pm.list()) {
                LogUtil.info(PluginManager.class.getName(), " plugin: " + p.getName() + "; " + p.getClass().getName());
            }

            LogUtil.info(PluginManager.class.getName(), " ===Execute SamplePlugin=== ");
            pm.execute(samplePlugin, null);

            LogUtil.info(PluginManager.class.getName(), " ===Uninstall SamplePlugin=== ");
            pm.uninstall(samplePlugin);
            LogUtil.info(PluginManager.class.getName(), " ===New Plugin List after removal=== ");
            for (Plugin p : pm.list()) {
                LogUtil.info(PluginManager.class.getName(), " plugin: " + p.getName() + "; " + p.getClass().getName());
            }
            pm.refresh();
            LogUtil.info(PluginManager.class.getName(), " ===New Plugin List after refresh=== ");
            for (Plugin p : pm.list()) {
                LogUtil.info(PluginManager.class.getName(), " plugin: " + p.getName() + "; " + p.getClass().getName());
            }

            pm.testPlugin(samplePlugin, samplePluginFile, null, true);
        } finally {
            pm.shutdown();
        }
    }
    
    /**
     * Gets the current Http Request
     * @return 
     */
    public HttpServletRequest getHttpServletRequest() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            return request;
        } catch (NoClassDefFoundError e) {
            // ignore if servlet request class is not available
            return null;
        } catch (IllegalStateException e) {
            // ignore if servlet request is not available, e.g. when triggered from a deadline
            return null;
        }
    }

    /**
     * Gets a class bean from ApplicationContext
     * @param beanName
     * @return 
     */
    public Object getBean(String beanName) {
        Object bean = null;
        if (applicationContext != null) {
            bean = applicationContext.getBean(beanName);
        }
        return bean;
    }

    /**
     * Method used for system to set ApplicationContext
     * @param appContext
     * @throws BeansException 
     */
    public void setApplicationContext(ApplicationContext appContext) throws BeansException {
        this.applicationContext = appContext;
        refresh();
    }
    
    public Class<?> findClass (String name) {
    	Class<?> result = null;
    	BundleContext context = felix.getBundleContext();
    	for (Bundle bundle : context.getBundles()) {
            try {
                Class<?> _class = bundle.loadClass(name);
                result = (_class);
            } catch (ClassNotFoundException e) {
                // No problem, this bundle doesn't have the class
            }
        }
    	
    	return result;
    	
    }

    /**
     * Kecak Exclusive
     *
     * Generate plugin object
     *
     * @param elementSelect
     * @param <T>
     * @return
     */
    public <T extends PropertyEditable> T getPluginObject(Map<String, Object> elementSelect) {
        if (elementSelect == null)
            return null;

        String className = (String) elementSelect.get("className");
        Map<String, Object> properties = (Map<String, Object>) elementSelect.get("properties");

        T plugin = (T) getPlugin(className);
        if (plugin == null) {
            LogUtil.warn(PluginManager.class.getName(), "Error generating plugin [" + className + "]");
            return null;
        }

        properties.forEach(plugin::setProperty);

        return plugin;
    }
}