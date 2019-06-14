# Kecak Mobile - Store Binder - File Upload API #

## REST API ##

### Overview ###

Store Binder - File Upload API is plugin to uploading file from client to certain form, certain field, and certain primary key value. Server will responses by giving back its path file and status.

##### Url #####

`web/json/plugin/com.kinnara.kecakplugins.mobileapi.FileUploadApi/service`

##### Method #####
HTTP POST

##### Header #####
* Authorization : *your access token*
* Referer : *your server host*
* Content-Type : *application/json*

##### Parameters #####
* appId - Application ID
* fileName - File Name included its extension
* fieldId - Field ID
* formId - Form ID
* primaryKey - Primary Key Value

##### Example #####
`https://kecak.kinnarastudio.com/web/json/plugin/com.kinnara.kecakplugins.mobileapi.FileUploadApi/service?appId=trainingREST&fileName=Bukti Transfer_No 50.JPG&fieldId=fileUpload&formId=testDuaForm1&primaryKey=1404_trainingREST_testdua`

### Use of API Plugin ###

The example below shows you that how to use File Upload API plugin. First of all, fill the authorization with your currently token or make it empty if your currently token is empty.

![Authorization](/uploads/6d6716597f1a01859502509369af0b7e/Authorization.PNG)

And also, you have to fill the header section by **Referer** and **Content-Type**. For this example, **Referer** is `https://kecak.kinnarastudio.com` and **Content-Type** is **application/json**

![Headers](/uploads/1477ce8be1a27162228559cb4d32ed28/Headers.PNG)

After that, you have to choose the file in request body in binary format.

![Request_Body](/uploads/e99fdf3084b3e35f706a5bd067ce51c5/Request_Body.PNG)

Request parameters are needed to set your Application ID, File Name, Field ID, Form ID, and Primary Key Value. For this example request parameters is as follow :

*appId*        : triningREST <br/>
*fileName*     : Bukti Transfer_No 50.JPG <br/>
*fielId*       : fileUpload <br/>
*formId*       : testDuaForm1 <br/>
*primaryKey*   : 1404_trainingREST_testdua <br/>

![Request_Parameters](/uploads/8ec7b0a84109f275cdab0b4c534241a8/Request_Parameters.PNG)

If you have done all steps above, the server will responses by giving you JSONBody in **Response Body** which contains its path file and status.

**Response is as follow** :

```json
{
    "path": "./wflow//app_formuploads/test_dua/1404_trainingREST_testdua/",
    "status": "success"
}
```


### Version History ###

*  **1.0.0**
   * Initial creation

