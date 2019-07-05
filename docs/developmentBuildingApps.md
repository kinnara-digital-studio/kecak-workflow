
## Building Application 

This article will explain how to create great application using Kecak Workflow. No we will create a application about reimbursement called `Reimbursement Approval`.

- If requestor want make a submission lest than Rp.500.000,00, requestor should have approval from supervisior 
- If requestor want make a submisson Rp.500.000,00 or more, requestor should have approval form manager
- Manager and supervisior can do reject to requestor submission.

### Designing a Process
1. Create new design process 

In the App design ,click on the Process menu on the left , and then the Design Process button along the top 

<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingApplication-createNewDesign.png" alt="buildingApplication-createNewDesign" />

2. Input Name and ID Application
 
<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingApplication-NameId.png" alt="buildingApplication-NameId" />

3. Sample `Reimbursement Approval` process 

<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingApplication-designProcess.png" alt="buildingApplication-designProcess" />

#### Participant

| ID | Name |
|---|---
| requestor | Requestor |
| supervisior | Supervisior |
| manager |	Manager |

#### Activities

| ID | Name |
|---|---
| supervisorApprove | Supervisor Approve |
| managerApprove | Manager Approve |
| approvedFinance|	Approved Finance |
| reject | Reject |

#### Routes

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


#### Tools 

You can add a tool or a plugin for something needed, in this case we need nitification tool 

<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingApplication-deadlineTools.png" alt="buildingApplication-deadlineTools" />

<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingApplication-deadlineTools1.png" alt="buildingApplication-deadlineTools1" />

#### Deadline and SLA limits 
 
If supervisor do not make an agreement in one day, a tool send a notification. If finance do not  make an agreement in one day, appoval will move to manager 

We have to set a time limit for supervisor and finance activity.

More detailed explaination. [SLA Limits and Deadline](buildingApplication_deadline.md)

#### Publishing Process

Publish your process, so you can build every application needed easily. more publishing process explanation [Publishing Process](buildingPlugins_Process)

### Participant Mapping

Participants in Kecak Workflow is the actor who will used this application. More explaination about [Participant Mapping](buildingPlugins_Participant.md)

### Datalist Builder

Datalist Builder offers an intuitive way of constructing a list of cumulative field values from all workflow process instances associated with the selected form. More explaination about [Datalist Builder]() 

### Form Builder 

Makes it easy for you to design your forms. One can easily build a form by using the simplistic and guided interface. [Form Builder](buildingApplication_FormBuilder.md)

### Front End Builder

Build your front and application, you can choose any themes from kecak or build your own theme. [Building Front End App](buildingAplication_BuildingFrontEnd.md)



----

- [System Administration](buidingApplication_MonitorApps.md)
- [SLA Limit, Deadline, Exception](buildingApplication_deadline.md)
- [Advanced Insight into Form Builder](buildingApplication_FormBuilder.md) 
- Advanced Application Plugins
- [Building Front End App](buildingAplication_BuildingFrontEnd.md)
- [Hash Variable](buildingAplication_HashVariabel.md)
