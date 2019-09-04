# Kecak Mobile - Check Token API #

## REST API ##

### Overview ###

Check Token API is plugin to validating authorization of the user by token which user currently have. Server will responses by giving the value of validation which is true or false and also full name, fcm token, email, and username belong to the user.

##### Url #####

`web/json/plugin/com.kinnara.kecakplugins.mobileapi.CheckTokenApi/service`

##### Method #####
HTTP GET

##### Header #####
* Authorization : *your access token*
* Referer : *your server host*
* Content-Type : *application/json*

##### Example #####
`https://kecak.kinnarastudio.com/web/json/plugin/com.kinnara.kecakplugins.mobileapi.CheckTokenApi/service`

### Use of API Plugin ###

The example below shows you that how to use Check Token API plugin. First of all, fill the authorization with your currently token or make it empty if your currently token is empty.

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/kecakMobile-CheckTokenAPI.PNG" alt="kecakMobile-CheckTokenAPI" />

And also, you have to fill the header section by **Referer** and **Content-Type**. For this example, **Referer** is `https://kecak.kinnarastudio.com` and **Content-Type** is **application/json**

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/kecakMobile-CheckTokenAPI2.PNG" alt="kecakMobile-CheckTokenAPI2" />


If you have done all steps above, the server will responses by giving you JSONBody in **Response Body** which contains the data of users and validation for the token.

**Response is as follow** :

```json
{
    "valid": true,
    "full_name": "Admin Admin",
    "fcm_token": "sdsdugsudgjHIDSASDA",
    "email": "",
    "username": "admin"
}
```


### Version History ###

*  **1.0.0**
   * Initial creation

