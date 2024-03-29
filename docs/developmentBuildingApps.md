
## Building Application 

This article will explain how to create great application using Kecak Workflow. Now we will create a application about reimbursement called `Reimbursement Approval`.

- If requester want make a submission lest than Rp.500.000,00, requester should have approval from supervisor 
- If requester want make a submisson Rp.500.000,00 or more, requester should have approval form manager
- Manager and supervisor can do reject to requester submission.

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
|---|---|
| requester | Requester |
| supervisor | Supervisor |
| manager |	Manager |

#### Activities

| ID | Name |
|---|---|
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

You can add a tool or a plugin for something needed, in this case we need notification tool 

<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingApplication-deadlineTools.png" alt="buildingApplication-deadlineTools" />

<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingApplication-deadlineTools1.png" alt="buildingApplication-deadlineTools1" />

#### Deadline and SLA limits 

If supervisor do not make an agreement in one day, a tool send a notification. If finance do not  make an agreement in one day, approval will move to manager 

We have to set a time limit for supervisor and finance activity.

More detailed explanation. [SLA Limits and Deadline](buildingApplication_deadline.md)

#### Publishing Process

Publish your process, so you can build every application needed easily. more publishing process explanation [Publishing Process](buildingPlugins_Process)

### Participant Mapping

Participants in Kecak Workflow is the actor who will used this application. More explanation about [Participant Mapping](buildingPlugins_Participant.md)

### Datalist Builder

Datalist Builder offers an intuitive way of constructing a list of cumulative field values from all workflow process instances associated with the selected form. More explanation about [Datalist Builder]() 

### Form Builder 

Makes it easy for you to design your forms. One can easily build a form by using the simplistic and guided interface. [Form Builder](buildingApplication_FormBuilder.md)

### Front End Builder

Build your front and application, you can choose any themes from kecak or build your own theme. [Building Front End App](buildingAplication_BuildingFrontEnd.md)



----
- [Activity](https://kinnara-digital-studio.github.io/kecak-workflow/#/buildingPlugins_Activities)
- [Routes](https://kinnara-digital-studio.github.io/kecak-workflow/#/buildingPlugins_Routes)
- [Publishing Process](https://kinnara-digital-studio.github.io/kecak-workflow/#/buildingPlugins_PublishingProcess)
- [Publishing Application](https://kinnara-digital-studio.github.io/kecak-workflow/#/buildingPlugins_PublishingApps)
- [Participant](https://kinnara-digital-studio.github.io/kecak-workflow/#/buildingPlugins_Participant)
- [Workflow Variable](https://kinnara-digital-studio.github.io/kecak-workflow/#/buildingPlugins_WorkflowVariable)
- [System Administration](https://kinnara-digital-studio.github.io/kecak-workflow/#/buildingApplication_MonitorApps)
- [SLA Limit, Deadline, Exception](https://kinnara-digital-studio.github.io/kecak-workflow/#/buildingApplication_deadline)
- [Advanced Insight into Form Builder](https://kinnara-digital-studio.github.io/kecak-workflow/#/buildingApplication_FormBuilder) 
- Advanced Application Plugins
- [Building Front End App](https://kinnara-digital-studio.github.io/kecak-workflow/#/buildingAplication_BuildingFrontEnd)
- [Hash Variable](https://kinnara-digital-studio.github.io/kecak-workflow/#/buildingAplication_HashVariable)
