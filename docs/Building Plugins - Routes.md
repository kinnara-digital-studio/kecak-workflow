## Routes ##

The next thing we'll need to add to our process are routes. Routes are typically nodes where decisions are made and, based on the rules of your process, where the flow of the application maybe altered. Strictly speaking, the position of a route in a swimlane doesn't really matter and will not change the logic of your process. For practicality's sake, however, it will be best to make it as legible as possible.

### Decisions ###

Decisions are used when you want to insert a point in your process where the flow will be determined by a set of rules. For instance, in our Leave Application process, once a requestor submits a leave request, a review by the supervisor will be required. You will see in the figure below that the transition from the requestor to a supervisor happens in a straight line, which tells us that whenever a requestor submits a request, that request will always go to the supervisor and there are no other circumstances or conditions in which that will not be true.

Now, what the supervisor decides to do will result in a decision: either choose to accept or reject the application. If the supervisor rejects it, a mail will be sent to the requestor. If the supervisor accepts it, the application will be forwarded to the HOD. If application must be revise. a mail will be sent to the requestor , the application need to be revised

Given the above, our process will look like the figure below:

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingPlugins-workflowVariable4.png" alt="buildingPlugins-workflowVariable4" />

There is, however, a very important caveat to insert here. Based on the rules above, if the approval value is "accept", the application will route to the HOD. If the approval value is "reject", then an email will be sent. However, if a mistake is made in the form or during user input and the value of approval is neither accept or reject, the process will get stuck because there is no valid rule to follow. It is due to this that the best practices of decision routing recommends using the Otherwise operator in this given scenario. If we make a slight change to the setting above and put the transition to system as Otherwise, we'll end up with:

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingPlugins-otherwise.png" alt="buildingPlugins-otherwise" />

#### Forks #### 

A fork is used when your process needs to split ways concurrently, meaning that your process will be travelling along 2 separate paths (hence, "fork"). For instance, in our given example, once the requestor submits his leave application, a notification is sent to both his supervisor and HOD at the same time. If that is the case, we'll need to add a route, set the split type to "and" and our process will look like:

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingPlugins-routeforks.png" alt="buildingPlugins-routeforks" />

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingPlugins-routeforks.png2" alt="buildingPlugins-routeforks2" />

#### Joins #### 

Joins usually occur when you have separate paths (usually caused by previous forks) in your process that you intend to merge. In our Leave Application process, once the requestor submits his leave application, both the supervisor and HOD are notified. Once they respond, the application ends up at the back office desk for further processing.

There are 2 types of joins:

- And
- OR


#### Operations #### 

- Equal to
- Not Equal to
- Greather Than
- Greater Than or Equal to
- Less Than
- Less Than or Equal to
- Is True
- Is False
- Open Paranthesis
- Close Paranthesis 

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingPlugins-routeOperations.png2" alt="buildingPlugins-routeOperations" />