# Kecak Mobile - Inbox Page API #

## Overview ##

Inbox API is plugin to view all data in certain Inbox menu from Userview, and when the data on the application is updated, then the data on Inbox API also changed.

On this plugin can be set in accordance with our desire to display any data that will be inserted into Inbox API.

##### Url #####

`/web/json/plugin/com.kinnara.kecakplugins.mobileapi.InboxApi/service`

##### Method #####
HTTP GET

##### Header #####
* Authorization : *your access token*
* Referer : *your server host*
* Content-Type : *application/json*

##### Parameters #####
* appId - Application ID
* appVersion - Application Version (optional): if not included will retrieve the latest version
* activityId - Activity ID (optional): Activity ID to be filtered
* processId - Process ID (optional): Process ID to be filtered

##### Example #####
```json
https://kecak.kinnarastudio.com/web/json/plugin/com.kinnara.kecakplugins.mobileapi.InboxApi/service?appId=trainingREST&appVersion=1`
```

## Use of Inbox ##
This example is showing the Inbox for authorized user from one of Kecak app, with the Inbox we want to get is as follow :


<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/KecakMobile-InboxPageAPI-Inbox_on_userview.PNG" alt="KecakMobile-InboxPageAPI-Inbox_on_userview" />


Then, you have to fill the authorization with your access token to authorize you as authorized user.

And also, you have to fill the header section by **Referer** and **Content-Type**. For this example, **Referer** is `https://kecak.kinnarastudio.com` and **Content-Type** is **application/json**

Request parameters are needed for the Inbox are *appId* and *appVersion*. For this example request parameter is as follow :

*appId*      : *trainingREST* <br/>
*appVersion* : *1* <br/>

so the Inbox API is as follows:
```javascript
[
    {
        "processRequesterId": null,
        "activityId": "2274_1444_trainingREST_trainingREST_trainingREST",
        "dateCreated": "06-08-2018 06:07 PM",
        "serviceLevelMonitor": "<span class=\"dot_red\">&nbsp;</span>",
        "processId": "1444_trainingREST_trainingREST",
        "processName": "Training REST",
        "acceptedStatus": false,
        "dueDate": "-",
        "activityName": "Training REST",
        "processVersion": "5"
    },
    {
        "processRequesterId": null,
        "activityId": "2273_1443_trainingREST_trainingREST_trainingREST",
        "dateCreated": "06-08-2018 06:07 PM",
        "serviceLevelMonitor": "<span class=\"dot_red\">&nbsp;</span>",
        "processId": "1443_trainingREST_trainingREST",
        "processName": "Training REST",
        "acceptedStatus": false,
        "dueDate": "-",
        "activityName": "Training REST",
        "processVersion": "5"
    },
    {
        "processRequesterId": null,
        "activityId": "2272_1442_trainingREST_trainingREST_trainingREST2",
        "dateCreated": "06-08-2018 06:07 PM",
        "serviceLevelMonitor": "<span class=\"dot_red\">&nbsp;</span>",
        "processId": "1442_trainingREST_trainingREST",
        "processName": "Training REST",
        "acceptedStatus": false,
        "dueDate": "-",
        "activityName": "Training REST 2",
        "processVersion": "5"
    },
    {
        "processRequesterId": null,
        "activityId": "2271_1441_trainingREST_trainingREST_trainingREST",
        "dateCreated": "06-08-2018 06:07 PM",
        "serviceLevelMonitor": "<span class=\"dot_red\">&nbsp;</span>",
        "processId": "1441_trainingREST_trainingREST",
        "processName": "Training REST",
        "acceptedStatus": false,
        "dueDate": "-",
        "activityName": "Training REST",
        "processVersion": "5"
    },
    {
        "processRequesterId": null,
        "activityId": "2270_1440_trainingREST_trainingREST_trainingREST",
        "dateCreated": "06-08-2018 06:07 PM",
        "serviceLevelMonitor": "<span class=\"dot_red\">&nbsp;</span>",
        "processId": "1440_trainingREST_trainingREST",
        "processName": "Training REST",
        "acceptedStatus": false,
        "dueDate": "-",
        "activityName": "Training REST",
        "processVersion": "5"
    },
    {
        "processRequesterId": null,
        "activityId": "2269_1439_trainingREST_trainingREST_trainingREST",
        "dateCreated": "06-08-2018 06:07 PM",
        "serviceLevelMonitor": "<span class=\"dot_red\">&nbsp;</span>",
        "processId": "1439_trainingREST_trainingREST",
        "processName": "Training REST",
        "acceptedStatus": false,
        "dueDate": "-",
        "activityName": "Training REST",
        "processVersion": "5"
    },
    {
        "processRequesterId": null,
        "activityId": "2268_1438_trainingREST_trainingREST_trainingREST",
        "dateCreated": "06-08-2018 06:07 PM",
        "serviceLevelMonitor": "<span class=\"dot_red\">&nbsp;</span>",
        "processId": "1438_trainingREST_trainingREST",
        "processName": "Training REST",
        "acceptedStatus": false,
        "dueDate": "-",
        "activityName": "Training REST",
        "processVersion": "5"
    },
    {
        "processRequesterId": null,
        "activityId": "2267_1437_trainingREST_trainingREST_trainingREST",
        "dateCreated": "06-08-2018 06:07 PM",
        "serviceLevelMonitor": "<span class=\"dot_red\">&nbsp;</span>",
        "processId": "1437_trainingREST_trainingREST",
        "processName": "Training REST",
        "acceptedStatus": false,
        "dueDate": "-",
        "activityName": "Training REST",
        "processVersion": "5"
    },
    {
        "processRequesterId": null,
        "activityId": "2266_1436_trainingREST_trainingREST_trainingREST",
        "dateCreated": "06-08-2018 06:07 PM",
        "serviceLevelMonitor": "<span class=\"dot_red\">&nbsp;</span>",
        "processId": "1436_trainingREST_trainingREST",
        "processName": "Training REST",
        "acceptedStatus": false,
        "dueDate": "-",
        "activityName": "Training REST",
        "processVersion": "5"
    },
    {
        "processRequesterId": null,
        "activityId": "2265_1435_trainingREST_trainingREST_trainingREST",
        "dateCreated": "06-08-2018 06:07 PM",
        "serviceLevelMonitor": "<span class=\"dot_red\">&nbsp;</span>",
        "processId": "1435_trainingREST_trainingREST",
        "processName": "Training REST",
        "acceptedStatus": false,
        "dueDate": "-",
        "activityName": "Training REST",
        "processVersion": "5"
    },
    {
        "processRequesterId": null,
        "activityId": "2287_1457_trainingREST_testdua_activity1",
        "dateCreated": "06-08-2018 06:07 PM",
        "serviceLevelMonitor": "<span class=\"dot_red\">&nbsp;</span>",
        "processId": "1457_trainingREST_testdua",
        "processName": "Test Dua",
        "acceptedStatus": false,
        "dueDate": "-",
        "activityName": "Activity 1",
        "processVersion": "5"
    },
    {
        "processRequesterId": null,
        "activityId": "2282_1452_trainingREST_testdua_activity1",
        "dateCreated": "06-08-2018 06:07 PM",
        "serviceLevelMonitor": "<span class=\"dot_red\">&nbsp;</span>",
        "processId": "1452_trainingREST_testdua",
        "processName": "Test Dua",
        "acceptedStatus": false,
        "dueDate": "-",
        "activityName": "Activity 1",
        "processVersion": "5"
    },
    {
        "processRequesterId": null,
        "activityId": "2281_1451_trainingREST_testdua_activity1",
        "dateCreated": "06-08-2018 06:07 PM",
        "serviceLevelMonitor": "<span class=\"dot_red\">&nbsp;</span>",
        "processId": "1451_trainingREST_testdua",
        "processName": "Test Dua",
        "acceptedStatus": false,
        "dueDate": "-",
        "activityName": "Activity 1",
        "processVersion": "5"
    },
    {
        "processRequesterId": null,
        "activityId": "2280_1450_trainingREST_testdua_activity1",
        "dateCreated": "06-08-2018 06:07 PM",
        "serviceLevelMonitor": "<span class=\"dot_red\">&nbsp;</span>",
        "processId": "1450_trainingREST_testdua",
        "processName": "Test Dua",
        "acceptedStatus": false,
        "dueDate": "-",
        "activityName": "Activity 1",
        "processVersion": "5"
    },
    {
        "processRequesterId": null,
        "activityId": "2279_1449_trainingREST_testdua_activity1",
        "dateCreated": "06-08-2018 06:07 PM",
        "serviceLevelMonitor": "<span class=\"dot_red\">&nbsp;</span>",
        "processId": "1449_trainingREST_testdua",
        "processName": "Test Dua",
        "acceptedStatus": false,
        "dueDate": "-",
        "activityName": "Activity 1",
        "processVersion": "5"
    },
    {
        "processRequesterId": null,
        "activityId": "2278_1448_trainingREST_testdua_activity1",
        "dateCreated": "06-08-2018 06:07 PM",
        "serviceLevelMonitor": "<span class=\"dot_red\">&nbsp;</span>",
        "processId": "1448_trainingREST_testdua",
        "processName": "Test Dua",
        "acceptedStatus": false,
        "dueDate": "-",
        "activityName": "Activity 1",
        "processVersion": "5"
    },
    {
        "processRequesterId": null,
        "activityId": "2277_1447_trainingREST_testdua_activity1",
        "dateCreated": "06-08-2018 06:07 PM",
        "serviceLevelMonitor": "<span class=\"dot_red\">&nbsp;</span>",
        "processId": "1447_trainingREST_testdua",
        "processName": "Test Dua",
        "acceptedStatus": false,
        "dueDate": "-",
        "activityName": "Activity 1",
        "processVersion": "5"
    },
    {
        "processRequesterId": null,
        "activityId": "2276_1446_trainingREST_testdua_activity1",
        "dateCreated": "06-08-2018 06:07 PM",
        "serviceLevelMonitor": "<span class=\"dot_red\">&nbsp;</span>",
        "processId": "1446_trainingREST_testdua",
        "processName": "Test Dua",
        "acceptedStatus": false,
        "dueDate": "-",
        "activityName": "Activity 1",
        "processVersion": "5"
    },
    {
        "processRequesterId": null,
        "activityId": "2275_1445_trainingREST_testdua_activity1",
        "dateCreated": "06-08-2018 06:07 PM",
        "serviceLevelMonitor": "<span class=\"dot_red\">&nbsp;</span>",
        "processId": "1445_trainingREST_testdua",
        "processName": "Test Dua",
        "acceptedStatus": false,
        "dueDate": "-",
        "activityName": "Activity 1",
        "processVersion": "5"
    }
]
```

## Use of Inbox Using Filter ##

### How to use Filter ###

with the Inbox API, add the **activityId** or **processId** parameter as a filter:
`https://kecak.kinnarastudio.com/web/json/plugin/com.kinnara.kecakplugins.mobileapi.InboxApi/service?appId=trainingREST&appVersion=1&activityId=2274_1444_trainingREST_trainingREST_trainingREST&processId=1444_trainingREST_trainingREST`

it will shows :
``` javascript
[
    {
        "processRequesterId": null,
        "activityId": "2274_1444_trainingREST_trainingREST_trainingREST",
        "dateCreated": "06-08-2018 06:07 PM",
        "serviceLevelMonitor": "<span class=\"dot_red\">&nbsp;</span>",
        "processId": "1444_trainingREST_trainingREST",
        "processName": "Training REST",
        "acceptedStatus": false,
        "dueDate": "-",
        "activityName": "Training REST",
        "processVersion": "5"
    }
]
```



### Version History ###

*  **1.0.0**
   * Initial creation
