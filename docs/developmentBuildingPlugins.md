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

### Getting and Building Joget WOrkflow Source Code 

- Checkout the source from public mirror at GitHub at or main SVN repository at `http://svn.github.com/jogetworkflow/jw-community.git http://dev.joget.org/svn/jw-community/trunk` (requires login) or get it in your Developer Training Materials

- Assuming you are using the CollabNet client, in the directory you created for Joget source, run `svn co http://svn.github.com/jogetworkflow/jw-community.git` to check out code from GitHub.

- Run “mvn clean install” in wflow-app project directory to build it.

### Creating a maven project for your plugins

- Using Joget Workflow sub project “wflow-plugin-archetype” to create a Maven project for your plugin 

#### for windows 

1. Create a directory to contain your plugins e.g. “C:\kecak”

2. In command prompt, go to the created directory.

3. Run `%HOMEPATH%\Desktop\Developer Training Materials\Joget Workflow source code\wflow-plugin-archetype\create-plugin.bat" org.joget.sample sample-plugin-pack`

4. Key in `3.0-SNAPSHOT` for version and `y` to confirm all the information

#### for Linux 

1. Create a directory at home directory to contain your plugins. E.g. `~\kecak`

2. In command terminal, go to the created directory.

3. Run `~\Developer Training Materials\Joget Workflow source code\wflow-plugin-archetype\create-plugin.bat" org.joget.sample sample-plugin-pack

4. Key in `3.0-BETA` for version and `y` to confirm all the information

##### What is inside the maven project 

- pom.xml
	- pom.xml stands for "Project Object Model“, an XML representation of a Maven project

	- Used to manage your plugin dependencies jar

- Activator.java
	- Bundle Activator for OSGi framework

	- The activator class is the bundle's hook to the lifecycle layer for management.

	- Used to register your plugin class in start method



- [Participant](buildingPlugins_Participant.md)
- [Activities](buildingPlugins_Activities.md)
- [Process](buildingPlugins_Process.md)
- [Workflow Variable](buildingPlugins_WorkflowVariable.md)
- [Routes](buildingPlugins_Routes.md)
- [Publishing Process](buildingPlugins_PublishingProcess.md)
- [Form](buildingPlugins_form.md)
- [Datalist](datalist.md)
- [Userview](buildingPlugins_Userview.md)
- [Publising Apps](buildingPlugins_PublishingApps)
