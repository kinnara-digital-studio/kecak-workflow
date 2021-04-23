# Setup Development Environment #

### Setting up JDK 8

#### For Windows

1. Download JDK 8 from [here](https://www.oracle.com/java/technologies/javase-jdk8-downloads.html)

2. Select the available version, accept license agreement, download JDK installation

3. Install JDK

4. Verify the installation and check javac executable

   ```shell
   # Check Java Version
   java -version
   # Check Java Compiler
   javac -version
   ```

   

**For Linux**

1. Open command terminal

2. Install with command below

   ```sh
   sudo apt update
   sudo apt install openjdk-8-jdk -y
   ```

3. Verify the installation and check javac executable

   ```shell
   # Check Java Version
   java -version
   # Check Java Compiler
   javac -version
   ```



### Setting up Apache Maven

#### For Windows

1. Make sure JDK is installed and `JAVA_HOME` environment variable is configured.

2. Head over to [Maven Official Site](https://maven.apache.org/download.cgi) and download the Maven zip file e.g. **apache-maven-3.6.0-bin.zip**

3. Unzip it to a folder e.g. using `c:\opt\apache-maven-3.6.0`

4. Add a `MAVEN_HOME` system variables, and point it to the Maven folder.

5. Press Windows key, type `adva` and clicks on the `View advanced system settings`

6. In System Properties dialog, select `Advanced` tab and clicks on the `Environment Variables...` button.

7. In “Environment variables” dialog, `System variables`, Clicks on the `New...` button and add a `MAVEN_HOME` variable and point it to `c:\opt\apache-maven-3.6.0`

8. In system variables, find `PATH`, clicks on the `Edit...` button. In “Edit environment variable” dialog, clicks on the `New` button and add this `%MAVEN_HOME%\bin`

9. Verify the installation

   ```shell
   mvn -version
   ```

**For Linux**

1. Open command terminal

2. Install with command below

   ```shell
   sudo apt-get install maven -y
   ```

3. Verify the installation

   ```shell
   mvn -version
   ```

   

### Setting up Kecak Workflow Maven Dependencies 

This is one time setup only

#### For Windows

1. Open command prompt.

2. Go into Maven dependency folder.

3. Clone kecak workflow dependencies

   ```shell
   git clone https://github.com/kinnara-digital-studio/repository.git
   ```


#### For Linux 

1. Open command terminal.

2. Go into Maven dependency folder.

   ```shell
   cd ~/.m2
   ```

3. Clone kecak workflow dependencies

   ```shell
   git clone https://github.com/kinnara-digital-studio/repository.git
   ```

   

### Setting up Bower 

**For Windows**

1. Go to [NodeJS](https://nodejs.org/en/download/) and download NodeJS installer

2. Install NodeJS

3. Open Command prompt and verify installation

   ```shell
   # Check Nodejs version
   node -v
   # Check NPM version
   npm -v
   ```

4. Install Bower

   ```shell
   npm install -g bower 
   ```

   

**For Linux**

1.  Open command terminal

2. Install NodeJS

   ```shell
   sudo apt update
   sudo apt install nodejs
   ```

3. Verify the NodeJS installation

   ```shell
   node -v
   ```

4. Install NPM

   ```shell
   sudo apt install npm
   ```

5. Verify NPM installation

   ```shell
   npm -v
   ```

6. Install bower

   ```shell
   npm install -g bower 
   ```

