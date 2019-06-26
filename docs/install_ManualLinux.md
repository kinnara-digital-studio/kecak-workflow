
# Install Manual Using Linux

Prepare For Database, Make Sure Your Database Up And Running
Download kecak-workflow From Here Using Command Line (Curl Or Wget)

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/KecakInstallManualLinux1.png" height="80" alt="KecakInstallManualLinux1" />

Extract Downloaded File Using Command tar -xvf [downloaded file]

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/KecakInstallManualLinux2.png" height="60" alt="KecakInstallManualLinux2" />

Navigate To kecak-workflow Folder

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/KecakInstallManualLinux3.png" height="60" alt="KecakInstallManualLinux3" />


(Optional, Not Necessary For Newer Versions) Execute Setup.Sh Using Command :
``` sh setup.sh [host] [dbname] [dbuser] [dbpassword] ```
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/KecakInstallManualLinux4.png" height="60" alt="KecakInstallManualLinux3" />


Wait Until Setup Process Finished
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/KecakInstallManualLinux5.png" alt="KecakInstallManualLinux3" />


To Run Server Type Command : sh tomcat8.sh start
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/KecakInstallManualLinux6.png" height="60" alt="KecakInstallManualLinux3" />


See Running Logs By Typing ``` tail -f apache-tomcat-8.x.20/logs/catalina.out ```
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/manualLinux.png" alt="manualLinux" />

When You See “info [main] org.apache.catalina.startup.catalina.start server startup in xxxx ms” It Means The Server Is Up And Running



