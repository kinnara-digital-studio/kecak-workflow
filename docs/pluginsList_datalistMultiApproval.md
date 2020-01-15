# Datalist Multi Approval

This multi-approval catalyst is a plugin that functions as a plugin that can send data to more than one approver.


## How to use

1. Open Apps

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalistMultiApproval_openApps.png" alt="" />


2. Go to process

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalistMultiApproval_process.png" alt="" />


3. Choose process

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalistMultiApproval_chooseProcess.png" alt="" />


5. Choose add & edit mapping in user

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalistMultiApproval_editMapping.png" alt="" />


6. Go to tab map to plugin

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalistMultiApproval_mapToPlugin.png" alt="" />


7. Choose datalist multi approval

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalistMultiApproval_pluginChoose.png" alt="" />


8. datalis multi approval choosed

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalistMultiApproval_pluginChoosed.png" alt="" />


9. submit

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalistMultiApproval_submit.png" alt="" />


### - Datalist Multi Approval Configuration

1. Datalist multi approval settings

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalistMultiApproval_settings.png" alt="" />

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalistMultiApproval_settings2.png" alt="" />


|Field|Description|
|-|-|
|Datalist Binder|Where will you take the datalist binder form|
|Field ID|Field which contains username. Leave empty if you want to use dataList ID as username|
|Sequenced Approval Type|Parallel : sending approval in parallel; Sequential : sending approval in sequence|
|Control Variable|workflow variable name that Will be empty if the approval looping process has finished|
|Starting Point|Any process component (activity/tool/route) that will be used as starting point of approval looping process|

**nama** in field "Field ID" is from ID in datalist master data approver that will be sending approval.

a. Choose Datalist in apps

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/from_listMd.png" alt="" />

b. Edit properties for know ID row

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/from_properties.png" alt="" />

c. ID from the row

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/from_id.png" alt="" />


2. Go to tab datalist binder

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalistMultiApproval_datalistBinder.png" alt="" />

|Field|Description|
|-|-|
|Form|Choose the form for the datalist binder|
|Extra Filter Condition|Additional filters if needed|


3. Choose form datalist binder

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalistMultiApproval_chooseForm.png" alt="" />

4. Form choosed

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalistMultiApproval_formChoosed.png" alt="" />

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalistMultiApproval_formChoosed2.png" alt="" />


3. Click submit

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/datalistMultiApproval_submitSettings.png" alt="" />


4. result

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/.png" alt="" />
