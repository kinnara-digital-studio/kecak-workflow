# Form Grid

Plugins From Grid functions to display Grid data which usually can be more than 1 data.
This Form Grid requires an additional form as a place to store data.

The following is data that must be filled when using Form Grid:

An example of using Form Grid is in the PT Timah Application for SHP Submission

the data that must be filled in is in figure 1:

*picture 1*
! [image] (/ uploads / 755b8a61a3fc1e56a901ed2e893305cf / image.png)

#### Information ####
* ID: auto generate Element ID ** [Mandatory: YES] **
* Label: Name to be displayed ** [Mandatory: NO] **
* Form: additional form selected to save form grid data ** [Mandatory: YES] **
* Columns: ** [Mandatory: YES] **
* Default Values: ** [Mandatory: NO] **

To be able to fill in the Columns field, you must first create an additional form as mentioned earlier to save the data.

## Creating a Data Form (example for Attachment data) ##
1. The following is an example of the contents of the form created:

* image 2 *
! [image] (/ uploads / 61aa2b80ea95bd8e52babf22fe66984e / image.png)

* image 3 *
! [image] (/ uploads / 242ae3bebd0b056fe9df61101ce2fd2c / image.png)

information :
in figure 1 is the file name for the form that was created.
in figure 2 is the field contained in the form created.

* Parent ID [hidden] as ...
* File Attachment: made from the Edit File Upload plugins (because the attachment file created is a file, so the plugins used are Edit File Attachments)

after the Attachment data storage form (for Form Grid) has been created, the next step is to call the form on the Form Grid to be created.

Here is an example of filling Form Grid data:

* image 4 *
! [image] (/ uploads / b56cac5dc4ebe45ab0d675f3e019fe4e / image.png)

Information :
* In the Column filling field, is the calling of the Attachment form that was created previously. The data used to call the Attachment Form is the ID of the File Attachment field, namely ** file_attachment ** and then labeled as "File Attachment".

after the data in the Form Grid field is filled in completely, save the data by pressing the OK button on the lower right.

### Version History ###

* ** 1.0.0 **
   * Initial creation: Isti Fatimah
