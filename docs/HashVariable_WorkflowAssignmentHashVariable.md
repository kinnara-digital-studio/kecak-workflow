### Workflow Assignment Hash Variable


---
 **Prefix**  
 ```
 Assignment
 ```
 **Description**  
 ```
 To retrieve information of a workflow process instance 
 ```
 **Attributes** 

 1.   `#assignment.processId#`
 2.   `#assignment.processDefId#` 
 3.   `#assignment.processDefIdWithoutVersion#` 
 4.   `#assignment.processName#` 
 5.   `#assignment.processVersion#` 
 6.   `#assignment.processRequesterId#` 
 7.   `#assignment.appId#` 
 8.   `#assignment.activityId#` 
 9.   `#assignment.activityName#` 
 10.  `#assignment.activityDefId#` 
 11.  `#assignment.assigneeId#` 

 **Scope Of Use** 
 
 - Element Within and part of a process
 
 1. Activitity Name 
 2. Form Mapped as part process  
 3. activity mapping 
 4. Email tool configuration as part of process tool mapping 
 
 **Sample Attributes** 
 
To display assingnee's name 
```
#user.{assignment.assigneeId}.firstName# #user.{assignment.assigneeId}.lastName#
```
