# Scheduler

Scheduler is a software product that allows an enterprise to schedule and track computer batch tasks. These units of work include running a security program or updating software.

Scheduler is a series of job schedule generally used in system managemen (maintenance) , actions and to provide timely services with in the applications.
Using scheduler, you can schedule and trigger integration between Kecak and other systems, or with in kecak system it self. 

For example, if you want to update master data every hours, or 2 hours, or import data from another systems to Kecak.

We over this schedule to simplify to other project.

Integration could be difficult sometimes, and usually people build scheduler apps separately.

# Manage Scheduler

<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/scheduler_manageScheduler.png" alt = "manage scheduler" />

	Add job
	Delete: menghapus scheduler yang ada
	Fire now : trigger scheduler saat itu juga.
Create job 

# Adding Scheduler

How To Adding Sheduler :

1. Click Settings

<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/scheduler_settings.png" alt = "scheduler settings" />

and will be show like this picture

<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/scheduler_manageScheduler.png" alt = "manage scheduler" />

2. Click indicated by arrow no 1

<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/scheduler_manageScheduleDetails.png" alt = "manage scheduler detail" />

3. Fil the form 

<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/scheduler_add.png" alt = "scheduler add" />

Description :

**Job name** : Specifies the job name of the job schedule entries with which you want to work.
**Group Job Name** : Group name for the data set that contains many job names
**Trigger name** : A component that defines the schedule upon which a given Job will be executed.
**Group Trigger Name** : Trigger name for the data set that contains many trigger names
**Job class name** : The name of the class in the program
CRON : the time we have to set, every time this scheduler is running

For the example filling the form, you can see this picture :

<img src = "https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/scheduler_FillAdd.png" alt = "scheduler fill add" />

4. save
After you create the Scheduler, don't forget to click button "save" for saving the scheduler.
