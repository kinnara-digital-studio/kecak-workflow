# Building Plugins #

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


### Creating a maven project for your plugins

- Using Joget Workflow sub project “wflow-plugin-archetype” to create a Maven project for your plugin 

#### for windows 

1. Create a directory to contain your plugins. E.g. “C:\kecak”

2. In Command Prompt, go to the created directory.

3. Run `%HOMEPATH%\Desktop\Developer Training Materials\Joget Workflow source code\wflow-plugin-archetype\create-plugin.bat" org.joget.sample sample-plugin-pack`

4. Key in `3.0-SNAPSHOT` for version and `y` to confirm all the information

#### for Linux 

1. Create a directory at home directory to contain your plugins. E.g. `~\kecak`

2. In Command Terminal, go to the created directory.

3. Run `~\Developer Training Materials\Joget Workflow source code\wflow-plugin-archetype\create-plugin.bat" org.joget.sample sample-plugin-pack

4. Key in `3.0-BETA` for version and `y` to confirm all the information

##### What is inside the maven project 

- pom.xml
	- POM stands for "Project Object Model“, an XML representation of a Maven project

	- Used to manage your plugin dependencies jar

- Activator.java
	- Bundle Activator for OSGi framework

	- The activator class is the bundle's hook to the lifecycle layer for management.

	-Used to register your plugin class in start method



- [Participant](buildingPlugins_Participant.md)
- [Activities](buildingPlugins_Activities.md)
- [Process](buidingPlugins_Process.md)
- [Workflow Variable](buildingPlugins_WorkflowVariable.md)
- [Routes](buildingPlugins_Routes.md)
- [Publishing Process](buidingPlugins_PublishingProcess.md)
- [Form](buildingPlugins_form.md)
- [Datalist](datalist_DatalistAction.md)
- [Userview](buildingPlugins_Userview.md)
- [Publising Apps]()