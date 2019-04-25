| Name | Description |
| --- | --- |
| Prefix | Assignment|
| Description | To get Workflow  information of the current assigment |
| Attributes | -#assignment.processId# |
| | - #assignment.processDefId# |
| | - #assignment.processDefIdWithoutVersion# |
| | - #assignment.processName# |
| | - #assignment.processVersion# |
| | - #assignment.processRequesterId# |
| | - #assignment.appId# |
| | - #assignment.activityId# |
| | - #assignment.activityName# |
| | - #assignment.activityDefId# |
| | - #assignment.assigneeId# |
| Scope Of Use | Element Within and part of a process|
| | - Activitity Name |
| | - Form Mapped as part process | 
| | - activity mapping |
| | - Email tool configuration as part of| 
| | - process tool mapping |
| Sample Attributes | To display assingnee's name |
| | #user.{assignment.assigneeId}.firstName# #user.{assignment.assigneeId}.lastName# |
