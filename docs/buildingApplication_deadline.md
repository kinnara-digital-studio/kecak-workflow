## Deadline 

### About Deadline

Deadlines act as a timer which triggers an exception transition to another activity when a specified duration has elapsed. Deadlines can be set for each activity.

We set time limits on each activity, which will trigger an exception (in this case, a specific string variable). When a matching transition that has the condition set to 'exception' matches this string value, the transition will be invoked.

### Type of Deadline 

- Deadline execution can be synchronous or asynchronous.
- For synchronous execution, the current activity will no longer be active when the deadline is triggered.  This is used in cases such as an approval escalation.
- For asynchronous execution, the next activity will be executed while the current activity is still waiting.  This is used in cases such as sending reminders.
- Multiple deadlines are supported for each activity.


### Setting Deadlines in Activities 

- Edit the activity properties you wish to set a deadline for, then click on the Deadlines tab. Add a new row and enter appropriate values for 'Execution', 'Duration Unit' and 'Deadline Limit'.

<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingApplication-deadlineProperties2.png" alt="buildingApplication-deadlineProperties2" />

- Type the exception name in the Exception name field (e.g., Remind). Exception names are case sensitive. Make sure this matches the exception name given to the transition.


- Click on 'OK' to save your deadline configuration. You can as many deadlines as you require.

<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingApplication-settingDeadline.png" alt="buildingApplication-settingDeadline" />

### Setting Transition as Exception Flow 

<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingApplication-deadlineProperties3.png" alt="buildingApplication-deadlineProperties3" />

- When a deadline is triggered, and where the exception name matches the one set for a transition, the flow will execute along that transition to the next element along the workflow.
- Add an Exception-type transition from the activity to the next (i.e., Remind).
- Set the type of condition to 'Exception'.
- Enter an Exception name (e.g., Remind) in the Expression text area.
- Confirm and close the properties window for the transition.

