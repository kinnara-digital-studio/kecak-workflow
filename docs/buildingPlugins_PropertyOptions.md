# Property 

## Decide Your Plugin Property Options 

- What kind of information needed from user ?

1. Which from to update ?
2. Which record in the form data ?
3. Wich field to modify ?
4. What is the value needed to set to the particular field?

## Introduction to Property Options 

- Json Format

```
	[

	{

		title : ‘Page Title',

		properties : [

	{

		name : ‘Property Name',

		label : ‘Property Label',

		type : ‘Property Type',

		required : ‘Mandatory or Not‘

		, … more property attributes …

		} , … more properties …

	],

	validators : [

		… properties custom validators …

		]

	} … more properties page …

	]
	
```

## Property Options Type


- Hidden field - hidden

- Read only field – readonly

- Text field – textfield

- Password field – password

- Text area field – textarea

- HTML editor field – htmleditor

- Checkbox field – checkbox

- Radio button field – radio

- Select box field – selectbox

- Multi select box field – multiselect

- Element (Plugin) select field – elementselect

- Grid field - grid


#### Common attributes from all property options type except hidden field and grid 

```json
{
	name : `Property Name`,
	label : `Property Label`,
	descriptions : `Property Descriptions` // optional, default is null,
	type : `readonly`,
	value : `Property Value` // optional, default is empty string
	required : `true` // optional, boolean value, default is false
}

```

#### Extra attributes for text field, password field, text area and HTML editor

```json

{
	size : ‘50’, //optional , integer value, default is NULL, only for text field and password field

	maxlength : ‘50’, //optional, integer value, default is NULL, only for text field and password field

	rows : ‘50’, //optional, integer value, default is NULL, only for text area and html editor

	cols : ‘50’, //optional, integer value, default is NULL , only for text area and html editor

	regex_validation : ‘^[a-zA-Z0-9_]+$’, //optional, default is NULL

	validation_message : ‘Error!!’ //optional, default is NULL
}

```

#### Extra attributes for checkbox, radio button, select box and multiple select box

``` json

{

	size : ‘10’, //optional, integer value, default is 4, only for multi
	select box

	options : [ //is optional to use this attribute or options_ajax

	{value: ‘value1’, label : ‘Value 1’},

	{value: ‘value2’, label : ‘Value 2’},

	{value: ‘value3’, label : ‘Value 3’}

	],

	options_ajax_on_change : ‘property1’, //optional, value of this property name will passed over to load options from ajax, only for select
	box and multi select box

	options_ajax : ‘URL to load options JSON’ //optional, URL return JSON Array of a set of Objects that have value & label attribute

}

```

#### Attributes of hidden field 

```json
{
	name : `Property Name`,
	type : `hidden`,
	value : `Property Value`
}

```

#### Attributes for grid

```json
{
	name : `Property Name`
	
	label : ‘Property Label’,

	description : ‘Property Description’, //optional, default is NULL

	type : ‘grid’,

	columns : [ // 2 type of column, with and without options attribute

	{key : ‘col1’, label : ‘Col 1’},

	{key : ‘col2’, label : ‘Col 2’,

	options:[

		{value :‘option1’, label : ‘Option 1’},

		{value :‘option2’, label : ‘Option 2’}

		]

	},

]

	value : [ //optional, default is NULL

		{col1 : ‘abc’, col2 : ‘option1’},

		{col1 : ‘def’, col2 : ‘option2’}

],

required : ‘true’, //optional, boolean value, default is false

}

````

####  Extra element select field

```json

{

	options_ajax_on_change : ‘property1’, //optional, value of this
	property name will passover to load options from ajax

	options_ajax :
	‘[CONTEXT_PATH]/web/property/json/getElements?classname=
	org.joget.apps.form.model.FormLoadElementBinder’, //Load plugin list based
	on class name given

	url : ‘[CONTEXT_PATH]/web/property/json/getPropertyOptions’ //Load
	plugin properties

}
```
#### Property validator types 

- Currently only one validator type supported - Ajax

```html

{
	type : ‘ajax’,

	url : ‘URL to validate properties page value’ ,

	// All properties in the same page will send to this url to validate,
	URL return a JSON Object with status (success or fail) & message (JSONArray of String) attribute

	default_error_message : ‘Error in this page!!’ //optional, default is null
}

```

## Create Your Plugin Properties 

1. In your maven project, right and create new folder 
2. key in `src/main/resources/properties` and press finish 
2. Under `Other Sources` > `src/main/resources` > `properties`, create new empty file named `formDataUpdateTool.json`
4. Put the following code in your public String `getPropertyOptions()` method

	```java
		return AppUtil.readPluginResource(getClass().getName(),
		"/properties/formDataUpdateTool.json", null, true, null);
	```
5. Construct your plugins properties in `formDataUpdateTool.json`.
