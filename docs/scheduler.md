# Scheduler

Scheduler is a software product that allows an enterprise to schedule and track computer batch tasks. These units of work include running a security program or updating software.

Scheduler is a series of job scheduling generally used in system management (maintenance), actions and to provide timely services with in the applications.
Using scheduler, you can schedule and trigger integration between Kecak and other systems, or with in kecak system. 

For example, if you want to update master data every hours, or 2 hours, or import data from another systems to Kecak.

We offers this build in schedule to simplify project integration to other project.

Integration could be difficult sometimes, and usually people build scheduler apps separately.

# Manage Scheduler

<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/scheduler_manageScheduler.png" alt = "manage scheduler" />

<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/scheduler_manageScheduleDetails.png" alt = "manage scheduler detail" />

**DESCRIPTION**

	**1. Add Process Scheduler** : Create a new scheduler

	**2. Delete:** Delete selected scheduler

	**3. Fire now:** Trigger selected scheduler at current time



# Adding Scheduler

How To Adding Sheduler :

1. Click Settings

<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/scheduler_settings.png" alt = "scheduler settings" />

and will be show like this picture

<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/scheduler_manageScheduler.png" alt = "manage scheduler" />

2. Click indicated by arrow no 1

<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/scheduler_manageScheduleDetails.png" alt = "manage scheduler detail" />

3. Fill the form 

<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/scheduler_add.png" alt = "scheduler add" />

|            FIELD              |                                  DESCRIPTION                                  |
|-------------------------------|-------------------------------------------------------------------------------|
|**Job name**                   |Specifies the job name of the job schedule entries with which you want to work |
|**Group Job Name**             |Group name for the data set that contains many job names                       |
|**Trigger name**               |A component that defines the schedule upon which a given Job will be executed  |
|**Group Trigger Name**         |Trigger name for the data set that contains many trigger names                 |
|**Job-Class Name**             |Name of Class Scheduler in the program apps                                    |
|**CRON**                       | the time we have to set, every time this scheduler is running                 |


For the example filling the form, you can see this picture :

<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/scheduler_FillAdd.png" alt = "scheduler fill add" />

4. Save

After you create the scheduler, don't forget to click button "save" for saving the scheduler.
