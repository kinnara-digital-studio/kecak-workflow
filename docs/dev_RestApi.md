# REST API

## 1. POST Form Submit

**description**

Submit Form into table, can be used to save master data or other data(s).


**Value**

`{host}/web/json/data/app/{appId}/{appVersion}/form/{formId}`


**Example**

1. Open postman

2. Authorization

<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/restApiPost_authorization.png" alt="" />

describe :
|No|Description|
|-|-|
|1|Method :|
|2|URL|
|3|Authorization|
|4||
|5||
|6||


3. Body

<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/restApiPost_body.png" alt="" />

describe :
|No|Description|
|-|-|
|1||
|2||
|3||
|4||
|5||


4. Parameter Form in Kecak

<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/restApiPost_bodyFrom.png" alt="" />

id from each field


5. Body response after run

<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/restApiPost_bodyResponse.png" alt="" />


6. Result in Kecak (data has been added in datalist)

<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/restApiPost_result.png" alt="" />


## 2. POST Form Update

**description**




**Value**

`/json/data/app/(*:appId)/(~:appVersion)/form/(*:formDefId)/(*:primaryKey)`


**Example**

in a body you only added ID from field that you will update, like this example :

<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/restApiUpdate_body.png" alt="" />


describe :
|No|Name|Description|
|-|-|-|
|1|||
|2|||
|3|||


<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/restApiUpdate_result.png" alt="" />


## 3. GET Form Data

**description**




**Value**

`/json/data/app/(*:appId)/(~:appVersion)/form/(*:formDefId)/(*:primaryKey)`
https://kecak.kinnarastudio.com/web/json/data/app/pengajuanCuti/1/form/masterDataEmployee/ID-066

**Example**

For example, you can follow step in this pictures :

<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/restApiGetFormData_authorization.png" alt="" />

describe :
|No|Name|Description|
|-|-|-|
|1|||
|2|||
|3|||


<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/restApiGetFormData_header.png" alt="" />

describe :
|No|Name|Description|
|-|-|-|
|1|||
|2|||
|3|||


<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/restApiGetFormData_result.png" alt="" />

describe :
|No|Name|Description|
|-|-|-|
|1|||
|2|||
|3|||


## 4. GET List Count

**description**


**Value**

`/json/data/app/(*:appId)/(~:appVersion)/list/(*:dataListId)/count`
https://kecak.kinnarastudio.com/web/json/data/app/pengajuanCuti/1/form/masterDataEmployee/ID-066


**Example**

For example, you can follow step in this pictures :

<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/.png" alt="" />

describe :
|No|Name|Description|
|-|-|-|
|1|||
|2|||
|3|||
##
