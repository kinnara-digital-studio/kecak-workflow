## Overview
Email Approval is one way approval is implemented on email (not through the form), so approval can be done more easily without having to open the website first.

## Email Format

Example of very basic Email Tool setting

`<a href="mailto:admin@domain?subject=Approval processId:#assignment.processId#&body=approved" data-rel="external">APPROVE</a>`
`<br>`
`<br>`
`<a href="mailto:admin@domain?subject=Revision processId:#assignment.processId#&body=revised" data-rel="external">REVISE</a>`
`<br>`
`<br>`
`<a href="mailto:admin@domain?subject=Rejection processId:#assignment.processId#&body=rejected" data-rel="external">REJECT</a>`

## Approval Settings

##### Parameters
* ID - Application ID
* Process ID - Application Version (optional) : if not included will retrieve the latest version
* Activity ID - The activity ID to do
* Content - display any pop-ups that will be sent to the corresponding email.

Here is an example of filling fields in email approval:

![image](/uploads/00468fab18ec8d41be0458f963b4d9f9/image.png)


1. Open Options * Settings * on the options on the right
2. Select * Email Approval Content * on the left selection
3. Click * Approval * on Activity ID

![image](/uploads/81303da410e5873880727a63ea41f686/image.png)

## How to Determine what Content will be displayed in Email Approval ##

in this case are an example for content Arya Noble email approval, the content is
``` javascript
Approval Status: {form_sf_approval_sf_appr_approval} {form_sf_approval_sf_appr_sf_remark_sf_app_history_approval_status} Remarks:[{form_sf_approval_sf_appr_sf_remark_sf_app_history_remark}]{nouse}
```

### * For {form_sf_approval_sf_appr_approval} ###
#### the order is as follows : ####
* form - form
* sf_approval - subform (if its form type is subform)
* sf_appr subform (if its form type is subform)
* approval - 

#### The trick is as follows: ####
1. open *All Apps* in the menu options on the right
2. Select *Payment Request Application*
3. Find the form which will be taken (in this case the form will be taken is EAF-Approval form)
4. Select the approval form and edit from approval to see the ID of the Approval Form (green box) and what form it uses (blue box)
