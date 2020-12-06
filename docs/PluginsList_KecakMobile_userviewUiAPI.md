# Kecak Mobile - Userview UI API #

**REST API**

**Overview**

Userview UI - API is plugin to get JSON from certain Userview for authorized user. Server will responses by giving response body in JSON format which contains appId, appVersion, digested JSON Userview, JSON Userview itself, and status.

**Url**

```html
web/json/plugin/com.kinnara.kecakplugins.mobileapi.GetJsonUserviewApi/service
```

**Method**

HTTP GET

**Header**
* Authorization : *your access token*
* Referer : *your server host*
* Content-Type : *application/json*

**Parameters**
* appId - Application ID
* appVersion - Application Version (optional): if not included will retrieve the latest version
* id - Userview ID: Userview to be displayed
* digest - Digested JSON Userview


**Use of API Plugin**

This example is showing the JSON of Userview for authorized user, with the JSON of Userview on Kecak is as follows

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/kecakmobile-userviewUI.png" alt="KecakMobile-Userview" />

Then, you have to fill the authorization with your access token to authorize you as authorized user.


<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/KecakMobile-Userview2.PNG" alt="KecakMobile-Userview" />


And also, you have to fill the header section by **Referer** and **Content-Type**. For this example, **Referer** is `https://kecak.kinnarastudio.com` and **Content-Type** is **application/json**


<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/KecakMobile-Userview3.PNG" alt="KecakMobile-Userview" />


Request parameters are needed to filter the userview by its *appId*, *appVersion*, *id*, and *digest*. For this example request parameters is as follow :

*appId*      : triningREST <br/>
*appVersion* : 1 <br/>
*id*         : training_REST <br/>
*digest*     : <br/>

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/KecakMobile-Userview4.PNG" alt="KecakMobile-Userview" />


If you have done all steps above, the server will responses by giving you JSONBody in **Response Body** which contains *appId*, *userviewId*, *digest*, *json*, and *status*.

```json
{
    "appVersion": "1",
    "appId": "trainingREST",
    "digest": "5a62cb80ab9b403c5c55ae796b452c2c058dea34",
    "json": {
        "className": "org.joget.apps.userview.model.Userview",
        "categories": [
            {
                "className": "org.joget.apps.userview.model.UserviewCategory",
                "menus": [
                    {
                        "className": "org.joget.apps.userview.lib.HtmlPage",
                        "properties": {
                            "id": "3B6F25A638EF40FFAD3C5DAAC41BA98F",
                            "label": "Welcome",
                            "customId": "welcome",
                            "content": "<h1>Apps and Workflow Made Simple</h1>\n<p>&nbsp;</p>\n<p><strong>Build apps, not just processes!</strong></p>\n<p>Create full-fledged apps with support for data records management.</p>\n<p>&nbsp;</p>\n<p><strong>Increased Efficiency and Productivity</strong><br /><span>Faster and more consistent completion of manual processes, and with minimal errors.</span></p>\n<p>&nbsp;</p>\n<p><span><strong>Lowered Cost</strong><br /><span>Employees can be guided through complex procedures, hence reducing the cost of training.</span></span></p>\n<p><span><span><br /></span></span></p>"
                        }
                    },
                    {
                        "className": "org.joget.apps.userview.lib.RunProcess",
                        "properties": {
                            "messageShowAfterComplete": "",
                            "runProcessDirectly": "Yes",
                            "showInPopupDialog": "",
                            "fieldPassoverMethod": "append",
                            "keyName": "",
                            "id": "3C40D0A1E41445D0A3FB3AB3D704FB40",
                            "label": "Run Process",
                            "paramName": "",
                            "processDefId": "trainingREST",
                            "redirectUrlAfterComplete": "",
                            "fieldPassover": "",
                            "customId": ""
                        }
                    },
                    {
                        "className": "com.kecak.enterprise.UserviewProcessMonitoring",
                        "properties": {
                            "id": "35CD7D2DFFCD4BCBA1E25F842640E008",
                            "label": "Kecak - Process Monitoring"
                        }
                    },
                    {
                        "className": "com.kinnara.kecakplugins.crudmenu.CrudMenu",
                        "properties": {
                            "list-customFooter": "",
                            "add-afterSavedRedirectUrl": "",
                            "edit-customHeader": "",
                            "editFormId": "",
                            "edit-afterSavedRedirectParamName": "",
                            "edit-saveButtonLabel": "",
                            "list-showDeleteButton": "",
                            "list-newButtonLabel": "",
                            "edit-afterSavedRedirectUrl": "",
                            "edit-prevButtonLabel": "",
                            "add-afterSavedRedirectParamName": "",
                            "customId": "",
                            "edit-afterSaved": "list",
                            "list-deleteButtonLabel": "",
                            "checkboxPosition": "left",
                            "selectionType": "multiple",
                            "add-customFooter": "",
                            "addFormId": "",
                            "edit-allowRecordTraveling": "",
                            "id": "5E763DB9C66440D0814101EAE3D4EAB0",
                            "rowCount": "",
                            "add-messageShowAfterComplete": "",
                            "edit-readonlyLabel": "",
                            "list-editLinkLabel": "",
                            "add-cancelButtonLabel": "",
                            "edit-afterSavedRedirectParamvalue": "",
                            "edit-customFooter": "",
                            "keyName": "",
                            "label": "Kecak CRUD",
                            "add-customHeader": "",
                            "edit-lastButtonLabel": "",
                            "edit-readonly": "",
                            "buttonPosition": "bottomLeft",
                            "edit-firstButtonLabel": "",
                            "datalistId": "master_REST",
                            "add-saveButtonLabel": "",
                            "edit-nextButtonLabel": "",
                            "edit-messageShowAfterComplete": "",
                            "edit-moreActions": [],
                            "list-moreActions": [],
                            "add-afterSaved": "list",
                            "edit-backButtonLabel": "",
                            "add-afterSavedRedirectParamvalue": "",
                            "list-customHeader": ""
                        }
                    },
                    {
                        "className": "com.kinnara.kecakplugins.datalistinboxmenu.DataListInboxMenu",
                        "properties": {
                            "appFilter": "process",
                            "list-customFooter": "",
                            "assignment-customHeader": "",
                            "keyName": "",
                            "assignment-customFooter": "",
                            "label": "Data List Inbox",
                            "customId": "",
                            "datalistId": "master_REST",
                            "checkboxPosition": "left",
                            "selectionType": "multiple",
                            "processId": "trainingREST",
                            "id": "09136D9C243A4B8ABD29729FC6507D03",
                            "rowCount": "",
                            "list-customHeader": ""
                        }
                    }
                ],
                "properties": {
                    "hide": "",
                    "permission": {
                        "className": "",
                        "properties": {}
                    },
                    "id": "category-EE74E4F4426241BD9BC3BC73B1D24AC7",
                    "label": "<i class='icon-home'></i> Home"
                }
            }
        ],
        "properties": {
            "logoutText": "Logout",
            "welcomeMessage": "#date.EEE, d MMM yyyy#",
            "name": "Training REST",
            "description": "",
            "footerMessage": "Powered by Kecak Workflow",
            "id": "training_REST"
        },
        "setting": {
            "properties": {
                "mobileViewDisabled": "",
                "mobileViewLogoWidth": "",
                "mobileViewLogoHeight": "",
                "permission": {
                    "className": "",
                    "properties": {}
                },
                "mobileViewBackgroundColor": "",
                "mobileViewLogoAlign": "left",
                "loginPageTop": "",
                "userviewDescription": "",
                "mobileViewCustomCss": "",
                "mobileCacheEnabled": "",
                "loginPageBottom": "",
                "mobileViewBackgroundStyle": "repeat",
                "mobileViewTranslucent": "true",
                "theme": {
                    "className": "org.joget.plugin.enterprise.CorporatiTheme",
                    "properties": {
                        "customBanner": "",
                        "css": "",
                        "customHeader": "",
                        "cssUrl": "",
                        "collapsibleMenu": "",
                        "js": "",
                        "colorScheme": "silver"
                    }
                },
                "mobileLoginRequired": "",
                "mobileViewLogoUrl": "",
                "mobileViewBackgroundUrl": ""
            }
        }
    },
    "status": "success"
}
```


**Version History**

*  **1.0.0**
   * Initial creation

