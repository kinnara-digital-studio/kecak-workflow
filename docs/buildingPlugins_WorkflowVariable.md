# Building Plugins #

## Workflow Variable ##

There are many classes of information in any given web aplication, so it is important to understand 
what qualifies as a workflow variable. In Kecak Workflow, Variable typically reperent data elements that need to be reperented as a whole across the entire process. they are generally used to aid condition and decision within your process, and they are not spesific to any singel acativity. For instance, in our sample Leave Application process, the requestor will have to provide information (i.e., date aplication, duration of leave, reason, etc.). While those are data that need stored, they aren't necessary workflow variable since they apply only to that one from and don't really aid in detemining the flow od information in our process.

On the other hand, when supervisior decides to accept/reject a leaave application, the course of process flow is altered, causing activities to be launched and decisions to be actived. Thus, in our sample process, the variable "supervisiorApproval" will have to be a workflow variable 

To create Workflow variable :

1. Point your cursor to ID of activity 
2. Click 

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingPlugins-workflowVariable.png" alt="buildingPlugins-workflowVariable" />

3. Crate a new workflow variable called "supervisiorApproval"

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingPlugins-workflowVariable2.png" alt="buildingPlugins-workflowVariable2" />

4. Look at Workflow Variable in condition

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingPlugins-workfloVariable3.png" alt="buildingPlugins-workfloVariable3" />

