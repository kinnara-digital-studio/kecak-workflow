# Kecak Mobile - Store Binder - Form Data API #

## REST API ##

### Overview ###

Store Binder - Form Data API is plugin to storing data from client to certain form and certain process. Server will responses by giving back its process ID.

##### Url #####

`web/json/plugin/com.kinnara.kecakplugins.mobileapi.FormStoreBinderApi/service`

##### Method #####
HTTP POST

##### Header #####
* Authorization : *your access token*
* Referer : *your server host*
* Content-Type : *application/json*

##### Parameters #####
* appId - Application ID
* appVersion - Application Version
* processDefId - Process Definition ID
* formId - Form ID
* processId - Process ID
* activiyId - Activity ID

##### Example #####
`https://kecak.kinnarastudio.com/web/json/plugin/com.kinnara.kecakplugins.mobileapi.FormStoreBinderApi/service?appVersion=1&processDefId=testdua&appId=trainingREST&formId=testDuaForm1`

### Use of API Plugin ###
#### Run Process ####

The example below shows you that how to use Form Store Binder API plugin. First of all, fill the authorization with your currently token or make it empty if your currently token is empty.

![Authorization](/uploads/989fbe2dabd1ceead9f56d88cd86c901/Authorization.PNG)

And also, you have to fill the header section by **Referer** and **Content-Type**. For this example, **Referer** is `https://kecak.kinnarastudio.com` and **Content-Type** is **application/json**

![Headers](/uploads/923d21ff7a2412203fc2c6b4e2bdfcdc/Headers.PNG)

After that, you have to fill request body in JSON format (choose **raw** in POSTMAN) which contains the Form Data.

```json
{
    "gridBiasa": [],
    "selectBox": "Finance",
    "testId": "",
    "grid_peserta": [],
    "nametest": "f",
    "kolombaru": "n",
    "peserta": "c"
}
```

Request parameters are needed to set your process definition ID and form ID. For this example request parameters is as follow :

*appId*        : triningREST <br/>
*appVersion*   : 1 <br/>
*processDefId* : testdua <br/>
*formId*       : testDuaForm1 <br/>

![Request_Parameters](/uploads/3dcfbf0246dd4280e0aae0f625d9ae20/Request_Parameters.PNG)

If you have done all steps above, the server will responses by giving you JSONBody in **Response Body** which contains its process ID and the message.

**Response is as follow** :

```json
{
    "processId": "1405_trainingREST_testdua",
    "message": "Form has been submitted",
    "dateSubmitted": "7/26/18 3:12:18 PM"
}
```

#### Save and Update Master Data ####

Furthermore, you also can save or update master data on kecak platform by this plugin API. First, you have to fill request body in JSON format (choose **raw** in POSTMAN) which contains the Form Data.

```json
{
    "selectBox": "test",
    "kolombaru": "test",
    "nametest": "test"
}
```

Request parameters are needed to set form ID. For this example request parameters is as follow :

*appId*        : triningREST <br/>
*appVersion*   : 1 <br/>
*formId*       : testDuaForm1 <br/>

If you have done all steps above, the server will responses by giving you JSONBody in **Response Body** which contains its primary key and the message.

**Response is as follow** :

```json
{
    "primary key": "1924d5cc-ac1f2796-323f2a51-c4987c10",
    "message": "Form has been submitted",
    "dateSubmitted": "8/8/18 10:46:09 AM"
}
```

#### Complete Assignment ####

Furthermore, you also can complete an assignment on kecak platform by this plugin API. First, you have to fill request body in JSON format (choose **raw** in POSTMAN) which contains the Form Data.

```json
{
    "selectBox": "test",
    "kolombaru": "test",
    "nametest": "test"
}
```

Request parameters are needed to set process ID or/and activity ID (you can only use process ID for certain process and you can add parameter activity ID for choose which activity in the process). For this example request parameters is as follow :

*appId*        : triningREST <br/>
*appVersion*   : 1 <br/>
*formId*       : testDuaForm1 <br/>
*processId*    : 1502_trainingREST_testdua <br/>
*activityId*   : 2421_1502_trainingREST_testdua_activity1 <br/>

If you have done all steps above, the server will responses by giving you JSONBody in **Response Body** which contains its primary key and the message.

**Response is as follow** :

```json
{
    "primary key": "1928cfbe-ac1f2796-323f2a51-d017c7dd",
    "message": "Assignment completed",
    "dateSubmitted": "8/8/18 10:50:29 AM"
}
```


### Version History ###

*  **1.0.0**
   * Initial creation

