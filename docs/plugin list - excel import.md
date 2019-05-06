# Kecak Excel Import Tool




## Brief Description
Kecak Excel Import Tool is a Kecak plugin for importing data --mainly master data-- from Excel document. This plugin will map the data from each column in your excel worksheet to a form field 



## How to Use
You can use this plugin in a Post Form Submission Processing group located in Form Properties in the form builder after making the upload form

##### 1. Create a new File Upload field in your Form Builder. In advanced options properties tab, specify the file type property to ".xls".
![asdf](/uploads/e88560c1a49fb974c43ad5277063c7e1/asdf.PNG)

##### 2. Choose the Excel Import Plugin from the "Post Processing Tool" select box in the Form Properties section of Form Builder
![Post_Form_Submission](/uploads/605defa48a584cfb34d9eae25429fdbf/Post_Form_Submission.PNG)

##### 3. There will be another properties tab belongs to Excel Import Plugin. Click the properties tab.
![Properties_Exce](/uploads/c6f344c519a6bad53294fa9a983090f6/Properties_Exce.PNG)

##### 4. In the properties tab, you will see the properties belongs to the Excel Import Plugin. You can see the description of each property in the [Properties section](#properties). Fill the mandatory properties marked with `*` and click ok, then you can save your form.
![Excel_Import_Tool_Properties](/uploads/c4afba549e20a26687cc73bf6508e20e/Excel_Import_Tool_Properties.PNG)




## <a name="properties"></a> Properties

### Form
![Screenshot_from_2017-10-30_16-40-14](/uploads/cfc40ba78ae186e735d1c63dadbfbdbd/Screenshot_from_2017-10-30_16-40-14.PNG "Form Property")
- **ID**    
    `formDefId`  
- **Description**  
    Form that is used to upload the excel file  
- **Mandatory**  
    Yes

--------

### Form Destination
![sgfdsds](/uploads/a1c33c52705930f9476f101ccb699e2a/sgfdsds.PNG)
- **ID**  
    `formDestId`
- **Description**  
    Form that used as a data structures to import data from excel documents  
- **Mandatory**  
    Yes

---------

### Field ID
![field_ID](/uploads/2e4f36ce869aab3bf2b7100d6c9886ee/field_ID.PNG)

- **ID**  
    `fieldId`  
- **Description**  
    The ID of Form Upload field located in selected Form properties. The form upload field is used to upload the excel documents.  
- **Mandatory**  
    Yes

---------

### Properties
![properties](/uploads/c9728fce2df841b36beabb8518b1c9a1/properties.PNG)

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
![skip_first_row](/uploads/39d2a41ef7b9b27c3c7da2cc723fd8df/skip_first_row.PNG)

- **ID**  
    `skipFirstRow`
- **Description**  
    Define if the first row in the excel documents is skipped when importing data. (Check if the first row containing the header for the data)
- **Mandatory**  
    No

---------

### Debug Mode
![Debug_Mode](/uploads/7faa5e043e9f7651c794f0ae184c848b/Debug_Mode.PNG)

- **ID**  
     `debugMode`
- **Description**  
    Mode for debugging the value inserted from the excel. The debug logs will be printed on kecak.log
- **Mandatory**  
    No

---------

### Stop Process When Error
![stop_process_when_error](/uploads/ffd6cf4daa9fa35df9562384fb8001e8/stop_process_when_error.PNG)

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
