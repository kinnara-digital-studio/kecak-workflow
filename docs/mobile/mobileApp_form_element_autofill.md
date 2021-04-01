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
- Add Token
![auth](https://user-images.githubusercontent.com/63091778/106728909-86794780-663f-11eb-9ddd-bf9c99ee99c0.png)

- Headers
![auth_autofill](https://user-images.githubusercontent.com/63091778/106729081-b9bbd680-663f-11eb-8a14-b975ca195326.png)

- Body
 ![body](https://user-images.githubusercontent.com/63091778/106729244-dc4def80-663f-11eb-8658-b7fd40c523e2.png)
 
- Result
![result](https://user-images.githubusercontent.com/63091778/106729331-ee2f9280-663f-11eb-9196-3ad9a76bcd91.png)






## Notes and Limitation on this version
1. Just tested and working with Autofill Loadbinder.
