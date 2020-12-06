# Building Plugins #

### Setting up JDK and Apache Maven 2

#### For Windows

1. Open windows system properties dialog
2. Selecting the `Advanced` tab, and the `Environment Variables` button.
3. Add new system variable named `JAVA_HOME`, and set its value to JDK installation directory e.g. `C:\Program Files\Java\jdk1.6.0_27`
4. Add new system variable named `M2_HOME`, and set its value to Apache Maven 2 installation directory e.g. `C:\Program Files\apache-maven-2.2.1`.
5. Append `;%JAVA_HOME%\bin;%M2_HOME%\bin` to system variable named `Path`.
6. Open command prompt and run `mvn –version` to verify.

#### For Linux

1. Open command terminal
2. Set environment variable `JAVA_HOME` to the location of JDK. E.G `export JAVA_HOME=/usr/java/jdk1.6.0_27`.
3. Set environment variable `M2_HOME` to the location of your Apache Maven 2. E.g. `export M2_HOME=/usr/apache-maven-2.2.1`.
4. Run `export PATH=$PATH:$JAVA_HOME/bin: $M2_HOME/bin` to add both into environment variable named `PATH`.
5. Run `mvn –version` to verify.

### Setting up Joget Workflow Maven Depedencies 

This is one time setup only

#### For Windows

1. Open command prompt
2. Go to the location of your developer training materials, go into Maven dependency folder.
3. Run `setup-dependency-window.bat`.

#### For Linux 

1. Open command terminal.
2. Go to the location of your developer training materials, go into Maven dependency folder.
3. Run `setup-dependency-linux.sh`.

### Getting Kecak Workflow Source Code

- Checkout the source from public mirror at GitHub `https://github.com/kinnara-digital-studio/kecak-workflow`

- clone Kecak Workflow `https://github.com/kinnara-digital-studio/kecak-workflow.git` to your repository 

### Creating a maven project for your plugins

- Using Joget Workflow sub project “wflow-plugin-archetype” to create a Maven project for your plugin 

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

	- The activator class is the bundle's hook to the lifecycle layer for management.

	- Used to register your plugin class in start method



- [Process](buildingPlugins_Process.md)
- [Form](buildingPlugins_form.md)
- [Datalist](datalist.md)
- [Property Options](buildingPlugins_PropertyOptions.md)
- [Userview](buildingPlugins_Userview.md)
