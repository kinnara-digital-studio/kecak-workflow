## Map to Users or Group ##

up the same resultant user(s) to be assigned as assignee(s).

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingPlugins-mapToUsersOrGroup.png" alt="buildingPlugins-mapToUsersOrGroup" />

```
If the user mapped is inactive at the time of assignment is made, the particular user will not be assigned.
```

```
If there's no valid user found to be assigned to at the time of assignment is made, the activity will be assigned to the current logged in user (previous performer).
```

#### Map to Org Chart ####

One may also map to a participant in relation to the reporting organization chart and the performers/participants in the process flow.

For example, we are trying to determine the participant that will be approving a claim. The person to approve a claim will be the HOD of the person that submitted the claim. Therefore, the setting would be "Performer's HOD where the performer executed Submit Claim"

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingPlugins-mapToOrgChart.png" alt="buildingPlugins-mapToOrgChart" />

#### Map to WorkFlow Variable ####

A participant can also be determined through the use of Workflow Variable. This is particularly useful when the participant is decided on-the-fly in the preceding activity with the value set into the Workflow Variable. If the variable value contains a username, then, the option to be set here would be "Username" with the correct Variable ID picked.

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingPlugins-mapToWorkflowVariable.png" alt="buildingPlugins-mapToWorkflowVariable" />

#### Map to Plugins ####

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingPlugins-mapToPlugins.png" alt="buildingPlugins-mapToPlugins" />
