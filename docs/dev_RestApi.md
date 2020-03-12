# REST API

## 1. POST Form Submit

*description*

Submit Form into table, can be used to save master data or other data(s).


**Value**

`{host}/web/json/data/app/{appId}/{appVersion}/form/{formId}`


**Example by postman**

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

in a body you only added ID from field that you will update, like this example :

<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/restApiUpdate_body.png" alt="" />


describe :
|No|Description|
|-|-|
|1||
|2||
|3||


<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/restApiUpdate_result.png" alt="" />

