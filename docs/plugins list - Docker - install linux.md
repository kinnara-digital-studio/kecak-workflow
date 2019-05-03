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



