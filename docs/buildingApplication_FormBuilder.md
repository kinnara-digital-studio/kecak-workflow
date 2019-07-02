# Form Builder 

# Form 

Kecak Workflow makes it easy for you to design your form. one can easy build a form by using a simplistic and guided interface 

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingApplication-FormBuilder.png" alt="buildingApplication-FormBuilder" />

Forms are made up of form elements, many types of which are already built inside Joget and ready to be used. We have listed them below.

When you design forms, you must first set its properties

## Form Properties ##

---

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingPlugins-formProperties.png" alt="buildingPlugins-formProperties" />

| Name | Description |
|---|---|
| ID | Form ID |
| Name | Form Name |
| Table Name | Table Name |
| Description | Description of what the form intends to do. This is meant for developer/admin consumption only to describe the purpose of this element | 


<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingPlugins-formPropertiesAdvanced.png" alt="buildingPlugins-formPropertiesAdvanced" />

***Load Binder*** : The Load Binder is reponsible to popoulate the form data by default, workflow Form Binder 
is the default binder used to return data form sbumitted forms 

***Store Binder*** : The Store Binder is Responsible to store the from the data. By default, [Workflow Form Binder]
is the default used to store data from submitted forms.

***Load Binder*** : The Load Binder is reponsible to popoulate the form data by default, workflow Form Binder is the default binder used to return data form sbumitted forms.

***Store Binder*** : The Store Binder is Responsible to store the from the data. By default, [Workflow Form Binder] is the default used to store data from submitted forms.
```
Under Normal circumtances, the load and Store binder should be the same to maintain the same 
source for the data to be written to/read form.
```

***Permission*** : Manage the permission on who to see this section. 

***Autorize Message*** : Manage to be shown when permission is defined.

***Post Processing Tool*** : Tool to run based on event below
***Run Tool on*** : 

- Data creation
- Data update
- Both date creation and update


## Form Element ## 


- Form Element is a type of plugin that is extensible via Kecak's plugin architecture. 
- Form Element is responsible for providing the end users form input elements to interact with.

- There are many different form fields (i.e., TextField, SelectBox, Hidden Field, etc.) to choose from when your are designing your forms.  

You can drag-and-drop them on your form canvas and then edit their properties. 

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingPlugins-textField.png" alt="buildingPlugins-textField" />

Each form element has its own set of attributes, ID and label attributes are common to most of then.

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingPlugins-editTextField.png" alt="buildingPlugins-editTextField" />

***ID***


The 'ID' in the property editor is a unique identifier. 


Reserved IDs

Do not use the following reserved IDs. "appId, appVersion, version, userviewId, menuId, key, embed" on the form element's ID attribute.
```

***Label***


Label is the human-readable identifier for the form field.


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

