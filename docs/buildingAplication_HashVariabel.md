## Hash Variable
### What is Hash Variable ?
Hash Variable gives	users greater flexibility and freedom in accesing useful information or relevant run-time values from the system 
A Hash variable is a special hash-escape keyword that can be used in :
- Form Builder 
- Datalist Builder
- Userview Builder
- Supported plugin configuration properties
- External Form URL when mapping an activity to an external form

to return the value of some useful runtime variable from kecak components.

### Nested Hash Variable 
A Hash Variable can be used another Hash Variable to form a Nested Hash Variable
The syntax for the inner Hash Variable is wrapped by a pair of (curly bracket).

```html
syntax/Format

#prefix.{prefix.variableKey}#
```

for example : 
```html
#date.{envVariable.dateFormat}#
#user.{variable.username}.firstName#
#form.tableChild.field1[{form.tableParent.childId}]#
```
### Escaping the Resultant Hash Variable
The parsed/returned Hash Variable may cause incompatibility with the current context/environment such as syntax error in a script.

Therefore, one may pass in additional parameters into the hash variable declared to escape certain characters.

Before ending a hash variable with a hash "#", add a question mark character "?" followed by the required format. You may include multiple by defining semicolon ";" separated values.

- regex	: used to escape regular expression special character
- json : Used to escape JSON special character
- javascript : Used to escape Javascript special character
- html : Used to escape HTML special characters
- xml : Used to escape XML special characters
- java : Used to escape Java special characters
- sql : Used to escape SQL special character
- url : Used to escape URL special characters
- nl2br : Used to convert new line character to <br> HTML tag
- separator(SEPARATOR_CHARS) : Used to change the default separator ";" to the SEPARATOR_CHARS. eg. ?separator(, ) resulted "abc, def" instead of "abc;def"

Example 
```html
#envVariable.script?java#
#envVariable.script?nl2br;json#
#form.table.users?separator(, )#
```
List of Hash Variable :

- [Workflow Assignment Hash Variable](HashVariable_WorkflowAssignmentHashVariable.md)
- [Workflow Process Hash Variable](HashVariable_WorkflowProcessHashVariable.md)
- [Current User Hash Variable](hashVariable_CurrentUser.md)
- [Date Hash Variable](hashVariable_date.md)  
- [Environment Variable Hash Variable](hashVariable_environmentVariable.md)  
- [Form Data Hash Variable](hashVariable-DataHashVariable.md)
- [Form Binder Hash Variable](hashVariable_FormBinder.md)
- [App Definition Hash Variable](hashVariable_AppDefinition.md)
- [App Message Hash Variable (Internationalization)]()  
- [Performer Hash Variable](hashVariable_Performer.md) 
- [Request Parameter Hash Variable](HashVariable_RequestParameter.md)  
- [User Hash Variable](hashVariable_User.md) 
- [Userview Key Hash Variable](hashVariable_UserviewKey.md)  
- [Workflow Variable Hash Variable](hashVariable_WorkflowVariable.md)
- [Request Hash Variable](hashVariable_Request.md)
- [Platform Hash Variable](hashVariable_Platform.md)
- [Users Hash Variable](hashVariable_Users.md)
- [Bean Shell Hash Variable](hashVariable_BeanShell.md)
