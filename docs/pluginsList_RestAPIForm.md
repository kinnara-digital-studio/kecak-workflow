# REST API Plugins

## Form

REST API Plugins bisa dibuat pada form, REST API yang dapat digunakan pada form ini adalah **Load Binder** dan **Store Binder**.

Pada form sendiri, bisa hanya menggunakan load binder saja, store binder saja, atau bisa menggunakan keduanya (load & store binder).

Berikut ini adalah contoh cara penggunaan load binder dan store binder.

### Load Binder

1. Open form

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/1selectForm.png" alt="selectForm.png" />


2. Go to properties

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/2selectProperties.png" alt="selectProperties.png" />


3. Open advanced page

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/3selectAdvanced.png" alt="selectAdvanced.png" />

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/4editLoadAndStoreBinder.png" alt="editLoadAndStoreBinder.png" />


4. Fill Load Binder field with **REST Load Binder**

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/5RestLoadBiner.png" alt="RestLoadBiner.png" />

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

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/4editLoadAndStoreBinder.png" alt="editLoadAndStoreBinder.png" />


4. Fill Load Binder field with **REST Store Binder**

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/6RestStoreBiner.png" alt="RestStoreBiner.png" />


5. Security Settings

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/securitySettingsStore.png" alt="securitySettingsStore.png" />

6. Open Rest Load Binder page

Fill field in Rest Load Binder Page like this picture :

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/10EditLoadBinder.png" alt="EditLoadBinder.png" />


9. Open Store Binder (REST Store Binder) page

Fill field in Rest Load Binder Page like this picture :

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/8EditStoreBinder.png" alt="EditStoreBinder.png" />

**Description :**

|   FIELD              |               DESCRIPTION                 |
|----------------------|-------------------------------------------|
|**Api URL**           |                URL from API               |
|**Parameters**        |               URL parameters              |
|**Headers**           |                Http headers               |
|**Record Path**       |The json data structure that will be taken |


10. Click button "OK" to save all configuration settings in that form

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/ok.png" alt="ok.png" />


11. Click Save

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
