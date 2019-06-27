### Performer Hash Variable

---

***Prefix***
```
performer
```

***Description***

```
To gert User Information of an Actiivity
```

***Attribute***

```	
1. #performer. activityDefId .id#
2. #performer. activityDefId .username#
3. #performer. activityDefId .firstName#
4. #performer. activityDefId .lastName#
5. #performer. activityDefId .email#
6. #performer. activityDefId .active#
7. #performer. activityDefId .timeZone#
```

***Scope of Use***
- Elements within and of a process
1. Activity Name
2. Form mapped as part of process activity mapping 
3. Email Tool configuration as part of process tool mapping

```
To get activityDefId (activity definition ID), mouseover the activity name in the Activity Mapping tab (Workflow Management Console); there will be an overlay showing the ID.
```

```
The activity chosen must had already been performed.
```

***Sample Attribute***
`#performer.runProcess.firstName#`

`#performer.submitLeave.firstName#`

