# Datalist Jasper Report

Plugins that are used to create reports that can be printed in PDF format and display reports based on datalist.

## How to use

1. Open Apps

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalistJasper_openApps.png" alt="" />


2. Choose userview

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalistJasper_chooseUserview.png" alt="" />


3. Drag and drop plugins

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalistJasper_dragDrop.png" alt="" />


4. Go to properties page

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalistJasper_properties.png" alt="" />


5. Go to configure datalist page

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalistJasper_configureDatalist.png" alt="" />


6. Go to configure jasper

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalistJasper_configureJasper.png" alt="" />

Description :

|Feld|Description|
|-|-|
|Default Output||
|Export Option||
|File Name ||
|Jasper Report Definition (JRXML) ||

## Configure Jasper report in Jasper Report Studio

### How to get JRXML

we can get JRXML from Jasper Report Studio

Let's check this step for get JRXML :

- Open Jasper report studio

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalistJasper_openJasper.png" alt="" />


- Settings Jasper Studio

|No|Information|
|-|-|
|1.| Choose Data Adapter |
|2.| Choose Create Data Adapter |

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalistJasper_jasperSetting1.png" alt="" />


|No|Information|
|-|-|
|1.| Choose JSON File |
|2.| Choose Next button |

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalistJasper_jasperSetting2.png" alt="" />


<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalistJasper_jasperSetting3.png" alt="" />

|No|Field|Description|
|-|-|-|
|1.| Name | Name of JSON file that will be created |
|2.| File Url | URL from plugins datalist in application (you can follow picture part in "**Get File URL**") |
|3.| Click Option and will be shown like Fill HTTP Connection Options picture |

**Fill HTTP Connection Options

Add HTTP Headers with

|No|Information|
|-|-|
|**Referer** | Fill with URL |
|**Authorization**| Fill with value url basic from datalist |

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalistJasper_jasperSetting4.png" alt="" />


**Get File URL 

- Go to kecak application
in Kecak application you are in datalist properties, and go to **Configure Datalist** like this picture:

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalistJasper_jasperSetting5.png" alt="" />

- Get JSON Url

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalistJasper_jasperSetting6.png" alt="" />

- Copy JSON Url

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalistJasper_jasperSetting7.png" alt="" />

after that, paste the URL in File URL Jasper studio

**- Set project in Jasper Studio**

|No|Information|
|-|-|
|1.|Go to project Explore|
|2.|Click File|

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalistJasper_jasperProject1.png" alt="" />


|No|Information|
|-|-|
|1.|Click New|
|2.|Choose Project|

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalistJasper_jasperProject2.png" alt="" />


|No|Information|
|-|-|
|1.|Choose Jaspersoft studio|
|2.|Choose JasperReports Project|
|3.| Click Next|

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalistJasper_jasperProject3.png" alt="" />


|No|Information|
|-|-|
|1.|Name: Write the name of project|
|2.| Click Finish|

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalistJasper_jasperProject4.png" alt="" />



<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalistJasper_jasperProject5.png" alt="" />

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalistJasper_jasperProject6.png" alt="" />

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalistJasper_jasperProject7.png" alt="" />


- Connecting data from datalist Kecak to Jasper Studio

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalistJasper_connectingData.png" alt="" />

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalistJasper_connectingData2.png" alt="" />

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalistJasper_connectingData3.png" alt="" />


- Result

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalistJasper_result.png" alt="" />

