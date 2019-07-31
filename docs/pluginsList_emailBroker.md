## How To Development Email Broker ?

1. Make sure your Kecak Workflow are running well
2. Login with administrator account
3. Create new application :

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/3.emailBroker.png" alt="" />


4. Setting application name :

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/4.emailBroker.png" alt="" />


5. After application was created then design new process [click side menu Process]: 

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/5.emailBroker.png" alt="" />


6. On next page [click Design Process]:

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/6.emailBroker.png" alt="" />


7. And Process Builder page was opened :

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/7.emailBroker.png" alt="" />

  a. Sidebar menu contains flow element 
  
  <img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/7.a.emailBroker.png" alt="" />

		
Description :

|     PROCESS     |                           DESCRIPTION                         |
|-----------------|---------------------------------------------------------------|
|**Participant**  |Element for add user or system who do an action on flow (actor)|
|**Activity**     |Element for add Activity to Participant                        |
|**Tool**         |Element for add Tool to Participant                            |
|**Route**        |Dessetion element (for conditional flow)                       |
|**Subflow**      |Element can assign another flow to current flow                |
|**Start**        |Start point                                                    |
|**End**          |End point                                                      |


8. Design Process :

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/8.emailBroker.png" alt="" />

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/8.a.emailBroker.png" alt="" />


9. Setting Kecak Workflow Variable : 

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/9.emailBroker.png" alt="" />


10. Click ok then click deploy button on the top right corner to deploy flow
11. Open map Tools to Plugins to mapping email plugin then [click Add/Edit Plugin ]: 

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/11.emailBroker.png" alt="" />


12. Select email tool plugin:

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/12.emailBroker.png" alt="" />


13. Setting email tool plugin Fill form with SMTP configuration then click Next:

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/13.emailBroker.png" alt="" />


14. Set email contain with Workflow Variable:

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/14.emailBroker.png" alt="" />


  a. From : #variable.from#
  b. To (specific email address): #variable.to#
  c. Cc : #variable.cc#
  d. Bcc: #variable.bcc#
  e. Subject : #variable.title#
  f. Message : #variable.message#
  Note: please Fill the form according setting workflow variable on step 9
  Then click submit
15. Set permission crate process to everyone, including anonymous

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/15.emailBroker.png" alt="" />


16. Ok your application is ready , publish it by clicking “Not Published” 

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/16.emailBroker.png" alt="" />


17. Select your Version then click Publish button :

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/17.emailBroker.png" alt="" />


18. Test Application with API Software e.g Postman (this sample email process request):

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/18.emailBroker.png" alt="" />


