## Compiling Kecak-Core

### Clone Kecak-Core
clone kecak-workflow to your local ccomputer 
,,,html
https://gitlab.com/kinnarastudio/kecak-workflow
,,,
ot use this
,,,html
git@gitlab.com:kinnarastudio/docker-kecak-workflow.git
,,,
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/compilingcore.PNG" alt="compilingcore" />

### Compile Kecak-Core

Prepare to compile Kecak-Core on your windows :

Open folder wfolw-app , for example : thispc > Local Disk (C:) > wflow-app

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/compilingcore2.png" alt="compilingcore2" />

Run kecak_make.sh shell script or run "mvn -Dmaven.test.skip=true clean install" in wflow-app folder

example :
add command ./kecak_make.sh using gitbash on windows
,,,html
 ./kecak_make.sh
,,,

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/compilingcore3.png" alt="compilingcore3" />