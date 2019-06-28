## Deadline 

### About Deadline

Deadlines act as a timer which triggers an exception transition to another activity when a specified duration has elapsed. Deadlines can be set for each activity.

We set time limits on each activity, which will trigger an exception (in this case, a specific string variable). When a matching transition that has the condition set to 'exception' matches this string value, the transition will be invoked.

### Type of Deadline 

- Deadline execution can be synchronous or asynchronous.
- For synchronous execution, the current activity will no longer be active when the deadline is triggered.  This is used in cases such as an approval escalation.
- For asynchronous execution, the next activity will be executed while the current activity is still waiting.  This is used in cases such as sending reminders.
- Multiple deadlines are supported for each activity.

### Setting Deadlines

#### Activating Deadline Checker

- The activity deadline checker is disabled by default. To allow activity deadline checking by Kecak Workflow, you will have to set the Process Deadline Checker Interval to a non-zero value. Set it to a suitable value, depending on your need.
- To enable deadline checking, go to the Admin Bar > General Settings > Timer Settings.
- Change the value for 'Process Deadline Checker Interval' to a non-zero value, i.e. 30 (seconds). The unit used is seconds.

#### Setting Deadlines in Activities 

- Edit the activity properties you wish to set a deadline for, then click on the Deadlines tab. Add a new row and enter appropriate values for 'Execution', 'Duration Unit' and 'Deadline Limit'.
- Type the exception name in the Exception name field (e.g., TIMEOUT). Exception names are case sensitive. Make sure this matches the exception name given to the transition.
- Click on 'OK' to save your deadline configuration. You can as many deadlines as you require.

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
