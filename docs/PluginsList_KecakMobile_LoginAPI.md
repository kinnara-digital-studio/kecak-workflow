# Kecak Mobile - Login API #

## REST API ##

### Overview ###

Login API is plugin to log into https://kecak.kinnarastudio.com by using basic authorization which username and password of the user are needed. User login by using this plugin have to send body request in JSON format which contains "fcm_token" and "device_id" belongs to mobile device of the user. And then, server will responses by giving the token.

##### Url #####

`web/json/plugin/com.kinnara.kecakplugins.mobileapi.LoginApi/service`

##### Method #####
HTTP POST

##### Header #####
* Authorization : *Basic "decrypted username and password"*
* Referer : *your server host*
* Content-Type : *application/json*

##### Example #####
`https://kecak.kinnarastudio.com/web/json/plugin/com.kinnara.kecakplugins.mobileapi.LoginApi/service`

### Use of API Plugin ###

The example below shows you that you have to fill basic authorization form which contains username and password to log into https://kecak.kinnarastudio.com using Login API plugin.

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/kecakMobile-Login.PNG" alt="kecakMobile-Login.png" />

After you have filled basic authorization form, you have to write request body in JSON format which contains FCM token and device ID of your mobile device.

```json
{
  "fcm_token" : "sdsdugsudgjHIDSASDA",
  "device_id" : "123131231dsafasfa"
}
```

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/kecakMobile-Login2.PNG" alt="kecakMobile-Login.png" />

And also, you have to fill the header section by **Referer** and **Content-Type**. For this example, **Referer** is `https://kecak.kinnarastudio.com` and **Content-Type** is **application/json**

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/kecakMobile-Login3.PNG" alt="kecakMobile-Login.png" />

If you have done all steps above, the server will responses by giving you the token and the user data in **Response Body**

**Response is as follow** :

```json
{
    "full_name": "Admin Admin",
    "email": "",
    "username": "admin",
    "token": "*your access token*"
}
```

### Version History ###

*  **1.0.0**
   * Initial creation

