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

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/docker-windows 1.png" alt="dockerwindows" />


### 3. Install Docker 
Install Docker using docker-compose. With adding directory address in command prompt
 Using this in your Command Prompt
```html
docker-compose up
```

which will look like this:

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/docker-windows-2.png" alt="dockerwindows2" />


if there is an access confirmation as shown below, then select "allowed access
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/docker-windows-3.png" alt="dockerwindows3" />


after that, the server can be used as shown below:

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/docker-windows-4.png" alt="dockerwindows4" />


## How To Install Docker in Linux 

### Docker Installation

1. Install docker by typing these on terminal
- in ubuntu
```html
sudo apt-get install docker
```

- in Arch
```html
sudo pacman -S docker
```

2. After installing Docker success, install docker-compose by typing these
ubuntu
```html
sudo apt-get install docker-compose
```

Arch
```html
Sudo pacman -S docker-compose
```
#### Terminology
Sudo : super user do
Pacman : package manager
Arch : arch linux


### Running Docker Kecak Workflow

1. Clone docker kecak workflow

```html
Git clone git@gitlab.com:kinnarastudio/docker-kecak-workflow.git
```

2. Access docker-kecak-workflow directory

```html
cd docker-kecak-workflow
```
3. Start docker service

```html
systemctl start docker
```

4. Run docker-kecak-workflow

5. Wait until kecak workflow running.

6. Server is ready to access if message “Server startup in xxxxx ms” shown

7. Then open localhost:8080 to open kecak workflow
