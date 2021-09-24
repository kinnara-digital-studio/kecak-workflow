# How To Install Kecak Workflow with Docker in Linux 

## Docker Installation

### 1. Install docker

#### Install using script
```Shell
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
```
#### Install with package manager
- Ubuntu
```Shell
sudo apt-get install docker
```

- Arch Linux
```Shell
sudo pacman -S docker
```

- CentOS
```Shell
sudo yum install docker-ce docker-ce-cli containerd.io
````
After install start docker
```Shell
sudo systemctl start docker
```

and then add your user to docker group
```Shell
sudo usermod -aG docker $(whoami)
```

### 2. After installing Docker success, install docker-compose by typing these
```Shell
sudo curl -L "https://github.com/docker/compose/releases/download/1.28.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

### Running Docker Kecak Workflow

1. Clone docker kecak workflow

```Shell
git clone git@gitlab.com:kinnarastudio/docker-kecak-workflow.git
```

2. Access docker-kecak-workflow directory

```Shell
cd docker-kecak-workflow
```
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/docker-linux.png" alt="docker linux" />

3. Start docker service

```Shell
systemctl start docker
```
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/docker-linux2.png" alt="docker linux" />

4. Run docker-kecak-workflow

`docker-compose up -d`

5. Show the logs

  `docker-compose logs -f --tail=10`

6. Wait until kecak workflow running.

  <img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/docker-linux4.png" alt="docker linux" />

7. Server is ready to access if message “Server startup in xxxxx ms” shown

8. Then open localhost:8080 to open kecak workflow



