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
