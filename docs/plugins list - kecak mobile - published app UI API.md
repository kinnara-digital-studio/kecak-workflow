# Kecak Mobile - Published Apps UI - API #

## REST API ##

### Overview ###

Published Apps UI - API is plugin to get all published apps for authorized user. Server will responses by giving response body in JSON format which contains *appId*, *userviewId*, and *userviewName*.

##### Url #####

`web/json/plugin/com.kinnara.kecakplugins.mobileapi.GetJsonPublishedAppApi/service`

##### Method #####
HTTP GET

##### Header #####
* Authorization : *your access token*
* Referer : *your server host*
* Content-Type : *application/json*

##### Parameters #####
* digest - Digested JSON

##### Example #####
`https://kecak.kinnarastudio.com/web/json/plugin/com.kinnara.kecakplugins.mobileapi.GetJsonPublishedAppApi/service`

### Use of API Plugin ###

This example is showing all published Kecak Workflow applications for authorized user, with the published apps on the website is as follows

![Published_Apps](/uploads/9b1c60449921578215cbc494c7d5b6dd/Published_Apps.PNG)
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/KKecakMobile-PublishedAppsAPI-Published_Apps.png" alt="KecakMobile-PublishedAppsAPI-Published_Apps" />


Then, you have to fill the authorization with your access token to authorize you as authorized user.

![Authorization](/uploads/210cd74f9f34028f56a0ebf2af4f09e6/Authorization.PNG)
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/KKecakMobile-PublishedAppsAPI-Authorization.png" alt="KecakMobile-PublishedAppsAPI-Authorization" />

And also, you have to fill the header section by **Referer** and **Content-Type**. For this example, **Referer** is `https://kecak.kinnarastudio.com` and **Content-Type** is **application/json**

![Headers](/uploads/3f30072717166731d16c7ce28284a007/Headers.PNG)
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/KKecakMobile-PublishedAppsAPI-Headers.png" alt="KecakMobile-PublishedAppsAPI-Headers" />

And this example, we just fill empty for **digest** parameter.

If you have done all steps above, the server will responses by giving you JSONBody in **Response Body** which contains *digest*, *status*, and *json* (which contains *appId*, *userviewId*, and *userviewName* belong to Published Userview).

**Response is as follow** :
```json
{
    "digest": "9ce8a2a501d90692e4ad6d8389aed8e721bc4d5f",
    "json": [
        {
            "userviewId": "AprovalRendy",
            "appId": "AprovalRendy",
            "userviewName": "Pengelolaan Reimburse Biaya Karyawan"
        },
        {
            "userviewId": "change_management_view",
            "appId": "bcai_change_management",
            "userviewName": "Change Management Portal"
        },
        {
            "userviewId": "su",
            "appId": "AccCreation",
            "userviewName": "Create Account"
        },
        {
            "userviewId": "eApproval_jo",
            "appId": "eApproval_jo",
            "userviewName": "e-Approval Jo"
        },
        {
            "userviewId": "userview1",
            "appId": "etracking_sop",
            "userviewName": "e-Tracking SOP"
        },
        {
            "userviewId": "ihealth_portal",
            "appId": "ihealth_mandiri",
            "userviewName": "Ihealth Portal"
        },
        {
            "userviewId": "inHealth",
            "appId": "inHealth",
            "userviewName": "Inhealth e-approval"
        },
        {
            "userviewId": "invoiceManagementPortal",
            "appId": "invoiceApproval",
            "userviewName": "Invoice Management Portal"
        },
        {
            "userviewId": "leavePortal",
            "appId": "leavePortal",
            "userviewName": "Leave Portal"
        },
        {
            "userviewId": "loan_app",
            "appId": "loan_app",
            "userviewName": "Loan Application"
        },
        {
            "userviewId": "process_adm",
            "appId": "processAdm",
            "userviewName": "Process Administration"
        },
        {
            "userviewId": "v",
            "appId": "ProcessVersionMigration",
            "userviewName": "#envVariable.AppName#"
        },
        {
            "userviewId": "pttimah_po_bijih",
            "appId": "pttimah_pobijih",
            "userviewName": "PT Timah - PO Bijih"
        },
        {
            "userviewId": "role_management",
            "appId": "roleMgmt",
            "userviewName": "Role Management"
        },
        {
            "userviewId": "shpSystem",
            "appId": "shp",
            "userviewName": "SHP System"
        },
        {
            "userviewId": "pengajuan_pengurangan_denda",
            "appId": "studyCaseKecak",
            "userviewName": "Pengajuan Pengurangan Denda"
        },
        {
            "userviewId": "admin_panel",
            "appId": "pttimah_eapproval",
            "userviewName": "Admin Panel"
        },
        {
            "userviewId": "po_bijih",
            "appId": "pttimah_eapproval",
            "userviewName": "PO Bijih"
        },
        {
            "userviewId": "spd",
            "appId": "pttimah_eapproval",
            "userviewName": "SPD dan SIJ"
        },
        {
            "userviewId": "test",
            "appId": "testing",
            "userviewName": "Test"
        },
        {
            "userviewId": "leavePortal",
            "appId": "TrainingKecak",
            "userviewName": "Leave Portal"
        },
        {
            "userviewId": "training_REST",
            "appId": "trainingREST",
            "userviewName": "Training REST"
        }
    ],
    "status": "success"
}
```

### Version History ###

*  **1.0.0**
   * Initial creation

