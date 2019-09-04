# Mobile Request API #

### Run Process REST API ###

#### Request ####

##### Url #####

`/web/json/plugin/com.kinnara.kecakplugins.mobilerequestapi.RunProcessRestApi/service`

##### Method #####
HTTP POST

##### Parameters #####
*  processDefId - Process definition ID
*  appId - Application ID
*  appVersion - Application Version

##### Header #####
*  BasicAuth - BASE64 encoded of "[username]:[password]"

##### Body #####
```javascript
{
  [field1] : [value1],
  [field2] : [value2]
}
```

#### Response ####
##### Body #####
```javascript
{
  "message" : "success",
  "processId" : [process ID]
}
```
