# Form Element # 

```
Form Element is a type of plugin that is extensible via Kecak's plugin architecture. 
Form Element is responsible for providing the end users form input elements to interact with.

There are many different form fields (i.e., TextField, SelectBox, Hidden Field, etc.) 
to choose from when your are designing your forms.  

You can drag-and-drop them on your form canvas and then edit their properties. 
```
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingPlugins-textField.png" alt="buildingPlugins-textField" />

Each form element has its own set of attributes, ID and label attributes are common to most of then.

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingPlugins-editTextField.png" alt="buildingPlugins-editTextField" />

***ID***

```
The 'ID' in the property editor is a unique identifier. 
The Joget Workflow core uses this ID as a column name within the table associated to the form (e.g. c_title in the figure above). 
When a user enters a value in the field during a workflow process, the value is stored in that column. 
If the ID has never been used in previous forms associated to the same table, a new column will be created. 
If the ID, on the other hand, has been used before, that column will be used.  
It is important to note that when someone enters a value when completing a form during a workflow process,
that value will overwrite the existing one. 


Reserved IDs

Do not use the following reserved IDs. "appId, appVersion, version, userviewId, menuId, key, embed" on the form element's ID attribute.
```

***Label***

```
Label is the human-readable identifier for the form field.
```

List of Form Elements

- Hidden Field
- Text Field 
- Password Field 
- Text Area 
- Select Box
- Check Box
- Radio 
- Date Picker
- File Upload
- Subform
- Tooltip 
- Text Editor 
- Grid
- Custom HTML
- Section Wizard
- ID Generator Field
- Camera
- Spreadsheets
- Audit Progress List
- Crud SelectBox
- Form Attachment
- Form Grid 
- Captcha
- Audit trail Form

