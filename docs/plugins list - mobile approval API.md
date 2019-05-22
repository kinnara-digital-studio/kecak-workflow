# Mobile Approval API #

## Overview ##

Mobile Approval API is a bundle of Webservice API Plugins used in Mobile Approval application. There are 3 APIs included in this bundle:
*  Login API : Login
*  Load Binder API : Load Datan
*  Store Binder API : Save Data

For Inbox API, you can use [Joget's JSON API](https://dev.joget.org/community/display/KBv5/JSON+API#JSONAPI-web/json/workflow/assignment/list)
.

## Pre-Configuration ##
Allow connection from external IP, go to:
```
Settings(1) -> General Settings(2) -> API Domain Whitelist(3) : *
```
![Kecak_Workflow_Settings_Whitelist](/uploads/492adebaacda0d376257aa1faf59fbe2/Kecak_Workflow_Settings_Whitelist.png)

## Login API ##

#### Request ####

##### Url #####

`/web/json/plugin/com.kinnara.kecakplugins.mobileapi.mobileapproval.LoginApi/service`

##### Method #####
HTTP POST

##### Header #####
*  Authorization - Basic [Base 64 encoded username and password]

##### Parameters #####
None

##### Example #####
`/web/json/plugin/com.kinnara.kecakplugins.mobileapi.mobileapproval.LoginApi/service`

#### Response ####
##### Body #####
```javascript
{
  "status": "success"
}
```

## Load Binder API ##

#### Request ####

##### Url #####

`/web/json/plugin/com.kinnara.kecakplugins.mobileapi.mobileapproval.LoadBinderRestApi/service`

##### Method #####
HTTP GET

##### Header #####
*  Authorization - Basic [Base 64 encoded username and password] : either Master or User account

##### Parameters #####
*  activityId - Activity ID (optional)
*  processId - Process ID (optional)
*  formId - Form ID (optional) : use in conjunction with processId
*  loginAs - Login As (optional) : required if you use Master username and password as authorization

##### Example #####
`/web/json/plugin/com.kinnara.kecakplugins.mobileapi.mobileapproval.LoadBinderRestApi/service?activityId=41_20_pttimah_eapproval_spd_activityPersetujuanKadivPeminta`

`/web/json/plugin/com.kinnara.kecakplugins.mobileapi.mobileapproval.LoadBinderRestApi/service?processId=20_pttimah_eapproval_spd`

`/web/json/plugin/com.kinnara.kecakplugins.mobileapi.mobileapproval.LoadBinderRestApi/service?processId=20_pttimah_eapproval_spd&formId=spd_approval`

#### Response ####
##### Body #####
```javascript
{
  "activityId": "41_20_pttimah_eapproval_spd_activityPersetujuanKadivPeminta",
  "form": {
    "className": "org.joget.apps.form.model.Form",
    "id": "spd_approval_np_spd",
    "label": "",
    "value": [
      {
        "className": "org.joget.apps.form.lib.SubForm",
        "id": "sf_np_spd",
        "label": "",
        "value": [
          {
            "className": "org.joget.apps.form.lib.IdGeneratorField",
            "id": "nomor_np_spd",
            "label": "Nomor NP-SPD",
            "element_path": "sf_np_spd_spd_new_np_spd_nomor_np_spd",
            "value": "ID-000004"
          },
          {
            "className": "org.joget.apps.form.lib.Radio",
            "id": "tipe_perjalanan",
            "label": "Tipe Perjalanan",
            "element_path": "sf_np_spd_spd_new_np_spd_tipe_perjalanan",
            "value": "Surat Perjalanan Dinas (SPD)"
          },
          {
            "className": "org.joget.apps.form.lib.TextField",
            "id": "nama_karyawan",
            "label": "Nama Karyawan",
            "element_path": "sf_np_spd_spd_new_np_spd_nama_karyawan",
            "value": ""
          },
          {
            "className": "org.joget.apps.form.lib.TextField",
            "id": "nik_karyawan",
            "label": "NIK Karyawan",
            "element_path": "sf_np_spd_spd_new_np_spd_nik_karyawan",
            "value": ""
          },
          {
            "className": "org.joget.apps.form.lib.TextField",
            "id": "email",
            "label": "Email",
            "element_path": "sf_np_spd_spd_new_np_spd_email",
            "value": ""
          },
          {
            "className": "org.joget.apps.form.lib.SelectBox",
            "id": "eselon",
            "label": "Eselon",
            "element_path": "sf_np_spd_spd_new_np_spd_eselon",
            "value": "Eselon 4",
            "required": true
          },
          {
            "className": "org.joget.apps.form.lib.Radio",
            "id": "jenis_perjalanan",
            "label": "Jenis Perjalanan",
            "element_path": "sf_np_spd_spd_new_np_spd_jenis_perjalanan",
            "value": "Luar Negeri",
            "required": true
          },
          {
            "className": "org.joget.apps.form.lib.SelectBox",
            "id": "jabatan_direksi",
            "label": "Jabatan Direksi",
            "element_path": "sf_np_spd_spd_new_np_spd_jabatan_direksi",
            "required": false
          },
          {
            "className": "org.joget.apps.form.lib.TextArea",
            "id": "tujuan",
            "label": "Tujuan",
            "element_path": "sf_np_spd_spd_new_np_spd_tujuan",
            "value": "aa",
            "required": true
          },
          {
            "className": "org.joget.apps.form.lib.TextArea",
            "id": "maksud",
            "label": "Maksud",
            "element_path": "sf_np_spd_spd_new_np_spd_maksud",
            "value": "a",
            "required": true
          },
          {
            "className": "org.joget.apps.form.lib.DatePicker",
            "id": "tanggal_berangkat",
            "label": "Berangkat",
            "element_path": "sf_np_spd_spd_new_np_spd_tanggal_berangkat",
            "value": "11/23/2017",
            "required": true
          },
          {
            "className": "org.joget.apps.form.lib.DatePicker",
            "id": "tanggal_kembali",
            "label": "Kembali",
            "element_path": "sf_np_spd_spd_new_np_spd_tanggal_kembali",
            "value": "11/25/2017",
            "required": true
          },
          {
            "summary": [
              {
                "id": "biaya",
                "label": "Biaya"
              }
            ],
            "options": [
              {
                "label": "Jenis",
                "value": "jenis_fasilitas"
              },
              {
                "label": "Biaya",
                "value": "biaya"
              },
              {
                "label": "Deskripsi",
                "value": "deskripsi"
              }
            ],
            "className": "com.kecak.kecakplugins.formgrid.FormGrid",
            "id": "fasilitas",
            "label": "Fasilitas",
            "element_path": "sf_np_spd_spd_new_np_spd_fasilitas",
            "value": []
          },
          {
            "className": "org.joget.apps.form.lib.Radio",
            "id": "jenis_pembayaran",
            "label": "Jenis Pembayaran",
            "element_path": "sf_np_spd_spd_new_np_spd_jenis_pembayaran",
            "value": "Tunai",
            "required": true
          }
        ]
      }
    ]
  },
  "processId": "20_pttimah_eapproval_spd",
  "digest": "99fa8086f592905edadfd1a6c1741b875bafe86ebda4aac60f978ab23983a1bcd224f8d5b668636690ccd78d3edbf6561d4fd4bca800b6aafc39c1979d2f7259",
  "actions": [
    {
      "options": [
        {
          "label": "Setuju",
          "value": "approved"
        },
        {
          "label": "Revisi",
          "value": "revised"
        }
      ],
      "className": "org.joget.apps.form.lib.Radio",
      "id": "status",
      "label": "Persetujuan",
      "element_path": "status",
      "required": true
    },
    {
      "className": "org.joget.apps.form.lib.TextArea",
      "id": "catatan",
      "label": "Catatan",
      "element_path": "catatan",
      "value": "",
      "required": true
    }
  ]
}
```

## Store Binder API ##

#### Request ####

##### Url #####

`/web/json/plugin/com.kinnara.kecakplugins.mobileapi.mobileapproval.StoreBinderRestApi/service`

##### Method #####
HTTP POST

##### Header #####
*  Authorization - Basic [Base 64 encoded username and password] : either MASTER or User account

##### Parameters #####
*  activityId - Activity ID
*  processId - Process ID
*  formId - Form ID : use in conjuction with processId
*  loginAs - Login As (optional) : required if you use Master username and password as authorization

##### Body #####
JSON Object containing pairs of field and value

```javascript
{
  "status" : "approved",
  "remarks" : "Remarks"
}
```

##### Example #####
`/web/json/plugin/com.kinnara.kecakplugins.mobileapi.mobileapproval.StoreBinderRestApi/service?activityId=41_20_pttimah_eapproval_spd_activityPersetujuanKadivPeminta`

`/web/json/plugin/com.kinnara.kecakplugins.mobileapi.mobileapproval.StoreBinderRestApi/service?processId=20_pttimah_eapproval_spd`

`/web/json/plugin/com.kinnara.kecakplugins.mobileapi.mobileapproval.StoreBinderRestApi/service?processId=20_pttimah_eapproval_spd&formId=spd_approval`

#### Response ####
##### Body #####
```javascript
{
  "message": "Form has been submitted",
  "data" : {
    "dateSubmitted" : "2017-02-03 10:23:42"
  }
}
```

## Inbox List API ##
Please refer to [Joget JSON API's Assignment List](https://dev.joget.org/community/display/KBv5/JSON+API#JSONAPI-web/json/workflow/assignment/list)

## Version History ##
*  **1.1.0**
   * Add digest calculation in Load Binder API
*  **1.0.0**
   * Initial creation

