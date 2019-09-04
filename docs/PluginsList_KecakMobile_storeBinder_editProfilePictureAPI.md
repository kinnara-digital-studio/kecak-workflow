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

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/kecakMobile-StoreBinderEditProfilePictureAPI-request_body.PNG" alt="request_body" />

And also, you have to fill the header section by **Referer** and **Content-Type**. For this example, **Referer** is `https://kecak.kinnarastudio.com` and **Content-Type** is **application/json**


If you have done all steps above, the server will responses by giving you its binary picture in **Response Body**.

**Response is as follow** :

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/kecakMobile-StoreBinderEditProfilePictureAPI-Response_Body2.PNG" alt="response_body" />


### Version History ###

*  **1.0.0**
   * Initial creation

