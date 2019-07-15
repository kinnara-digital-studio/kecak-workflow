# REST API Plugins

## Form

REST API Plugins can be made on forms, the REST API that can be used on this form is **Load Binder** and **Store Binder**.

On the form itself, it can only use load binders, store binders, or can use both (load & store binders).

The following is an example of how to use load binders and store binders.

### Load Binder

1. Open form

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/1selectForm.png" alt="selectForm.png" />


2. Go to properties

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/2selectProperties.png" alt="selectProperties.png" />


3. Open advanced page

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/3selectAdvanced.png" alt="selectAdvanced.png" />

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/LoadBinder1.png" alt="LoadBinder1.png" />


4. Fill Load Binder field with **REST Load Binder**

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/5RestLoadBiner.png" alt="RestLoadBiner.png" />

5. Open Load Binder page 

Fill field in Rest Load Binder Page like this picture :

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/10EditLoadBinder.png" alt="EditLoadBinder.png" />


5. Security Settings

Please check list for security settings like this picture:

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/securitySettings.png" alt="securitySettings.png" />


**Description :**

|   FIELD              |               DESCRIPTION                 |
|----------------------|-------------------------------------------|
|**Api URL**           |                URL from API               |
|**Parameters**        |               URL parameters              |
|**Headers**           |                Http headers               |
|**Record Path**       |The json data structure that will be taken |


6. Click button "OK" to save all configuration settings in that form

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/ok.png" alt="ok.png" />


7. Click Save

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/11save.png" alt="11save.png" />


### Store Binder

1. Open form

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/1selectForm.png" alt="selectForm.png" />


2. Go to properties

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/2selectProperties.png" alt="selectProperties.png" />


3. Open advanced page

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/3selectAdvanced.png" alt="selectAdvanced.png" />

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/storeBinder1.png" alt="storeBinder.png" />


4. Fill Store Binder field with **REST Store Binder**

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/6RestStoreBiner.png" alt="RestStoreBiner.png" />


5. Open Store Binder (REST Store Binder) page

Fill field in Rest Store Binder Page like this picture :

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/8EditStoreBinder.png" alt="EditStoreBinder.png" />

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/body.png" alt="body.png" />

6. Security Settings

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/securitySettingsStoreBinder.png" alt="securitySettingsStore.png" />


**Description :**

|   FIELD              |                                                DESCRIPTION                                              |
|----------------------|---------------------------------------------------------------------------------------------------------|
|**Api URL**           |                                                URL from API                                             |
|**Parameters**        |                                              URL parameters                                             |
|**Headers**           |                                               Http headers                                              |
|**Body**              |To replace the data with other values entered in the form (ex: used hash variable / string interpolation)|

**Body Example**

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/securitySettingsStoreBinder.png" alt="securitySettingsStore.png" />


7. Click button "OK" to save all configuration settings in that form

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/ok2.png" alt="ok.png" />


8. Click Save

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/11save.png" alt="11save.png" />


## How to know for fill **Record Path**

You need **Postman** to get information for fill **Record Path** field.

Then follow this step :
 > Open **Postman**
 
 > Copy API URL from **Rest Load Binder** settings and paste to the place like this picture :
 
 <img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/postman1.png" alt="postman.png" />

 
 > fill all configuration in **Postman** like this picture :
 
  <img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/postman2.png" alt="postman.png" />
 
 
 > Click **Send** and will be shown :
 
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/postmanBody.png" alt="postmanBody.png" />

Word **"data"** in the **Record Path** field is derived from json data marked by the black box in the image above.
