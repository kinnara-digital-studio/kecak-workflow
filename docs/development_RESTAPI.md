# REST API 


### Post Form Submit 

#### Description 

Submit Form into table, can be used to save master data

#### Value

`{host}/web/json/data/app/{appId}/{appVersion}/form/{formId}`

#### Method 

RequestMethod.POST

#### Parameters (Using JSON)



#### Example (Using Postman)

1. Make sure you have been open postman

2. REST API Post -Submit

#### Sample Result 



<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/api_example.png" alt="api_example" />

---


### Put Form Data

`json/app/(*:appId)/(~:appVersion)/data/form/(*:formDefId)/(*:primaryKey)`

#### Description 

Update data in Form

#### Value

`json/app/(*:appId)/(~:appVersion)/data/form/(*:formDefId)/(*:primaryKey)`

#### Method

RequestMethod.PUT

#### Parameters 

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
 
#### Sample Result

```json
{
	"data" : "",
	"message" : "",
	"digest" : ""
}
```

---

### Get Form Data 

#### Description 

API to retrieve dataList data

#### Value

`/json/app/(*:appId)/(~:appVersion)/data/form/(*:formDefId)/(*:primaryKey))`

#### Method

RequestMethod.GET

#### Parameters

| Parameters | Description |
|---|---|
| request | HTTP Request |
| response | HTTP Response |
| appId |  |
| appVersion |  |
| formDefId |  |
| primaryKey |  |
| digest |  |

#### Sample Result

```json
{
	
}

```
---

### Get List Count 

#### Description

#### Value

`/json/data/app/(*:appId)/version/(*:appVersion)/list/(*:dataListId)/count`


#### Method

RequestMethod.GET

#### Parameters

| Parameters | Description |
|---|---|
| appId |  |
| appVersion |  |
| dataListId |  |

#### Sample Result

```json
{
	"total" : ""
}
```

---

### Get List 

#### Description 

API to retrieve datalist data 

#### Value 

`/json/app/(*:appId)/(~:appVersion)/data/list/(*:dataListId)`

#### Parameters

| Parameters | Description |
|---|---|
| appId |  |
| appVersion |  |
| dataListId |  |
| page |  |
| sort |  |
| desc |  |
| digest |  |

#### Sample Result

```json
{
	
}

```

---

### Post Process Start 

#### Description

Start New Process

#### Value

`/json/data/app/(*:appId)/version/(*:appVersion)/process/(*:processId)`

#### Method

RequestMethod.POST 

#### Parameters

| Parameters | Description |
|---|---|
| request | HTTP Request, request body contains form field values |
| response | HTTP Response |
| appId | Application ID |
| appVersion | put 0 for current published app |
| processId | Process ID |

#### Sample Result

```json
{
	"processId" : "",
	"activityId" : "",
	"dateCreated" : "",
	"dueDate" : "",
	"priority" : ""
}
{
	"processId" : "",
	"activityId" : ""
}
```

---

### Post Assignment Complete

#### Description 

Complete assignment form

#### Value 

`/json/data/assignment/(*:assignmentId)`

#### Method

RequestMethod.POST

#### Parameters

| Parameters | Description |
|---|---|
| request | HTTP request, request body contains form field values |
| response | HTTP response |
| assignmentId | Assignment ID |

#### Sample Result

```json
{
	"activityId" : "",
	"processId" : "",
	"digest" : "",
	"message" : "",
	"data" : ""
}

```

--- 

### Get Assignment Count

#### Description

#### Value

`/json/data/assignments/count`

#### Method

RequestMethod.GET

#### Parameters

| Parameters | Description |
|---|---|
| assignmentId |  |
| digest |  |

#### Sample Result

```json

{
	"total" : ""
}
```

---

### Get Assignments

#### Description

#### Value  

`/json/data/assignments`

#### Method

RequestMethod.GET

#### Parameters

| Parameters | Description |
|---|---|
| request |  |
| response |  |
| page |  |
| sort |  |
| desc |  |
| digest |  |
| IOException |  |


#### Sample Result

```json

{
	"activity" : "",
	"process" : "",
	"assigneId" : ""
}
```

---

