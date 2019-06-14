# Kecak Excel Import Tool




## Brief Description
Kecak Excel Import Tool is a Kecak plugin for importing data --mainly master data-- from Excel document. This plugin will map the data from each column in your excel worksheet to a form field 



## How to Use
You can use this plugin in a Post Form Submission Processing group located in Form Properties in the form builder after making the upload form

##### 1. Create a new File Upload field in your Form Builder. In advanced options properties tab, specify the file type property to ".xls".
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/excelFormatter1.png" alt="excelFormatter1" />

##### 2. Choose the Excel Import Plugin from the "Post Processing Tool" select box in the Form Properties section of Form Builder
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/excelFormatter2.png" alt="excelFormatter1" />

##### 3. There will be another properties tab belongs to Excel Import Plugin. Click the properties tab.
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/excelFormatter3.png" alt="excelFormatter1" />

##### 4. In the properties tab, you will see the properties belongs to the Excel Import Plugin. You can see the description of each property in the [Properties section](#properties). Fill the mandatory properties marked with `*` and click ok, then you can save your form.
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/excelFormatter4.png" alt="excelFormatter1" />



## <a name="properties"></a> Properties

### Form
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/excelFormatter5.png" alt="excelFormatter1" />

- **ID**    
    `formDefId`  
- **Description**  
    Form that is used to upload the excel file  
- **Mandatory**  
    Yes

--------

### Form Destination
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/excelFormatter6.png" alt="excelFormatter1" />

- **ID**  
    `formDestId`
- **Description**  
    Form that used as a data structures to import data from excel documents  
- **Mandatory**  
    Yes

---------

### Field ID
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/excelFormatter7.png" alt="excelFormatter1" />

- **ID**  
    `fieldId`  
- **Description**  
    The ID of Form Upload field located in selected Form properties. The form upload field is used to upload the excel documents.  
- **Mandatory**  
    Yes

---------

### Properties
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/excelFormatter8.png" alt="excelFormatter1" />

- **ID**  
    `fieldId`  
- **Type**  
    `grid`  
- **Description**  
    Maps data based on column index in excel documents to specified form fields based on their id-s. These form fields must be located in the selected Form in Form Destination properties.  
- **Columns**  
    - Column Number (`excelColNum`)  
         Column index in excel documents. Index start from 0.  
    - Field ID  (`field`)  
         Form field id located in the selected Form Destination  
- **Mandatory**  
    Yes

---------

### Skip First Row
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/excelFormatter9.png" alt="excelFormatter1" />

- **ID**  
    `skipFirstRow`
- **Description**  
    Define if the first row in the excel documents is skipped when importing data. (Check if the first row containing the header for the data)
- **Mandatory**  
    No

---------

### Debug Mode
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/excelFormatter9.png" alt="excelFormatter1" />

- **ID**  
     `debugMode`
- **Description**  
    Mode for debugging the value inserted from the excel. The debug logs will be printed on kecak.log
- **Mandatory**  
    No

---------

### Stop Process When Error
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/excelFormatter10.png" alt="excelFormatter1" />

- **ID**  
    `errorMode`
- **Description**  
    If checked, system will abort importing process when there are error on validating form data.
- **Mandatory**  
    No

---------


## Version Notes & Limitations
- kecak-plugins-excel-import-tool@5ab9e38dbd76d352b78c048eed6123abe6c9cd0d (23-10-2017)
   - Only support excel 2003 document or below (.xls)
   - Erase all commas when inserting value with currency format
