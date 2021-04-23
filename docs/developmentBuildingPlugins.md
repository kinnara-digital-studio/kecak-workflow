# Building Plugins #

### Getting Kecak Workflow Source Code

- Checkout the source from public mirror at GitHub `https://github.com/kinnara-digital-studio/kecak-workflow`

- clone Kecak Workflow `https://github.com/kinnara-digital-studio/kecak-workflow.git` to your repository 

### Creating a maven project for your plugins

- Using Kecak Workflow sub project “wflow-plugin-archetype” to create a Maven project for your plugin 

#### for windows 

1. Create a directory to contain your plugins e.g. “C:\kecak”

2. In command prompt, go to the created directory.

3. Run `C:\Kinnara\kecak-workflow\wflow-plugin-archetype\create-plugin.bat com.kinnara.kecakplugins."PluginsName" kecak-plugins-"PluginsName" 5.0-SNAPSHOT`

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingPlugins-buildingPlugins1.png" alt="buildingPlugins-buildingPlugins1" />

4. Input your plugins version 

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingPlugins-buildingPlugins2.png" alt="buildingPlugins-buildingPlugins2" />

5. Key in `5.0-SNAPSHOT` for version and `y` to confirm all the information

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingPlugins-buildingPlugins3.png" alt="buildingPlugins-buildingPlugins3" />

#### for Linux 

1. Create a directory at home directory to contain your plugins. E.g. `~\kecak`

2. In command terminal, go to the created directory.

3. Run `~\Kinnara\kecak-workflow\wflow-plugin-archetype\create-plugin.bat com.kinnara.kecakplugins."PluginsName" kecak-plugins-"PluginsName" 5.0-SNAPSHOT`

4. Input your plugins version

5. Key in `5.0-SNAPSHOT` for version and `y` to confirm all the information

##### What is inside the maven project 

- pom.xml
	- pom.xml stands for "Project Object Model“, an XML representation of a Maven project

	- Used to manage your plugin dependencies jar

- Activator.java
	- Bundle Activator for OSGi framework

	- The activator class is the bundle's hook to the life-cycle layer for management.

	- Used to register your plugin class in start method



- [Process](buildingPlugins_Process.md)
- [Form](buildingPlugins_form.md)
- [Datalist](datalist.md)
- [Property Options](buildingPlugins_PropertyOptions.md)
- [Userview](buildingPlugins_Userview.md)
