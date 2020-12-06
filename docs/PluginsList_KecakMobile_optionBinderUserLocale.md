# Kecak Mobile - Options Binder - User Locale #

## REST API ##

### Overview ###

Options Binder - User Locale is plugin to get available locale. Server will responses by giving back its available locale.

##### Url #####

`web/json/plugin/com.kinnara.kecakplugins.mobileapi.UserLocaleApi/service`

##### Method #####
HTTP GET

##### Header #####
* Authorization : *your access token*
* Referer : *your server host*
* Content-Type : *application/json*

##### Example #####
`https://kecak.kinnarastudio.com/web/json/plugin/com.kinnara.kecakplugins.mobileapi.UserLocaleApi/service`

### Use of API Plugin ###

The example below shows you that how to test User Locale API plugin. First of all, fill the authorization with your currently token of the User with authorization type **Bearer Token** in POSTMAN.

And also, you have to fill the header section by **Referer** and **Content-Type**. For this example, **Referer** is `https://kecak.kinnarastudio.com` and **Content-Type** is **application/json**

If you have done all steps above, the server will responses by giving you JSONBody in **Response Body** which contains its available locale.

**Response is as follow** :

```json
{
    "en_US": "en_US - English (United States)",
    "in_ID": "in_ID - Indonesian (Indonesia)"
}
```


### Version History ###

*  **1.0.0**
   * Initial creation

