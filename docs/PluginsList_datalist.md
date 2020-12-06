## Overview ##

Datalist API is useful to see what data list is there, and when the data list on the application is updated, then the data on the datalist API also changed.

on this datalist can be set in accordance with our desire to display any data that will be inserted into the rest API.

##### Url #####

`/web/json/plugin/com.kinnara.kecakplugins.datalistapi.DataListLoaderApi/service`

##### Method #####
HTTP GET

##### Header #####
* Referer : *your server host*
* masterUsername : *master username*
* masterHash : *master hash password*

##### Parameters #####
* appId - Application ID
* appVersion - Application Version (optional): if not included will retrieve the latest version
* dataListId - DataList ID: data list to be displayed
* page - page number (optional): each page contains 10 data, if not included will display all data
* *filter* - *filter* (optional): this parameter depends on **DataList Filter** which is included when creating dataList

##### Example #####
`http://kecak.kinnarastudio.com:8080/web/json/plugin/com.kinnara.kecakplugins.datalistapi.DataListLoaderApi/service?appId=pttimah_pobijih&dataListId=master_jabatan&page=1`

## Use of Datalist ##
the example in the datalist below is showing data **JABATAN** and data **BOBOT**
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/plugins-datalist1.png" alt="plugins-datalist" />

with the view data on the website is as follows
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/plugins-datalist2.png" alt="plugins-datalist2" />

so the datalist API is as follows:
```javascript
{
    "total": 10,
    "data": [
        {
            "jabatan": "ABC",
            "bobot_approval": "10.00"
        },
        {
            "jabatan": "XYZ",
            "bobot_approval": "90.00"
        },
        {
            "jabatan": "123",
            "bobot_approval": "1.00"
        },
        {
            "jabatan": "345",
            "bobot_approval": "1.00"
        },
        {
            "jabatan": "234",
            "bobot_approval": "234.00"
        },
        {
            "jabatan": "321",
            "bobot_approval": "321.00"
        },
        {
            "jabatan": "444",
            "bobot_approval": "444.00"
        },
        {
            "jabatan": "555",
            "bobot_approval": "555.00"
        },
        {
            "jabatan": "111",
            "bobot_approval": "111.00"
        },
        {
            "jabatan": "222",
            "bobot_approval": "222.00"
        }
    ]
}
```

if you want to display more data from the **Jabatan** and **Bobot**, can be done in the way below:

1. drag what data you want to display on kecak (for example, the data you want to add is the **username** data)
2. save
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/plugins-datalist3.png" alt="plugins-datalist3" />

3. refresh in website applications
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/plugins-datalist4.png" alt="plugins-datalist4" />

then the display of additional data username on the website is as follows:
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/plugins-datalist5.png" alt="plugins-datalist5" />

with its datalist API is as follows (increase 1 list of data, ie data **username**):

```javascript
{
    "total": 10,
    "data": [
        {
            "jabatan": "ABC",
            "bobot_approval": "10.00",
            "username": "aristo"
        },
        {
            "jabatan": "XYZ",
            "bobot_approval": "90.00",
            "username": "irma"
        },
        {
            "jabatan": "123",
            "bobot_approval": "1.00",
            "username": "aristo"
        },
        {
            "jabatan": "345",
            "bobot_approval": "1.00",
            "username": "aristo"
        },
        {
            "jabatan": "234",
            "bobot_approval": "234.00",
            "username": "aristo"
        },
        {
            "jabatan": "321",
            "bobot_approval": "321.00",
            "username": "aristo"
        },
        {
            "jabatan": "444",
            "bobot_approval": "444.00",
            "username": "aristo"
        },
        {
            "jabatan": "555",
            "bobot_approval": "555.00",
            "username": "aristo"
        },
        {
            "jabatan": "111",
            "bobot_approval": "111.00",
            "username": "aristo"
        },
        {
            "jabatan": "222",
            "bobot_approval": "222.00",
            "username": "aristo"
        }
    ]
}
```

## Use of Datalist Using Filter ##

### How to use Filter ###
1. Drag datalist for filter (sample data to be filtered is data username)
2. save

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/plugins-datalist-save-using-filter.png" alt="plugins-datalist-save-using-filter" />

3. refresh in website applications
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/plugins-datalist-save-using-filter-refresh.png" alt="plugins-datalist-save-using-filter-refresh" />

 it will display the username filter box as in the picture:
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/plugins-datalist-save-using-filter-result.png" alt="plugins-datalist-save-using-filter-result" />

Sample Filter used is username: **irma**
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/plugins-datalist-save-using-filter-result1.png" alt="plugins-datalist-save-using-filter-result1" />

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/plugins-datalist-save-using-filter-test.png" alt="plugins-datalist-save-using-filter-test" />

with the datalist API, add the **username8* parameter as a filter:
`http://kecak.kinnarastudio.com:8080/web/json/plugin/com.kinnara.kecakplugins.datalistapi.DataListLoaderApi/service?appId=pttimah_pobijih&dataListId=master_jabatan&page=1&username=irma`

wiil show :
``` javascript
{
    "total": 1,
    "data": [
        {
            "jabatan": "XYZ",
            "bobot_approval": "90.00",
            "username": "irma"
        }
    ]
}
```



### Version History ###

*  **1.1.0**
   * 

*  **1.0.0**
   * Initial creation : Isti Fatimah
