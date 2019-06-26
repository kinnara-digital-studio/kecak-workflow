## Architecture of How the Captcha Plugins Work

here is an overview of how the captha plugins work on kecak workflow.
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/captcha1.png" alt="Captcha" />

#### describe :
1. google server sends the client key to the browser (form form element) and to the kecak server (sent to the kecak validator)
2. The browser changes the client key to form a token after sending the token to the workflow kecak validator
3. The Kecak server sends key servers and tokens to the Google server
4. The Google server checks whether the server key and token are valid or not and returns the results of checking to the server kecak validator
5. The validator checks the results of returning data from Google server whether the token is a robot or not and returns to the browser a valid / invalid token

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/captcha2.png" alt="Captcha" />
generate site key:

drag and drop icon captcha
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/captcha3.png" alt="Captcha" />

edit captcha
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/captcha4.png" alt="Captcha" />

choosing captcha version
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/captcha5.png" alt="Captcha" />
fill site key field

save
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/captcha6.png" alt="Captcha" />

launch
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/captcha7.png" alt="Captcha" />
