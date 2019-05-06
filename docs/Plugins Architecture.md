#Plugins Architecture

###Allows The Functionality Of The System To Be Extended Dynamically.
To Achieve Extensibility And Adaptability Of Product Features 
Any Kind Of Integration That Is Not Yet Available In Kecak Workflow As Standard Feature Can Be Accomplished By Developing A Plugin, Without Breaking The Fundamental Core Of The Product.
Kecak Workflow Plugin Architecture Supported 2 Type Of Plugin Structure
Standard Java Plugin
Dynamic Osgi Plugin


###Standard Java Plugin
Build As A Standard Java JAR
Plugin Classes Should Place In A Package Name Start With “org.kecak”
Make JAR Available In The Java Classpath E.G. Place It Under WEB-INF/lib
Requires Restarting The JVM For Deployment Or Changes
May Cause Library Version Conflicts With Base Libraries Or Other Plugins
Easier To Develop And Test Using Normal Java Classes And Libraries

###Dynamic OSGi Plugin
Build as an OSGi (Open Services Gateway initiative framework) JAR bundle
Deploy JAR using the Manage Plugins in the Web Console
Supports dynamic loading/unloading/reloading without restarting
Runs in isolated mode thus prevents library version conflict with base libraries or other plugins
More difficult to develop and test due to OSGi configuration and isolation

###Workflow Engine Plugin Types
Deadline Plugins Provide The Ability To Recalculate Deadline Limit And SLA Limit Based On Programming Logic. 
Process Participant Plugins Are Used To Provide Custom Selection Of Users To Workflow Participants.
Process Tool Plugins To Integrate With External Systems.

###Form Builder Plugin Types
Form Element Plugins To Extend Types Of Fields Available In Form Builder
Form Load Binder Plugins To Extend Methods Of Loading Data In A Form From Any Data Source.
Form Options Binder Plugins To Extends Method To Loading Data For A Form Field’s Options From Any Data Source. 
Form Store Binder Plugins To Extend Methods Of Storing Data In A Form To Any Data Source.
Form Validator Plugins To Extend Ways To Validate Form Data.

###Datalist Builder Plugin Types
Datalist Action Plugins To Extend Methods Of Executing An Action On List Item. E.G. Delete A Record
Datalist Binder Plugins To Extend Methods Of Loading Data For A List.
Datalist Column Formatter Plugins To Extend Ways Of Formatting Column Data.

###Userview Builder Plugin Types
Userview Menu Plugins To Extend Types Of Pages Available In Userview Builder.
Userview Permission Plugins To Handle Permissions And Access Rights In A Userview.
Userview Theme Plugins To Change The Ui Design Of Userview.

###App Level Plugin Types
Audit Trail Plugins Is Triggered After Process Related Event To Provide Extra Processing Capabilities. E.G. Capture Reporting Data Or User Notification. 
Hash Variable Plugins To Extend Support Of Processing Hash Variable.

###System Level Plugin Types
Directory Manager Plugins To Integrate Users From External System. E.G. Active Directory Or LDAP

