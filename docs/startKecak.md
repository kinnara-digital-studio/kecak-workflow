# Start Kecak

**Via Windows**

1. Open folder Kinnara Studio

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/startKecak.png" alt="startKecak" />

2. Choose and click kecak-start

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/kecak-start.png" alt="kecak-start" />

3. You will see this info if Kecak has been started

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/information-kecak-start.png" alt="startKecakInfo" />

**Via Linux**

Make sure your Kecak in linux is running.

Suppose that kecak-workflow is available in your PC or laptop directory:

Then type this in your command prompt
```
ls -l
```
And you can see the result like in this picture:

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/startKecak_linux1.png" alt="startKecak_linux" />

After that, type this in your command prompt for start Kecak via linux:

```
sh tomcat8.sh start
```

**Via Docker**

Make sure your Kecak docker service is running, otherwise do this step:

1. Start docker service

```html
systemctl start docker
```
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/docker-linux2.png" alt="docker linux" />

2. Run docker-kecak-workflow

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/docker-linux3.png" alt="docker linux" />

3. Wait until Kecak workflow running.
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/docker-linux4.png" alt="docker linux" />

4. Server is ready to access if message “Server startup in xxxxx ms” shown
