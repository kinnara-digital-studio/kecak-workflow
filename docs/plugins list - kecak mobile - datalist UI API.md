# Kecak Mobile - Datalist UI API #

## REST API ##

### Overview ###

Datalist UI - API is plugin to get JSON from certain Datalist for authorized user. Server will responses by giving response body in JSON format which contains *appId*, *appVersion*, digested JSON Datalist, JSON Datalist itself, and status.

##### Url #####

`web/json/plugin/com.kinnara.kecakplugins.mobileapi.GetJsonDatalistApi/service`

##### Method #####
HTTP GET

##### Header #####
* Authorization : *your access token*
* Referer : *your server host*
* Content-Type : *application/json*

##### Parameters #####
* appId - Application ID
* appVersion - Application Version (optional): if not included will retrieve the latest version
* digest - Digest
* id - Datalist ID : Datalist to be displayed

##### Example #####
`https://kecak.kinnarastudio.com/web/json/plugin/com.kinnara.kecakplugins.mobileapi.GetJsonDatalistApi/service?appId=eApproval_jo&appVersion=1&id=MasterCategoryList`

### Use of API Plugin ###

This example is showing the JSON of Datalist for authorized user, with the JSON of Datalist on Kecak is as follows


<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/KecakMobile-DatalistAPI.png" alt="KecakMobile-DatalistAPI" />


Then, you have to fill the authorization with your access token to authorize you as authorized user.


<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/KecakMobile-DatalistAPI-Authorization.png" alt="KecakMobile-DatalistAPI-Authorization" />

And also, you have to fill the header section by **Referer** and **Content-Type**. For this example, **Referer** is `https://kecak.kinnarastudio.com` and **Content-Type** is **application/json**


<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/KecakMobile-DatalistAPI-Request_Parameters.png" alt="KecakMobile-DatalistAPI-Request_Parameters" />



Request parameters are needed to filter the datalist by its *appId*, *appVersion*, *digest*, and *id*. For this example request parameters is as follow :

*appId*      : triningREST <br/>
*appVersion* : 1 <br/>
*digest*     : <br/>
*id*         : training_REST <br/>


<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/KecakMobile-DatalistAPI-Headers.png" alt="KecakMobile-DatalistAPI-Headers" />



If you have done all steps above, the server will responses by giving you JSONBody in **Response Body** which contains *appId*, *appVersion*, *digest*, *json*, and *status*.

```json
{
    "appVersion": "1",
    "appId": "eApproval_jo",
    "digest": "6c39ec81ba869018ce84f1107339fd75cb9deb80",
    "json": {
        "useSession": "false",
        "showPageSizeSelector": "true",
        "rowActions": [],
        "columns": [
            {
                "displayLabel": "Category No",
                "name": "category_no",
                "id": "column_0",
                "label": "Category No"
            },
            {
                "displayLabel": "Category Name",
                "name": "category_name",
                "id": "column_1",
                "label": "Category Name"
            }
        ],
        "pageSize": "0",
        "orderBy": "",
        "description": "",
        "filters": [
            {
                "name": "category_name",
                "id": "filter_0",
                "label": "Category Name"
            }
        ],
        "pageSizeSelectorOptions": "10,20,30,40,50,100",
        "buttonPosition": "bottomLeft",
        "checkboxPosition": "left",
        "name": "Master Category List",
        "id": "MasterCategoryList",
        "binder": {
            "className": "org.joget.apps.datalist.lib.FormRowDataListBinder",
            "properties": {
                "formDefId": "FormMasterCategory",
                "extraCondition": ""
            }
        },
        "actions": [],
        "order": ""
    },
    "status": "success"
}
```


### Version History ###

*  **1.0.0**
   * Initial creation

