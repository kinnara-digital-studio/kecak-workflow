# Kecak Mobile - Store Binder - Edit Profile Picture API#

## REST API ##

### Overview ###

Store Binder - Edit Profile Picture API is plugin to edit profile picture for certain user. Server will responses by giving back its binary of the picture.

##### Url #####

`web/json/plugin/com.kinnara.kecakplugins.mobileapi.EditProfilePictureApi/service`

##### Method #####
HTTP PUT

##### Header #####
* Authorization : *your access token*
* Referer : *your server host*
* Content-Type : *application/json*

##### Example #####
* pictureName : *File Name of Picture*

##### Example #####
`https://kecak.kinnarastudio.com/web/json/plugin/com.kinnara.kecakplugins.mobileapi.EditProfilePictureApi/service?pictureName=google-2.jpg`

### Use of API Plugin ###

The example below shows you that how to test Edit Profile Picture API plugin. First of all, fill the authorization with your currently token of the User with authorization type **Bearer Token** in POSTMAN.

After that, you have to choose the file of picture in request body in binary format.


![Request_Body](/uploads/22657cc4a805bef9b7195d62206c3ca2/Request_Body.PNG)


And also, you have to fill the header section by **Referer** and **Content-Type**. For this example, **Referer** is `https://kecak.kinnarastudio.com` and **Content-Type** is **application/json**


If you have done all steps above, the server will responses by giving you its binary picture in **Response Body**.

**Response is as follow** :

![Response_Body](/uploads/33e1c84bc0c8cef3d3d9a06aeee36a01/Response_Body.PNG)


### Version History ###

*  **1.0.0**
   * Initial creation

