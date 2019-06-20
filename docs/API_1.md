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

```json

{
	"data" : " ",
	"message" : " ",
	"digest" : " "
}