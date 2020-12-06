## Deadline 

### About Deadline

Deadlines acts as a timer that sets the time limit for each activity executed.


### Type of Deadline 

- Deadline execution can be synchronous or asynchronous.
- For synchronous execution, Current activity will not be active whe the deadline is trigered.
- For asynchronous execution, the next activity will continue even though the current activity is still waiting.
- Multiple deadlines are supported for each activity.


#### Setting Deadlines in Activities 

- Edit the activity properties, choose Deadlines menu.

<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingApplication-deadlineProperties.png" alt="buildingApplication-deadlineProperties" />




#### Setting Transition as Exception Flow

- When a deadline is triggered, and where the exception name matches the one set for a transition, the flow will execute along that transition to the next element along the workflow.
- Add an Exception-type transition from the activity to the next (i.e., Send Reminder).
- Set the type of condition to 'Exception'.
- Enter an Exception name (e.g., TIMEOUT) in the Expression text area.
- Confirm and close the properties window for the transition.

---

## SLA (Service Level Agreement)

### About SLA 

Service Level Agreement (SLA) can be incorporated in the process design as a means for the process owner to define and maintain quality of services.

In Kecak, SLA may be implemented at the process level and, typically, at the activity level.

Before such report can be generated, one must first enable the Process Data Collector at each intended Kecak App.

### Why Set Limits 

- By setting limits to workflow activities, you are able to define appropriate service levels for your processes.
- Participants in the workflow can be made aware of adherence to these service levels.
- You can generate reports to determine the efficiency of your processes (e.g., identifying bottlenecks) 

### Setting SLA Limits
