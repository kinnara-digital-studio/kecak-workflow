
# Building Application 

This article will explain how to create great application using Kecak Workflow. No we will create a application about reuimbursement called `Reuimbursement Approval`.

- if requestor want make a submission lest than Rp.500.000,00, requestor should have approval from supervisior 
- if requestor want make a submisson Rp.500.000,00 or more, requestor should have approval form manager
- manager and supervisior can do reject to requestor submission.

## Designing a Process
1. Create new design process 

in the App design ,click on the Process menu on the left , and then the Design Process button along the top 

<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingApplication-createNewDesign.png" alt="buildingApplication-createNewDesign" />
2. Input Name and ID Application
 
<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingApplication-NameId.png" alt="buildingApplication-NameId" />

3. Sample `Reuimbursement Approval` process 

<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingApplication-designProcess.png" alt="buildingApplication-designProcess" />

### Participant

| ID | Name |
|---|---
| requestor | Requestor |
| supervisior | Supervisior |
| manager |	Manager |

### Activities

| ID | Name |
|---|---
| supervisorApprove | Supervisor Approve |
| managerApprove | Manager Approve |
| approvedFinance|	Approved Finance |
| reject | Reject |

### Routes

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


### Tools 

You can add a tool or a plugin for something needed, in this case we need nitification tool 

<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/Tools.png" alt="Tools" />

<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingApplication-buildingApplication-deadlineTools.png" alt="buildingApplication-deadlineTools" />

### Deadline and SLA limits 
 
If supervisor do not make an agreement in one day, a tool send a notification. If Finance do not  make an agreement in one day, appoval will move to manager 

We have to set a time limit for supervisor and Finance activity.

<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/SLA Limits" alt="SLA Limits" />

More detailed explaination. [SLA Limits and Deadline](buildingApplication_deadline.md)


----

- System Administration 
- [SLA Limit, Deadline, Exception](buildingApplication_deadline.md)
- Advanced Insignt into Form Builder 
- Advanced Application Plugins
- Building Front End App
- Insight into Joget Workflow
- [Hash Variable](buildingAplication_HashVariabel.md)
- Builder
- Enterprise
