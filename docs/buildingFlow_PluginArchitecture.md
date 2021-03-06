## Plugins Architecture 

## What is Plugins ##

Plugins are a software extensions that add capabilities an app.

- Allow the fuctionality of the system to be extended dynamically. 
- Bring about extensibility and adabtability of product features.

Any kind of integration that is not yet available in Kecak Workflow as a standard feature can be accomplished by developing a plugin, without breaking the fundamental core of the product.

Kecak Workflow plugins architecture support 2 types of plugins structures :

- Standard Java Plugin
- Dynamic OSGi plugin

### Standard Java Plugin ###

- Build as a standard java JAR
- Plugin classes should place name start with "org.kecak"
- Make JAR avaible in java classpath e.g. place it under WEB-INF/lib
- Requires restarting the JVM version conflict with base libraries or other plugins 
- Easier to develop and test using normal java classes and libraries

### Dynamic Osgi ###

- Build as an Osgi (Open Services Gateway initiative framework) JAR bundle
- Deploy JAR using th manage plugins int the web console 
- Support dynamic loading/unloading/realoading without restarting
- Runs in isolated mode thus prevents library version conflit with base libraries or other plugins 
- More dificult to develop and test due to OSGI configuration and isolation

<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingPlugins-pluginsType.png" alt="buildingPluginspluginsType" />