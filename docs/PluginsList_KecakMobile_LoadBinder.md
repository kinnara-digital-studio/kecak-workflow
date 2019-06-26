# Kecak Mobile - Load Binder API #

## REST API ##

### Overview ###

Load Binder API is plugin to load tansactional or master data from certain Form for authorized user. Server will responses by giving response body in JSON format which contains the requested transactional or master data form.

##### Url #####

`web/json/plugin/com.kinnara.kecakplugins.mobileapi.FormLoadBinderApi/service`

##### Method #####
HTTP GET

##### Header #####
* Authorization : *your access token*
* Referer : *your server host*
* Content-Type : *application/json*

##### Parameters #####
**Choose only the necessary**
* appId - Application ID
* formId - Form ID
* processId - Process ID
* activityId - Activity ID
* primaryKey - Primary Key for the Form Data
* activityToolId - Activity Tool ID
* activityDefId - Activity Definition ID

##### Example #####
`https://kecak.kinnarastudio.com/web/json/plugin/com.kinnara.kecakplugins.mobileapi.FormLoadBinderApi/service?appId=trainingREST&formId=testDuaForm1&primaryKey=1381_trainingREST_testdua`

### Use of API Plugin ###
#### Example ####
This example is showing the Form Data for authorized user from one of Kecak app, with the form data we want to get is as follow


<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/KecakMobile-LoadBinderAPI-Form_Data.PNG" alt="KecakMobile-LoadBinderAPI-Form_Data" />


Then, you have to fill the authorization with your access token to authorize you as authorized user.

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/KecakMobile-LoadBinderAPI-Form_Data-Authorization.PNG" alt="KecakMobile-LoadBinderAPI-Authorization" />


And also, you have to fill the header section by **Referer** and **Content-Type**. For this example, **Referer** is `https://kecak.kinnarastudio.com` and **Content-Type** is **application/json**

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/KecakMobile-LoadBinderAPI-Form_Data-Headers.PNG" alt="KecakMobile-LoadBinderAPI-Headers" />


Request parameters are needed to filter the Load Binder by its *appId*, *formId*, and *optionId*. For this example request parameter is as follow :

*appId*      : *trainingREST* <br/>
*formId*     : *testDuaForm1* <br/>
*primaryKey*   : *1381_trainingREST_testdua* <br/>


and there are more request parameter you can fill if you want change your request parameter.

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/KecakMobile-LoadBinderAPI-Form_Data-Request_Parameters.PNG" alt="KecakMobile-LoadBinderAPI-Request_Parameters" />


If you have done all steps above, the server will responses by giving you JSONObject in **Response Body** which contains the form data you have been requested.

```json
{
    "dateModified": "2018-07-16 09:14:53.0",
    "grid_peserta": [
        {
            "testId": "1381_trainingREST_testdua",
            "peserta": "test form grid"
        }
    ],
    "fileUpload": [
        "pom.xml",
        "kecak-plugins-mobile-api.iml",
        ".gitignore"
    ],
    "peserta": "arjun section 2",
    "dateCreated": "2018-07-16 09:14:53.0",
    "createdBy": "admin",
    "gridBiasa": [
        {
            "testId": "1381_trainingREST_testdua",
            "peserta": "test form grid"
        }
    ],
    "selectBox": "Finance",
    "modifiedBy": "admin",
    "testId": "",
    "id": "1381_trainingREST_testdua",
    "nama_test": "Arjun test final",
    "kolombaru": "arjun kolom 2"
}
```

### Version History ###

*  **1.0.0**
   * Initial creation

