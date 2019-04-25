### Workflow Process Hash Variable



---
| Name | Description |
| --- | --- |
| Prefix | Process|
| Description | To retrieve information of a workflow process instance |
| Attributes | 1. `#process.appId#` |
|  | 2. `process.processId#` |
|  ^  | 3. `#process.processDefId#` |
|  | 4. `#process.processDefIdWithoutVersion#` |
|  | 5. `#process.processName#` |
|  | 6. `#process.processVersion#` |
|  | 7. `#process.processRequesterId#` |
|  | 8. `#process.state#` |
|  | 9. `#process.startedTime#` |
|  | 10. `#process.limit#` |
|  | 11. `#process.due#` |
|  | 12. `#process.delay#` |
|  | 13. `#process.delayInSeconds#` |
|  | 14. `#process.finishTime#` |
|  | 15. `#process.timeConsumingFromDateStarted#` |
|  | 16. `#process.timeConsumingFromDateStartedInSeconds#` |
|  | 17. `#process.activityInst.ACTIVITY_DEF_ID.instanceId#`|
|  | 18. `#process.activityInst.ACTIVITY_DEF_ID.name#` |
|  | 19. `#process.activityInst.ACTIVITY_DEF_ID.status#` |
|  | 20. `#process.activityInst.ACTIVITY_DEF_ID.state#` |
|  | 21. `#process.activityInst.ACTIVITY_DEF_ID.type#` |
|  | 22. `#process.activityInst.ACTIVITY_DEF_ID.startedTime#` |
|  | 23. `#process.activityInst.ACTIVITY_DEF_ID.limit#` |
|  | 24. `#process.activityInst.ACTIVITY_DEF_ID.limitInSeconds#` |
|  | 25. `#process.activityInst.ACTIVITY_DEF_ID.due#` |
|  | 26. `#process.activityInst.ACTIVITY_DEF_ID.delay#` |
|  | 27. `#process.activityInst.ACTIVITY_DEF_ID.delayInSeconds#` |
|  | 28. `#process.activityInst.ACTIVITY_DEF_ID.finishTime#` |
|  | 29. `#process.activityInst.ACTIVITY_DEF_ID.timeConsumingFromDateStarted#` |
|  | 30. `#process.activityInst.ACTIVITY_DEF_ID.timeConsumingFromDateStartedInSeconds#` |
|  | 31. `#process.activityInst.ACTIVITY_DEF_ID.performer#` |
|  | 32. `#process.activityInst.ACTIVITY_DEF_ID.performerUser#` |
|  | 33. `#process.activityInst.ACTIVITY_DEF_ID.assignmentUsers#` |
|  | 34. `#process.appId[PROCESS_INSTANCE_ID]#` |
|  | 35. `#process.processDefId[PROCESS_INSTANCE_ID]#` |
|  | 36. `#process.processDefIdWithoutVersion[PROCESS_INSTANCE_ID]#` |
|  | 37. `#process.processName[PROCESS_INSTANCE_ID]#` |
|  | 38. `#process.processVersion[PROCESS_INSTANCE_ID]#` |
|  | 39. `#process.processRequesterId[PROCESS_INSTANCE_ID]#` |
|  | 40. `process.startedTime[PROCESS_INSTANCE_ID]#` |
|  | 41. `#process.limit[PROCESS_INSTANCE_ID]#` |
|  | 42. `#process.due[PROCESS_INSTANCE_ID]#` |
|  | 43. `#process.delay[PROCESS_INSTANCE_ID]#` |
|  | 44. `#process.delayInSeconds[PROCESS_INSTANCE_ID]#` |
|  | 45. `#process.finishTime[PROCESS_INSTANCE_ID]#` |
|  | 46. `#process.timeConsumingFromDateStarted[PROCESS_INSTANCE_ID]#` |
|  | 47. `#process.timeConsumingFromDateStartedInSeconds[PROCESS_INSTANCE_ID]#` |
|  | 48. `#process.activityInst.ACTIVITY_DEF_ID.instanceId[PROCESS_INSTANCE_ID]#` |
|  | 49. `#process.activityInst.ACTIVITY_DEF_ID.name[PROCESS_INSTANCE_ID]#` |
|  | 50. `#process.activityInst.ACTIVITY_DEF_ID.status[PROCESS_INSTANCE_ID]#` |
|  | 51. `#process.activityInst.ACTIVITY_DEF_ID.state[PROCESS_INSTANCE_ID]#` |
|  | 52. `#process.activityInst.ACTIVITY_DEF_ID.type[PROCESS_INSTANCE_ID]#` |
|  | 53. `#process.activityInst.ACTIVITY_DEF_ID.startedTime[PROCESS_INSTANCE_ID]#` |
|  | 54. `#process.activityInst.ACTIVITY_DEF_ID.limit[PROCESS_INSTANCE_ID]#` |
|  | 55. `#process.activityInst.ACTIVITY_DEF_ID.limitInSeconds[PROCESS_INSTANCE_ID]#` |
|  | 56. `#process.activityInst.ACTIVITY_DEF_ID.due[PROCESS_INSTANCE_ID]#` |
|  | 57. `#process.activityInst.ACTIVITY_DEF_ID.delay[PROCESS_INSTANCE_ID]#` |
|  | 58. `#process.activityInst.ACTIVITY_DEF_ID.delayInSeconds[PROCESS_INSTANCE_ID]#` |
|  | 59. `#process.activityInst.ACTIVITY_DEF_ID.finishTime[PROCESS_INSTANCE_ID]#` |
|  | 60. `#process.activityInst.ACTIVITY_DEF_ID.timeConsumingFromDateStarted[PROCESS_INSTANCE_ID]#` |
|  | 61. `#process.activityInst.ACTIVITY_DEF_ID.timeConsumingFromDateStartedInSeconds[PROCESS_INSTANCE_ID]#` |
|  | 62. `#process.activityInst.ACTIVITY_DEF_ID.performer[PROCESS_INSTANCE_ID]#` |
|  | 63. `#process.activityInst.ACTIVITY_DEF_ID.performerUser[PROCESS_INSTANCE_ID]#` |
|  | 64. `#process.activityInst.ACTIVITY_DEF_ID.assignmentUsers[PROCESS_INSTANCE_ID]#` |
| Scope Of Use | Element Within and part of a process|
| | 1. Activitity Name |
| | 2. Form Mapped as part process activity mapping | 
| | 3. activity mapping |
| | 4. Email tool configuration as part of process tool mapping| 
| Sample Attributes | To display the performer username of an activity instance of a process instance: |
| | `#process.activityInst.assign.performerUser[{assingment.processId}]#` |
