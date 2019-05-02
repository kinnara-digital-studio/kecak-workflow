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

![Authorization](/uploads/25040947a325b4b71a2c864ae2c75cf7/Authorization.PNG)

And also, you have to fill the header section by **Referer** and **Content-Type**. For this example, **Referer** is `https://kecak.kinnarastudio.com` and **Content-Type** is **application/json**

![Headers](/uploads/ffa37ed4d49f438c5c78aab4d92b50f1/Headers.PNG)

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

