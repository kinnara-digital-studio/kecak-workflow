# Kecak Mobile - Form UI - API #

## REST API ##

### Overview ###

Form UI - API is plugin to get JSON from certain Form and also its *children*, like *subform* etc, for authorized user. Server will responses by giving response body in JSON format which contains *appId*, *appVersion*, digested JSON Form, JSON Form itself, and status.

##### Url #####

`web/json/plugin/com.kinnara.kecakplugins.mobileapi.GetJsonFormApi/service`

##### Method #####
HTTP GET

##### Header #####
* Authorization : *your access token*
* Referer : *your server host*
* Content-Type : *application/json*

##### Parameters #####
* appId - Application ID
* appVersion - Application Version (optional): if not included will retrieve the latest version
* id - Form ID: Form to be displayed
* digest - Digested JSON form

##### Example #####
`https://kecak.kinnarastudio.com/web/json/plugin/com.kinnara.kecakplugins.mobileapi.GetJsonFormApi/service?appId=trainingREST&appVersion=1&id=masterREST&digest=`

### Use of API Plugin ###

This example is showing the JSON of Form for authorized user, with the JSON of Form on Kecak is as follows


![Form](/uploads/a23c1c8bfd2e199e4569144fbac92e8d/Form.PNG)


Then, you have to fill the authorization with your access token to authorize you as authorized user.


![Authorization](/uploads/465c5706028891d5f663b8846842c88d/Authorization.PNG)


And also, you have to fill the header section by **Referer** and **Content-Type**. For this example, **Referer** is `https://kecak.kinnarastudio.com` and **Content-Type** is **application/json**


![Headers](/uploads/5ea98f2307847e8c070c1cce5b8f0c95/Headers.PNG)


Request parameters are needed to filter the form by its *appId*, *appVersion*, *id*, and *digest*. For this example request parameters is as follow :

*appId*      : triningREST <br/>
*appVersion* : 1 <br/>
*id*         : testDuaForm1 <br/>
*digest*     : <br/>

![Request_Parameters](/uploads/c7ec46ad4f2da54589bce4b627694711/Request_Parameters.PNG)


If you have done all steps above, the server will responses by giving you JSONBody in **Response Body** which contains *appId*, *appVersion*, *digest*, *json*, and *status*.

```json
{
    "appVersion": "1",
    "appId": "trainingREST",
    "digest": "87ef115ef6b29899d663556a9b07e4fe49f0e3eb",
    "json": {
        "elements": [
            {
                "elements": [
                    {
                        "elements": [
                            {
                                "elements": [],
                                "className": "org.joget.apps.form.lib.TextField",
                                "properties": {
                                    "encryption": "",
                                    "size": "",
                                    "readonly": "",
                                    "maxlength": "",
                                    "validator": {
                                        "className": "org.joget.apps.form.lib.DefaultValidator",
                                        "properties": {
                                            "custom-regex": "",
                                            "type": "",
                                            "message": "",
                                            "mandatory": "true"
                                        }
                                    },
                                    "workflowVariable": "",
                                    "id": "nametest",
                                    "label": "Name Test",
                                    "value": "",
                                    "readonlyLabel": ""
                                }
                            },
                            {
                                "elements": [],
                                "className": "org.joget.apps.form.lib.FileUpload",
                                "properties": {
                                    "permissionType": "custom",
                                    "maxSizeMsg": "File size limit exceeded",
                                    "validator": {
                                        "className": "",
                                        "properties": {}
                                    },
                                    "multiple": "true",
                                    "maxSize": "",
                                    "label": "FileUpload",
                                    "size": "",
                                    "readonly": "",
                                    "attachment": "true",
                                    "permissionPlugin": {
                                        "className": "com.kinnara.kecakplugins.permissions.IsAnonymousPermission",
                                        "properties": {}
                                    },
                                    "fileTypeMsg": "Invalid file type",
                                    "id": "fileUpload",
                                    "fileType": ""
                                }
                            },
                            {
                                "elements": [],
                                "className": "org.joget.apps.form.lib.Grid",
                                "properties": {
                                    "validateMaxRow": "",
                                    "readonly": "",
                                    "loadBinder": {
                                        "className": "com.kecak.kecakplugins.formgrid.MultirowFormBinder",
                                        "properties": {
                                            "formDefId": "testDuaFormGrid",
                                            "extraCondition": [],
                                            "foreignKey": "testId"
                                        }
                                    },
                                    "validateMinRow": "",
                                    "options": [
                                        {
                                            "label": "Peserta",
                                            "value": "peserta"
                                        }
                                    ],
                                    "validator": {
                                        "className": "",
                                        "properties": {}
                                    },
                                    "errorMessage": "Invalid number of rows",
                                    "id": "gridBiasa",
                                    "label": "Grid",
                                    "storeBinder": {
                                        "className": "com.kecak.kecakplugins.formgrid.MultirowFormBinder",
                                        "properties": {
                                            "formDefId": "testDuaFormGrid",
                                            "extraCondition": [],
                                            "foreignKey": "testId"
                                        }
                                    }
                                }
                            },
                            {
                                "elements": [],
                                "className": "com.kecak.kecakplugins.formgrid.FormGrid",
                                "properties": {
                                    "enhancementStoreBinder": {
                                        "className": "",
                                        "properties": {}
                                    },
                                    "validateMaxRow": "",
                                    "notes": "",
                                    "formDefId": "testDuaFormGrid",
                                    "validator": {
                                        "className": "",
                                        "properties": {}
                                    },
                                    "enableSorting": "",
                                    "storeBinder": {
                                        "className": "com.kecak.kecakplugins.formgrid.MultirowFormBinder",
                                        "properties": {
                                            "formDefId": "testDuaFormGrid",
                                            "extraCondition": [],
                                            "foreignKey": "testId"
                                        }
                                    },
                                    "readonly": "",
                                    "options": [
                                        {
                                            "summary": "none",
                                            "width": "",
                                            "format": "",
                                            "label": "Peserta",
                                            "formatType": "text",
                                            "alignment": "left",
                                            "value": "peserta"
                                        }
                                    ],
                                    "sortField": "sort",
                                    "enhancementLoadBinder": {
                                        "className": "",
                                        "properties": {}
                                    },
                                    "defaultValues": [],
                                    "id": "grid_peserta",
                                    "height": "500",
                                    "hideHeader": "",
                                    "showRowNumber": "",
                                    "disabledDelete": "",
                                    "validateMinRow": "",
                                    "submit-label-normal": "",
                                    "uniqueKey": "",
                                    "errorMessage": "Invalid Number of Row",
                                    "deleteMessage": "Delete row?",
                                    "label": "Peserta",
                                    "loadBinder": {
                                        "className": "com.kecak.kecakplugins.formgrid.MultirowFormBinder",
                                        "properties": {
                                            "formDefId": "testDuaFormGrid",
                                            "extraCondition": [],
                                            "foreignKey": "testId"
                                        }
                                    },
                                    "disabledAdd": "",
                                    "submit-label-readonly": "",
                                    "width": "900",
                                    "javascriptEnhancementOnFormLoad": "function(grid, button) { /*code here*/ }",
                                    "readonlyLabel": ""
                                }
                            },
                            {
                                "elements": [],
                                "className": "org.joget.apps.form.lib.SelectBox",
                                "properties": {
                                    "controlField": "",
                                    "multiple": "",
                                    "validator": {
                                        "className": "org.joget.apps.form.lib.DefaultValidator",
                                        "properties": {
                                            "custom-regex": "",
                                            "type": "",
                                            "message": "",
                                            "mandatory": "true"
                                        }
                                    },
                                    "label": "SelectBox",
                                    "removeDuplicates": "",
                                    "size": "20",
                                    "readonly": "",
                                    "optionsBinder": {
                                        "className": "com.kecakplugins.directoryoptionsbinder.DepartmentOptionsBinder",
                                        "properties": {
                                            "addEmptyOption": "",
                                            "emptyLabel": "",
                                            "orgId": "bca_insurance"
                                        }
                                    },
                                    "options": [
                                        {
                                            "label": "Finance Department",
                                            "grouping": "bca_insurance",
                                            "value": "Finance"
                                        },
                                        {
                                            "label": "IT Development",
                                            "grouping": "bca_insurance",
                                            "value": "ITDevelopment"
                                        }
                                    ],
                                    "workflowVariable": "",
                                    "id": "selectBox",
                                    "value": "",
                                    "readonlyLabel": "",
                                    "modernStyle": "true"
                                }
                            }
                        ],
                        "className": "org.joget.apps.form.model.Column",
                        "properties": {
                            "width": "49%"
                        }
                    },
                    {
                        "elements": [
                            {
                                "elements": [
                                    {
                                        "elements": [
                                            {
                                                "elements": [
                                                    {
                                                        "elements": [],
                                                        "className": "org.joget.apps.form.lib.HiddenField",
                                                        "properties": {
                                                            "useDefaultWhenEmpty": "",
                                                            "workflowVariable": "",
                                                            "id": "testId",
                                                            "value": ""
                                                        }
                                                    },
                                                    {
                                                        "elements": [],
                                                        "className": "org.joget.apps.form.lib.TextField",
                                                        "properties": {
                                                            "encryption": "",
                                                            "size": "",
                                                            "readonly": "",
                                                            "maxlength": "",
                                                            "validator": {
                                                                "className": "",
                                                                "properties": {}
                                                            },
                                                            "workflowVariable": "",
                                                            "id": "peserta",
                                                            "label": "Peserta",
                                                            "value": "",
                                                            "readonlyLabel": ""
                                                        }
                                                    }
                                                ],
                                                "className": "org.joget.apps.form.model.Column",
                                                "properties": {
                                                    "width": "100%"
                                                }
                                            }
                                        ],
                                        "className": "org.joget.apps.form.model.Section",
                                        "properties": {
                                            "label": "Section",
                                            "id": "section1"
                                        }
                                    }
                                ],
                                "className": "org.joget.apps.form.lib.SubForm",
                                "properties": {
                                    "parentSubFormId": "",
                                    "readonly": "",
                                    "loadBinder": {
                                        "className": "org.joget.apps.form.lib.WorkflowFormBinder",
                                        "properties": {}
                                    },
                                    "formDefId": "testDuaFormGrid",
                                    "noframe": "",
                                    "subFormParentId": "",
                                    "id": "subformkolombaru",
                                    "label": "SubForm",
                                    "storeBinder": {
                                        "className": "org.joget.apps.form.lib.WorkflowFormBinder",
                                        "properties": {}
                                    },
                                    "readonlyLabel": ""
                                }
                            },
                            {
                                "elements": [],
                                "className": "org.joget.apps.form.lib.TextField",
                                "properties": {
                                    "encryption": "",
                                    "size": "",
                                    "readonly": "",
                                    "maxlength": "",
                                    "validator": {
                                        "className": "org.joget.apps.form.lib.DefaultValidator",
                                        "properties": {
                                            "custom-regex": "",
                                            "type": "",
                                            "message": "",
                                            "mandatory": "true"
                                        }
                                    },
                                    "workflowVariable": "",
                                    "id": "kolombaru",
                                    "label": "nama kolom baru",
                                    "value": "",
                                    "readonlyLabel": ""
                                }
                            }
                        ],
                        "className": "org.joget.apps.form.model.Column",
                        "properties": {
                            "width": "49%"
                        }
                    }
                ],
                "className": "org.joget.apps.form.model.Section",
                "properties": {
                    "label": "Section",
                    "id": "section1"
                }
            },
            {
                "elements": [
                    {
                        "elements": [
                            {
                                "elements": [
                                    {
                                        "elements": [
                                            {
                                                "elements": [
                                                    {
                                                        "elements": [],
                                                        "className": "org.joget.apps.form.lib.HiddenField",
                                                        "properties": {
                                                            "useDefaultWhenEmpty": "",
                                                            "workflowVariable": "",
                                                            "id": "testId",
                                                            "value": ""
                                                        }
                                                    },
                                                    {
                                                        "elements": [],
                                                        "className": "org.joget.apps.form.lib.TextField",
                                                        "properties": {
                                                            "encryption": "",
                                                            "size": "",
                                                            "readonly": "",
                                                            "maxlength": "",
                                                            "validator": {
                                                                "className": "",
                                                                "properties": {}
                                                            },
                                                            "workflowVariable": "",
                                                            "id": "peserta",
                                                            "label": "Peserta",
                                                            "value": "",
                                                            "readonlyLabel": ""
                                                        }
                                                    }
                                                ],
                                                "className": "org.joget.apps.form.model.Column",
                                                "properties": {
                                                    "width": "100%"
                                                }
                                            }
                                        ],
                                        "className": "org.joget.apps.form.model.Section",
                                        "properties": {
                                            "label": "Section",
                                            "id": "section1"
                                        }
                                    }
                                ],
                                "className": "org.joget.apps.form.lib.SubForm",
                                "properties": {
                                    "parentSubFormId": "",
                                    "readonly": "",
                                    "loadBinder": {
                                        "className": "org.joget.apps.form.lib.WorkflowFormBinder",
                                        "properties": {}
                                    },
                                    "formDefId": "testDuaFormGrid",
                                    "noframe": "",
                                    "subFormParentId": "",
                                    "id": "subForm",
                                    "label": "SubForm",
                                    "storeBinder": {
                                        "className": "org.joget.apps.form.lib.WorkflowFormBinder",
                                        "properties": {}
                                    },
                                    "readonlyLabel": ""
                                }
                            }
                        ],
                        "className": "org.joget.apps.form.model.Column",
                        "properties": {
                            "width": "100%"
                        }
                    }
                ],
                "className": "org.joget.apps.form.model.Section",
                "properties": {
                    "label": "Section",
                    "id": "section2"
                }
            }
        ],
        "className": "org.joget.apps.form.model.Form",
        "properties": {
            "loadBinder": {
                "className": "org.joget.apps.form.lib.WorkflowFormBinder",
                "properties": {}
            },
            "name": "Test Dua Form 1",
            "description": "",
            "id": "testDuaForm1",
            "storeBinder": {
                "className": "org.joget.apps.form.lib.WorkflowFormBinder",
                "properties": {}
            },
            "tableName": "test_dua"
        }
    },
    "status": "success"
}
```


### Version History ###

*  **2.0.0**

