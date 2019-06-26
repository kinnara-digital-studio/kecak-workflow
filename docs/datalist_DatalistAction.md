# Datalist Action #

Datalist Action allows one to associate a list record(s) to a predefined action/script/executable.  It can be used for columns by configuring the column's properties.

List Datalist Action 

- Hyperlink
- Download PDF
- Delete
- Run Process
- Process Admin 

## Delete Action ##
Drag delete action plugin into row action 

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalist-deletedDrag.png" alt="datalist-deletedDrag" />

Delete Action on Side 

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalist-deleteTop.png" alt="datalist-deleteTop" />

Delete Action on Buttom

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalist-deleteButton.png" alt="datalist-deleteButton" />

#### Delete Properties ####

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalist-deleteProperties.png" alt="datalist-deleteProperties" />

| Name | Description |
|---|---|
| Label | Link / Button Label |
| Form | Form to lookup to for record deletion |
| Confirmation Message | Confirmation message before performing action |
| Delete Associated Grid Data | If Checked, grid elements found in the target form will have its data deleted as well |
| Delete Associated Child Form Data | If checked, child elements found in the target for will have its data deleted as well |
| Abort Related Running Process | if checked, active process associated with the record ID will be aborted |

#### Delete Properties - Visibilty Control ####

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalist-deleteVisibilityControl.png" alt="datalist-deleteVisibilityControl" />

| Name | Description |
|---|---|
| Rules | Controls on when the action should appear |


## Datalist Action - Download PDF ##


<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalist-downloadPDF.png" alt="datalist-downloadPDF" />

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalist-downloadPDFButton.png" alt="datalist-downloadPDFButton" />


#### Download PDF - Properties ####

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalist-downloadPDFproperties.png" alt="datalist-downloadPDFproperties" />

| Name | Description |
|---|---|
| Label | Link/Button label |
| Form |  |
| Record ID Coloumn | Use the id of the datalist row or a column value to load the record |


#### Download PDF - Properties - Advanced ####


|  |  |
|---|---|
| Formatting Options | Options to format and customise the PDF output |

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalist-downloadPDFadvanced.png" alt="datalist-downloadPDFadvanced" />

#### Download PDF - Properties - Visibility Control ####

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalist-downloadPDFvisibiltyControl.png" alt="datalist-downloadPDFvisibiltyControl" />

## Datalist Action - Hyperlink ##


Hyperlink Action allows you add a hyperlink to your record 

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalist-hyperlink.png" alt="datalist-hyperlink" />


<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalist-hyperlinkButton.png" alt="datalist-hyperlinkButton" />


<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalist-hyperlinkTop.png" alt="datalist-hyperlinkTop" />

#### Hyperlink Properties ####


<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalist-hyperlinkButton.png" alt="datalist-hyperlinkButton" />

| Name | Description |
|---|---|
| Hyperlink | Url To Link |
| Label | Link / Button label |
| Visible when no record or checkbox? | If Checked link wiill be visible when no record or checkbox appear in the list. Only appalicable in bulk action place horder|
| hyperlink Target | - Current Window
|  | - New Window |
|  | - Popup Dialog |
| Confirmation Message | Confirmation Message berfore performing action |

#### Hyperlink Parameters #####

Add parameter value to the hyperlink define above

| Name | Description |
|---|---|
| Parameter Name | Parameter Name to be set as part of the URL |
| Column Name | Column Name to retrieve value Form |


#### Hyperlink properties - Visibilty Control ####

<img src= "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalist-hyperlinkVisibilityControl.png" alt="datalist-hyperlinkVisibilityControl />

| Name | Description |
|---|---|
| Rules | Controls on when eh action should appear |




- JSON Datalist Binder 

- SOAP Datalist Binder :
	- Plugins bundel content several SOAP components 
	- Plugins bundel consist of :
		- SOAP options binder : data provider fo element options (Select box / radio button/ check box/ submit button)
		- SOAP tool : send SOAP request 
		- multirow SOAP tool : send SOAP request and use form table record as SOAP body request
		- SOAP datalist binder : data binder for data list table 
		- SOAP binder
		


- JDBC Datalist Binder
- Currency Format
