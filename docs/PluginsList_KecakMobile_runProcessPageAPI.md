# Kecak Mobile-RunProcess Page- API #

##### Url #####
```html
web/json/plugin/com.kinnara.kecakplugins.mobileapi.GetJsonFormApi/service
```

##### Header #####
* Authorization : *your access token*
* Referer : *your server host*
* Content-Type : *application/json*

##### Parameters #####
* appId - Application ID
* processDefId - Process Definition ID
* digest - Digested JSON form
* appVersion - Application Version (optional): if not included will retrieve the latest version

##### Example #####
```html
https://kecak.kinnarastudio.com/web/json/plugin/com.kinnara.kecakplugins.mobileapi.GetJsonFormApi/service?appId=kecakMobileTest&processDefId=submitApp&digest=&appVersion=1
```

### Use of API Plugin ###
The example below shows then, you have to fill the authorization with your access token to authorize you as authorized user.
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/kecakMobile-RunProcessPageAPI-captures.PNG" alt="captures" />

And also, you have to fill the header section by **Accept**, **Referer** and **Content-Type**. For this example,**Accept** is **application/json** **Referer** is `https://kecak.kinnarastudio.com` and **Content-Type** is **application/json**
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/kecakMobile-RunProcessPageAPI-headers.PNG" alt="headers" />

Request parameters are needed to filter the form by its *appId*,*processDefId*,*digest* *appVersion*, and *digest*. For this example request parameters is as follow :

*appId*        : kecakMobileTest <br/>
*processDefId* : submitApp <br/>
*digest*       : <br/>
*appVersion*   : 1 <br/>

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/kecakMobile-RunProcessPageAPI-params.PNG" alt="params" />

If you have done all steps above, the server will responses by giving you JSONBody in **Response Body** which contains *appId*, *appVersion*, *digest*, *json*, and *status*.
  
```json
{
  "appVersion": "1",
  "appId": "kecakMobileTest",
  "digest": "f85dd9d1b0ab33d07a25bdb9705ab9e231c0511f",
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
                    "className": "",
                    "properties": {}
                  },
                  "workflowVariable": "",
                  "id": "textfield",
                  "label": "Text Field",
                  "value": "",
                  "readonlyLabel": ""
                }
              },
              {
                "elements": [],
                "className": "org.joget.apps.form.lib.TextArea",
                "properties": {
                  "readonly": "",
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
                  "id": "textarea",
                  "label": "Text Area",
                  "rows": "5",
                  "value": "",
                  "cols": "20",
                  "readonlyLabel": ""
                }
              },
              {
                "elements": [],
                "className": "org.joget.apps.form.lib.PasswordField",
                "properties": {
                  "size": "",
                  "readonly": "",
                  "maxlength": "",
                  "validator": {
                    "className": "",
                    "properties": {}
                  },
                  "id": "passwordfield",
                  "label": "Password Field",
                  "value": "",
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
                    "className": "",
                    "properties": {}
                  },
                  "label": "SelectBox",
                  "lazyLoading": "",
                  "messageLoadingMore": "Loading More...",
                  "removeDuplicates": "",
                  "messageErrorLoading": "Error Loading",
                  "size": "20",
                  "readonly": "",
                  "optionsBinder": {
                    "className": "",
                    "properties": {}
                  },
                  "messageSearching": "Searching...",
                  "options": [
                    {
                      "label": "Value 1",
                      "grouping": "",
                      "value": "value1"
                    },
                    {
                      "label": "Value 2",
                      "grouping": "",
                      "value": "value2"
                    },
                    {
                      "label": "Value 3",
                      "grouping": "",
                      "value": "value3"
                    }
                  ],
                  "workflowVariable": "",
                  "messageNoResults": "No Results Found",
                  "id": "field11",
                  "placeholder": "Please Select",
                  "value": "",
                  "readonlyLabel": "",
                  "modernStyle": "true"
                }
              },
              {
                "elements": [],
                "className": "org.joget.apps.form.lib.SelectBox",
                "properties": {
                  "controlField": "",
                  "multiple": "",
                  "validator": {
                    "className": "",
                    "properties": {}
                  },
                  "label": "SelectBox",
                  "lazyLoading": "",
                  "messageLoadingMore": "Loading More...",
                  "removeDuplicates": "",
                  "messageErrorLoading": "Error Loading",
                  "size": "20",
                  "readonly": "",
                  "optionsBinder": {
                    "className": "org.joget.apps.form.lib.FormOptionsBinder",
                    "properties": {
                      "addEmptyOption": "",
                      "groupingColumn": "",
                      "showIdInLabel": "",
                      "useAjax": "",
                      "formDefId": "daftarBuah",
                      "labelColumn": "jenis_buah",
                      "extraCondition": "",
                      "emptyLabel": "",
                      "idColumn": ""
                    }
                  },
                  "messageSearching": "Searching...",
                  "options": [
                    {
                      "label": "Buah naga",
                      "grouping": "",
                      "value": "50013783-ac1f2796-601ea872-a35573e9"
                    },
                    {
                      "label": "Test 10",
                      "grouping": "",
                      "value": "54ad8a08-ac1f2796-601ea872-2ae31283"
                    },
                    {
                      "label": "Test 2",
                      "grouping": "",
                      "value": "53aa756f-ac1f2796-601ea872-68ec71bb"
                    },
                    {
                      "label": "Test 4",
                      "grouping": "",
                      "value": "53ab9d66-ac1f2796-601ea872-55b4ce93"
                    },
                    {
                      "label": "Test 6",
                      "grouping": "",
                      "value": "53ac8d14-ac1f2796-601ea872-667e86fd"
                    },
                    {
                      "label": "Test 9",
                      "grouping": "",
                      "value": "54ad2423-ac1f2796-601ea872-8781c641"
                    }
                  ],
                  "workflowVariable": "",
                  "messageNoResults": "No Results Found",
                  "id": "field10",
                  "placeholder": "Please Select",
                  "value": "",
                  "readonlyLabel": "",
                  "modernStyle": "true"
                }
              },
              {
                "elements": [],
                "className": "org.joget.apps.form.lib.SelectBox",
                "properties": {
                  "controlField": "",
                  "multiple": "true",
                  "validator": {
                    "className": "",
                    "properties": {}
                  },
                  "label": "Select Box",
                  "lazyLoading": "",
                  "messageLoadingMore": "Loading More...",
                  "removeDuplicates": "",
                  "messageErrorLoading": "Error Loading",
                  "size": "20",
                  "readonly": "",
                  "optionsBinder": {
                    "className": "org.joget.apps.form.lib.FormOptionsBinder",
                    "properties": {
                      "addEmptyOption": "",
                      "groupingColumn": "",
                      "showIdInLabel": "",
                      "useAjax": "",
                      "formDefId": "daftarBuah",
                      "labelColumn": "jenis_buah",
                      "extraCondition": "",
                      "emptyLabel": "",
                      "idColumn": ""
                    }
                  },
                  "messageSearching": "Searching...",
                  "options": [
                    {
                      "label": "Buah naga",
                      "grouping": "",
                      "value": "50013783-ac1f2796-601ea872-a35573e9"
                    },
                    {
                      "label": "Test 10",
                      "grouping": "",
                      "value": "54ad8a08-ac1f2796-601ea872-2ae31283"
                    },
                    {
                      "label": "Test 2",
                      "grouping": "",
                      "value": "53aa756f-ac1f2796-601ea872-68ec71bb"
                    },
                    {
                      "label": "Test 4",
                      "grouping": "",
                      "value": "53ab9d66-ac1f2796-601ea872-55b4ce93"
                    },
                    {
                      "label": "Test 6",
                      "grouping": "",
                      "value": "53ac8d14-ac1f2796-601ea872-667e86fd"
                    },
                    {
                      "label": "Test 9",
                      "grouping": "",
                      "value": "54ad2423-ac1f2796-601ea872-8781c641"
                    }
                  ],
                  "workflowVariable": "",
                  "messageNoResults": "No Results Found",
                  "id": "selectbox",
                  "placeholder": "Please Select",
                  "value": "",
                  "readonlyLabel": "",
                  "modernStyle": "true"
                }
              },
              {
                "elements": [],
                "className": "org.joget.apps.form.lib.CheckBox",
                "properties": {
                  "controlField": "",
                  "readonly": "",
                  "optionsBinder": {
                    "className": "org.joget.apps.form.lib.FormOptionsBinder",
                    "properties": {
                      "addEmptyOption": "",
                      "groupingColumn": "",
                      "showIdInLabel": "",
                      "useAjax": "",
                      "formDefId": "daftarBuah",
                      "labelColumn": "jenis_buah",
                      "extraCondition": "",
                      "emptyLabel": "",
                      "idColumn": ""
                    }
                  },
                  "options": [
                    {
                      "label": "Buah naga",
                      "grouping": "",
                      "value": "50013783-ac1f2796-601ea872-a35573e9"
                    },
                    {
                      "label": "Test 10",
                      "grouping": "",
                      "value": "54ad8a08-ac1f2796-601ea872-2ae31283"
                    },
                    {
                      "label": "Test 2",
                      "grouping": "",
                      "value": "53aa756f-ac1f2796-601ea872-68ec71bb"
                    },
                    {
                      "label": "Test 4",
                      "grouping": "",
                      "value": "53ab9d66-ac1f2796-601ea872-55b4ce93"
                    },
                    {
                      "label": "Test 6",
                      "grouping": "",
                      "value": "53ac8d14-ac1f2796-601ea872-667e86fd"
                    },
                    {
                      "label": "Test 9",
                      "grouping": "",
                      "value": "54ad2423-ac1f2796-601ea872-8781c641"
                    }
                  ],
                  "validator": {
                    "className": "",
                    "properties": {}
                  },
                  "workflowVariable": "",
                  "id": "checkbox",
                  "label": "Check Box",
                  "value": "",
                  "readonlyLabel": ""
                }
              },
              {
                "elements": [],
                "className": "org.joget.apps.form.lib.Radio",
                "properties": {
                  "controlField": "",
                  "readonly": "",
                  "optionsBinder": {
                    "className": "org.joget.apps.form.lib.FormOptionsBinder",
                    "properties": {
                      "addEmptyOption": "",
                      "groupingColumn": "",
                      "showIdInLabel": "",
                      "useAjax": "",
                      "formDefId": "daftarBuah",
                      "labelColumn": "jenis_buah",
                      "extraCondition": "",
                      "emptyLabel": "",
                      "idColumn": ""
                    }
                  },
                  "options": [
                    {
                      "label": "Buah naga",
                      "grouping": "",
                      "value": "50013783-ac1f2796-601ea872-a35573e9"
                    },
                    {
                      "label": "Test 10",
                      "grouping": "",
                      "value": "54ad8a08-ac1f2796-601ea872-2ae31283"
                    },
                    {
                      "label": "Test 2",
                      "grouping": "",
                      "value": "53aa756f-ac1f2796-601ea872-68ec71bb"
                    },
                    {
                      "label": "Test 4",
                      "grouping": "",
                      "value": "53ab9d66-ac1f2796-601ea872-55b4ce93"
                    },
                    {
                      "label": "Test 6",
                      "grouping": "",
                      "value": "53ac8d14-ac1f2796-601ea872-667e86fd"
                    },
                    {
                      "label": "Test 9",
                      "grouping": "",
                      "value": "54ad2423-ac1f2796-601ea872-8781c641"
                    }
                  ],
                  "validator": {
                    "className": "",
                    "properties": {}
                  },
                  "workflowVariable": "",
                  "id": "radiobutton",
                  "label": "Radio",
                  "value": "",
                  "readonlyLabel": ""
                }
              },
              {
                "elements": [],
                "className": "org.joget.apps.form.lib.DatePicker",
                "properties": {
                  "yearRange": "c-10:c+10",
                  "dataFormat": "",
                  "startDateFieldId": "",
                  "validator": {
                    "className": "",
                    "properties": {}
                  },
                  "format": "",
                  "label": "Date Picker",
                  "currentDateAs": "",
                  "endDateFieldId": "",
                  "readonly": "",
                  "allowManual": "",
                  "workflowVariable": "",
                  "id": "datepicker",
                  "value": "",
                  "readonlyLabel": ""
                }
              },
              {
                "elements": [],
                "className": "org.joget.apps.form.lib.FileUpload",
                "properties": {
                  "permissionType": "",
                  "maxSizeMsg": "File size limit exceeded",
                  "validator": {
                    "className": "",
                    "properties": {}
                  },
                  "multiple": "",
                  "maxSize": "",
                  "label": "File Upload",
                  "size": "",
                  "readonly": "",
                  "attachment": "",
                  "fileTypeMsg": "Invalid file type",
                  "id": "fieldupload",
                  "fileType": ""
                }
              },
              {
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
                                "className": "",
                                "properties": {}
                              },
                              "workflowVariable": "",
                              "id": "jenis_buah",
                              "label": "Jenis Buah",
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
                      "visibilityControl": "",
                      "regex": "",
                      "loadBinder": {
                        "className": "",
                        "properties": {}
                      },
                      "permission": {
                        "className": "",
                        "properties": {}
                      },
                      "id": "section1",
                      "label": "Master Jenis Buah",
                      "storeBinder": {
                        "className": "",
                        "properties": {}
                      },
                      "visibilityValue": ""
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
                  "formDefId": "daftarBuah",
                  "noframe": "",
                  "subFormParentId": "",
                  "id": "subform",
                  "label": "",
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
          "visibilityControl": "",
          "regex": "",
          "loadBinder": {
            "className": "org.joget.apps.form.lib.WorkflowFormBinder",
            "properties": {}
          },
          "permission": {
            "className": "",
            "properties": {}
          },
          "id": "section1",
          "label": "Submit Form",
          "storeBinder": {
            "className": "org.joget.apps.form.lib.WorkflowFormBinder",
            "properties": {}
          },
          "visibilityValue": ""
        }
      }
    ],
    "className": "org.joget.apps.form.model.Form",
    "properties": {
      "noPermissionMessage": "",
      "description": "",
      "permission": {
        "className": "",
        "properties": {}
      },
      "storeBinder": {
        "className": "org.joget.apps.form.lib.WorkflowFormBinder",
        "properties": {}
      },
      "tableName": "m_submit",
      "loadBinder": {
        "className": "org.joget.apps.form.lib.WorkflowFormBinder",
        "properties": {}
      },
      "preProcessor": {
        "className": "",
        "properties": {}
      },
      "name": "Submit Form",
      "postProcessorRunOn": "both",
      "customAssignmentCompleteButton": {
        "className": "org.joget.apps.form.lib.SaveAsDraftButton",
        "properties": {}
      },
      "id": "submitForm",
      "postProcessor": {
        "className": "",
        "properties": {}
      }
    }
  },
  "status": "success"
}
```

### Version History ###

*  **2.0.0**

