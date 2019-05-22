## Architecture of How the Captcha Plugins Work

here is an overview of how the captha plugins work on kecak workflow.
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/captcha.PNG" alt="kecakMobile-Captcha" />

#### describe :
1. google server sends the client key to the browser (form form element) and to the kecak server (sent to the kecak validator)
2. The browser changes the client key to form a token after sending the token to the workflow kecak validator
3. The Kecak server sends key servers and tokens to the Google server
4. The Google server checks whether the server key and token are valid or not and returns the results of checking to the server kecak validator
5. The validator checks the results of returning data from Google server whether the token is a robot or not and returns to the browser a valid / invalid token

![image](uploads/9ea4c313b4546f6458f2310923a3be86/image.png)

generate site key:

drag and drop icon captcha
![image](uploads/014cabe8cb722c9d6cb00d9e2c4c6e35/image.png)
edit captcha
![image](uploads/125022347142a51c77fe9a7ba3b5448f/image.png)

choosing captcha version
![image](uploads/a8796b3cd8c9c57524f62ff0bdd2f203/image.png)
fill site key field

save
![image](uploads/89ece4b0887491fd5557b6e27eba1e33/image.png)

launch
![image](uploads/dfe3b13d8997b7811687fa29c2536b57/image.png)

Kecak Captcha Version 3
