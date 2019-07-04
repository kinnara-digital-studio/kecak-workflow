### DataList Action API ###

Datalist action API in Kecak Mobile is for adding datalist action based on mobile.

##### Url #####
```json
/web/json/plugin/com.kinnara.kecakplugins.mobileapi.DataListActionApi/service
```

##### Method #####
HTTP POST

##### Parameters #####
* appId - Application ID
* appVersion - Application Version (optional): if not included will retrieve the latest version
* dataListId - DataList ID: data list to be displayed
* actionId - Action ID

##### Example #####
```json
http://kecak.kinnarastudio.com:8080/web/json/plugin/com.kinnara.kecakplugins.mobileapi.DataListActionApi/service?appId=pttimah_pobijih&dataListId=master_jabatan&actionId=rowAction_1`
```
