# Form Grid

Plugins From Grid functions to display Grid data which usually can be more than 1 data.
This Form Grid requires an additional form as a place to store data.

The following is data that must be filled when using Form Grid:

*picture 1*
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/formGrid1.png" alt="formGrid" />

#### Information ####
* ID: auto generate Element ID **[Mandatory: YES]**
* Label: Name to be displayed **[Mandatory: NO]**
* Form: additional form selected to save form grid data **[Mandatory: YES]**
* Columns: **[Mandatory: YES]**
* Default Values: **[Mandatory: NO]**

To be able to fill in the Columns field, you must first create an additional form as mentioned earlier to save the data.

## Creating a Data Form (example for Attachment data) ##
1. The following is an example of the contents of the form created:

*image 2
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/formGrid2.png" alt="formGrid" />

*image 3
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/formGrid3.png" alt="formGrid" />

information :
in image 1 is the file name for the form that was created.
in image 2 is the field contained in the form created.

*Parent ID [hidden] as ...
*File Attachment: made from the Edit File Upload plugins (because the attachment file created is a file, so the plugins used are Edit File Attachments)

after the Attachment data storage form (for Form Grid) has been created, the next step is to call the form on the Form Grid to be created.

Here is an example of filling Form Grid data:

*image 4*
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/formGrid4.png" alt="formGrid" />

Information :
* In the Column filling field, is the calling of the Attachment form that was created previously. The data used to call the Attachment Form is the ID of the File Attachment field, namely ** file_attachment ** and then labeled as "File Attachment".

after the data in the Form Grid field is filled in completely, save the data by pressing the OK button on the lower right.

### Version History ###

* ** 1.0.0 **

   * Initial creation: Isti Fatimah
