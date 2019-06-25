## Participant Mapping ##

Participant Mapping digunakan untuk memetakan Participant yang sesuai dengan role's nya.

### Map Partipant to Users ##

Each particaipant declared in Process Design would have its mapping configure here. There are various options to define particaipants.

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/buildingPlugins-mapParticipantToUsers.png" alt="buildingPlugins-mapParticipantToUsers" />

### Process start White list ###


Process Start White List defines who is permitted to start the process. By default, everyone can start the process.  But when configured, only those found in the mapping will be allowed to do so.

### Mapping Options ###

#### Map to Users or Group ####

One may map a participant to a selected user(s) or group(s). By doing so, every time a new activity is created in the participant's swimlane, Kecak will pick up the same resultant user(s) to be assigned as assignee(s).

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