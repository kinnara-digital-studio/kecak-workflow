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
=======
***Load Binder*** : The Load Binder is reponsible to popoulate the form data by default, workflow Form Binder is the default binder used to return data form sbumitted forms.

***Store Binder*** : The Store Binder is Responsible to store the from the data. By default, [Workflow Form Binder] is the default used to store data from submitted forms.
```
Under Normal circumtances, the load and Store binder should be the same to maintain the same 
source for the data to be written to/read form.
```

***Permission*** : Manage the permission on who to see this section. 

***Autorize Message*** : Manage to be shown when permission is defined.

***Post Processing Tool*** : Tool to run based on event below
***Run Tool on*** : - Data creation
					- Data update
					- Both date creation and update
