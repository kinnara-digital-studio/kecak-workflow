# Plugin Architecture #

## What is Plugin ? ##

- Allow the Functionality of the system to be extended dynamically.
- To achieve Extensibitlit and adabtability of product features
- Any kind of integration that is not yet Avaible in Kecak Workflow as standard feature can be Accomplished by developing a Plugin, without breaking th fundamental core of the product.
- Kecak Workflow plugin Architecture 2 types of plugin stucture.
	
	- Standard Java Plugin 
	- Dynamic Osgi Plugin
	
### Standard Java Plugin ###

- Build as a standard java JAR
- Plugin classes should place a package name start with "org.kecak"
- Make JAR avaible in the Java classpatch e.g place it under WEB-INF/lib
- may cause library version conflict with the base libraries or other plugins
- easier to develop and test using normal java classes and libraries.

### Dynamic Osgi Plugin ###

- Build as an Osgi (OPen Services Gateway Initative Framework) JAR bundle
- Deploy JAR using the manage plugins in the WEB console 
- Su 