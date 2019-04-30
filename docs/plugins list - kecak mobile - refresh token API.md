# Kecak Mobile - Refresh Token API #

## REST API ##

### Overview ###

Refresh Token API is plugin to refresh token at user's mobile device with new FCM (Firebase Cloud Messaging) token. And then, server will responses by giving the update token with new FCM token included.

##### Url #####

`web/json/plugin/com.kinnara.kecakplugins.mobileapi.RefreshTokenApi/service`

##### Method #####
HTTP PUT

##### Header #####
* Authorization : *your access token*
* Referer : *your server host*
* Content-Type : *application/json*

##### Example #####
`https://kecak.kinnarastudio.com/web/json/plugin/com.kinnara.kecakplugins.mobileapi.RefreshTokenApi/service`

### Use of API Plugin ###

The example below shows you that how to use Refresh Token API plugin. First of all, you have to fill the authorization with your access token to authorize you as authorized user.

![Authorization](/uploads/b7bfe451430a6e613d7b1a548c9f61d4/Authorization.PNG)

After that, you have to fill request body in JSON format which contains FCM token and device ID of your mobile device.

```json
{
  "fcm_token" : "sdsdugsudgjHIDSASDA",
  "device_id" : "123131231dsafasfa"
}
```

![Body](/uploads/366529aebb11c7750452f7c5ae5b6d82/Body.PNG)

And also, you have to fill the header section by **Referer** and **Content-Type**. For this example, **Referer** is `https://kecak.kinnarastudio.com` and **Content-Type** is **application/json**

![Headers](/uploads/8d7ab81ed8d031ccde1d19b825851690/Headers.PNG)

If you have done all steps above, the server will responses by giving you the updated token in **Response Body**

**Response is as follow** :

```json
{
    "token": "your updated token"
}
```

### Version History ###

*  **1.0.0**
   * Initial creation

