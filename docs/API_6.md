### Post Process Start 

### Description

Start New Process

### Value

`/json/data/app/(*:appId)/version/(*:appVersion)/process/(*:processId)`

### Method

RequestMethod.POST 

### Parameters

| Parameters | Description |
|---|---|
| request | HTTP Request, request body contains form field values |
| response | HTTP Response |
| appId | Application ID |
| appVersion | put 0 for current published app |
| processId | Process ID |

### Sample Result

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
- [JSON API](ResAPI.md)