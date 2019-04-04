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