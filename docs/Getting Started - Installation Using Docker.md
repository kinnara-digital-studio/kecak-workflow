## Installed Kecak Workflow Using Docker

### What is Docker
Docker is a computer program that performs operating-system-level virtualization, also known as "containerization".It was first released in 2013 and is developed by Docker, Inc.

Docker is used to run software packages called "containers". Containers are isolated from each other and bundle their own application, tools, libraries and configuration files; they can communicate with each other through well-defined channels. All containers are run by a single operating-system kernel and are thus more lightweight than virtual machines. Containers are created from "images" that specify their precise contents. Images are often created by combining and modifying standard images downloaded from public repositories.

## Installation Docker Workflow in Windows

let's following this step for installation docker workflow in windows :

### 1. Clone docker-kecak-workflow
Preparing your docker-kecak-workflow using git in this link
```html
https://gitlab.com/kinnarastudio/docker-kecak-workflow.git
```
or using this 
```html
git@gitlab.com:kinnarastudio/docker-kecak-workflow.git
```

### 2. Checked your docker-kecak-workflow
after clone docker kecak workflow, checked the file in your computer. for example, see this picture (place the folder after clone) :

![image](uploads/2653b537620a63c219d842bc0399180e/image.png)
x

### 3. Install Docker 
Install Docker using docker-compose. With adding directory address in command prompt
 Using this in your Command Prompt
```html
docker-compose up
```

which will look like this:

![image](uploads/2fb6f793c7bf38cb5b1cb96aa0fb7e75/image.png)


if there is an access confirmation as shown below, then select "allowed access
![image](uploads/4a95cc7cf5871e1c5cf0b45d0a8d344b/image.png)


after that, the server can be used as shown below:
![image](uploads/3d0488dcf32afac6eb0290d26c704e7a/image.png)
