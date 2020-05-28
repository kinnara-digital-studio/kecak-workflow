# Push Notification

Push notification plugins are used to send notifications via the user's mobile phone.

## How to use

### Settings Firebase

1. Open Google Firebased

You can open google firebased in this link https://console.firebase.google.com/u/0/ and you can see this page

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/firebased_open.png" alt="" />


2. Open Project

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/firebased_openProject.png" alt="" />


If you don't have project, you can create project like in this picture :

click "add project" 

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/firebased_buildProject.png" alt="" />


a. Settings add firebase to android

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/firebased_addAndroidSet.png" alt="" />


b. register firebase

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/firebased_registerSet.png" alt="" />

c. creating project

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/firebased_create.png" alt="" />

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/firebased_createContinue.png" alt="" />

3. Open settings Project

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/firebased_settingsProject.png" alt="" />

4. Settings project

a. Go to Cloud Messaging page

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/firebased_cloudMessaging.png" alt="" />

b. Copy server key that showed in cloud messaging page

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/firebased_copyServeyKey.png" alt="" />

c. Go to service accounts page

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/firebased_copyPrivateKey.png" alt="" />

d. Generate private key
in the bottom of page, click button "Generate new private key"

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/firebased_GeneratePrivateKey.png" alt="" />

and you will show the private key downloaded.

### Settings in Kecak

1. Open Apps

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/push_openApps.png" alt="" />


2. Go to process and maps tools to plugins

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/push_process-mapToTools.png" alt="" />


3. Choose Tools

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/push_chooseTools.png" alt="" />


4. Choose tools then add/edit plugin

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/push_addEdit.png" alt="" />


5. Choose Push Notification Tools

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/push_choose.png" alt="" />


6. Edit configuration

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/push_configuration.png" alt="" />


Input here server key and private key that you have get from firebased.

Description :

|Field | Description|
|-|-|
|Authorization||
|JSON Private key||

7. Go to tab activity

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/push_activity.png" alt="" />

Description :

|Field | Description|
|-|-|
|Process||
|Activity||

8. Go to notification

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/push_notification.png" alt="" />

Description :

|Field | Description|
|-|-|
|Participant ID||
|User ID||
|Titte||
|Content||

9. Result

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/push_result.jpg" alt="" />
