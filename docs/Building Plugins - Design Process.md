## Process ##

### Design Process ###

Design process will open up the Process Builder. Process builder is one of the highlights of Kecak Workflow
where it allows one to design process on the browser itself without the need of installing any additional components

The Process Builder allows one to design processes for the Kecakworkflow App in an eay drag and drop manner.

 

## Create new Design Process ##

- click new design app 

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingPlugins-new.png" alt="buildingPlugins-new" />

- App ID  and And App Name 

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingPlugins-new2.png" alt="buildingPlugins-new2" />

``` 
you can duplicate form existing process
```


<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingPlugins-new4.png" alt="buildingPlugins-new4" />

- Save 

### Element Available ###

#### Participant ####

The Participant node is used to declare a new participant swimlane to represent a role (e.g. Requester). Map Participant Users for more infomation about the mapping 

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingPlugins-participant.png" alt="buildingPlugins-participant" />

| Name | Description |
|---|---|
| ID | Partcipant ID |
| Name | Partcipant Name |

#### Activity ####

Activity node is used to be mapped to Form, to be used to interact with human participant, as part of the flow. 

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingPlugins-activity.png" alt="buildingPlugins-activity" />

| Name | Description |
|---|---|
| ID | Activity ID |
| Name | Activity Name |

| Name | Description |
|---|---|
| Deadline | Multiple Deadline can be set for each activity learn about Deadlines and Escalation. |

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingPlugins-Deadlines.png" alt="buildingPlugins-deadlines" />

| Name | Description |
|---|---|
| Limit | SLA Limit for the activity. Number is expected in this field |

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingPlugins-limit.png" alt="buildingPlugins-limit" />

#### Tool ####

Tool node is used to be mapped to Process Tool Plugins, to trigger/archive certain functionality programmatically, as part of the process flow.

| Name | Description |
|---|---|
| ID | Tool ID |
| Name | Tool Name |

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingPlugins-tool.png" alt="buildingPlugins-tool" />

#### Route ####

Route node is used to detemine the flow of the process

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingPlugins-route.png" alt="buildingPlugins-route" />

| Name | Description |
|---|---|
| ID |Root ID |
| Name | Route Name |
| Join Type | Incoming transition treatment type |
| Split Type | Outgoing transition treatment type |

#### Subflow ####

Subflow node is used to trigger the start of another process under the same App.

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingPlugins-subflow.png" alt="buildingPlugins-subflow" />

| Name | Description |
|---|---|
| ID | Subflow ID |
| Name | Subflow Name|
| SubProcessID | Process ID of the Subflow |
| Execution | Synchronous or Asynchronous execution |
| Parameters | Workflow variable(s) to be passed over to the Subflow |




