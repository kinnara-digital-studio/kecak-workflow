# Building Application 

This article will explain how to create great application using Kecak Workflow. No we will create a application about reuimbursement called `Reuimbursement Approval`.

Create New Design Application

<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingApplication-createNewDesign.png" alt="buildingApplication-createNewDesign" />

<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingApplication-NameId.png" alt="buildingApplication-NameId" />

<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingApplication-designProcess.png" alt="buildingApplication-designProcess" />

| ID | Name |
|---|---
| requestor | Requestor |
| supervisior | Supervisior |
| manager |	Manager |


| ID | Name |
|---|---
| supervisorApprove | Supervisor Approve |
| managerApprove | Manager Approve |
| approvedFinance|	Approved Finance |
| reject | Reject |


|  |  |
|---|---|
| Name | default |
| Style | Straight |
| Type | Condition |
| Use Condition | Yes |
| Condition | Join : AND |
|  | Variable : Status |
|  | Operation : Greater Than |
|  | Value : 500000 |


Do on other condition on this case
