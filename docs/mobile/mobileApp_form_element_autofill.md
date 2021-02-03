# Kecak Mobile - Form Elements #

## Form Element - Autofill Select Box ##
A field button for selecting from a list of items and request data from the API and fill it to the elements in the form.

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/mobile-form-elemnts/textfield.png" alt="" />

## Features

1. Default Value
2. Default Validator
3. Readonly
4. Lazy Loading


## Rest API
The API comes from the autofill selectbox plugin, which is useful for getting data to fill other elements in the form.

#### Url ####
`{host}/web/json/plugin/com.kinnara.kecakplugins.autofillselectbox.AutofillSelectBox/service`

#### Method ####
HTTP POST

#### Headers ####
* Authorization : *Bearer "a token that is obtained at login"*
* Referer : *your server host*
* Content-Type : *application/json*

#### Body ####
* appId: {appId},
* appVersion: {appVersion},
* id: {value obtained when clicking selectbox},
* FIELD_ID: {fieldId},
* FORM_ID: {formId},
* SECTION_ID: {sectionId},
* requestParameter: {}

##### Example #####
`https://sandbox.kecak.org/web/json/plugin/com.kinnara.kecakplugins.mobileapi.LoginApi/service`

### Use of API Plugin ###






## Notes and Limitation on this version
1. Just tested and working with Autofill Loadbinder.