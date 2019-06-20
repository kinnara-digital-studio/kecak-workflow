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
    "data": {
        "createdBy": "admin",
        "dateModified": "2019-06-20 08:51:04.0",
        "duit2": "",
        "id": "\t6e1a37c7-ac1f2796-653b8a01-f1ef601b",
        "modifiedBy": "admin",
        "duit1": "",
        "username": "",
        "hasil": "30000",
        "just_format": "",
        "wololo_numeric": "",
        "locale": "",
        "dateCreated": "2019-06-20 08:51:04.0"
    },
    "digest": "5f25eba6f3f3ccf9bfad9bb01e4340802ff4ca586ca528a39067819922df8876",
    "message": "Success"
}
```

https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/api_example.png" alt="api_example />
