## Rest API ##



### /json/data/app/(*:appId)/version/(*:appVersion)/form/(*:formDefId)/submit
	
### Description ###

Submit Form into table, can be used to save master data

### Method ###

RequestMethod.POST

### Parameters ###

| Parameters | Description |
|---|---|
| request | HTTP Request, request body contains form field values |
| response | HTTP response |
| appId | Application ID |
| appVersion | put 0 for current published app |
| formDefId | Form ID |


### Sample Result ###
	

### /json/data/app/(*:appId)/version/(*:appVersion)/form/(*:formDefId)/(*:primaryKey)

### Description 

Update data in Form

### Method

RequestMethod.PUT

### Parameters 

| Parameters | Description |
|---|---|
| request |  |
| response |  |
| appId |  |
| appVersion |  |
| formDefid |  |
| primaryKey |  |
| IOException |  |
 JSONException |  |
 
### Sample Result



### /json/data/app/(*:appId)/version/(*:appVersion)/list/(*:dataListId)

### Description 

API to retrieve dataList data

### Method

RequestMethod.GET

### Parameters

| Parameters | Description |
|---|---|
| request | HTTP Request |
| response | HTTP Response |
| appId | Application ID |
| appVersion | Application Version |
| dataListID | Datalist ID |
| page | Page paging every 10 rows, page = 0 will show all data without paging |
| sort | Sort Order list by specified field name |
| desc | Optional true/false |
| digest | hash calculation of data json |

### Sample Result
