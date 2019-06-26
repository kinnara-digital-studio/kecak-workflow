## Hash Variable
### What is Hash Variable ?
Hash Variable gives	users greater flexibility and freedom in accesing useful information or relevant run-time values from the system 
A Hash varible is a special hash-escape keyword that can be used in :
- Form Builder 
- Datalist Builder
- Userview Bulider
- Supported plugin configuration properties
- External Form URL when mapping an activity to an external form

to return the value of some useful runtime varibale from kecak components.

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

- Workflow Assignment Hash Variable
- Workflow Process Hash Variable
- Current User Hash Variable
- Date Hash Variable  
- Environment Variable Hash Variable  
- Form Data Hash Variable  
- Form Binder Hash Variable
- App Definition Hash Variable
- App Message Hash Variable (Internationalization)  
- Performer Hash Variable  
- Request Parameter Hash Variable  
- User Hash Variable  
- Userview Key Hash Variable  
- Workflow Variable Hash Variable
- Request Hash Variable
- Platform Hash Variable
- Users Hash Variable
- Bean Shell Hash Variable
