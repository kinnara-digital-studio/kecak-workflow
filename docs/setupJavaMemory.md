# Setup Java Memory

**Via Windows**

For setup java memory, open file with the name “tomcat8-start.bat” using Notepad Or Notepad++, then you will see like this:

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/java.png" alt="java" />


**Via Linux**

Setup java memory Kecak in linux

Export:
```
JAVA_OPTS="Xmx512M -Dwflow.home+./wflow/ "
```

```
apache-tomcat-8.5.20/bin/catalina.sh $*
```

**Via Docker**

Setup java memory Kecak in docker

From tomcat:8.5

Copy kecak.war
```
/usr/local/tomcat/webapps
```

Run 

```
rm -rf /usr/local/tomcat/webapps/ROOT ; \
```

```
mv /usr/local/tomcat/webapps/kecak.war/usr/local/tomcatwebapps/ROOT.war ; \
```

```
mkdir /usr/local/tomcat/wflow ; \
```

```
echo '' >> /usr/local/tomcat/wflow/app_datasource.properties ; \
```

```
echo '' >> /usr/local/tomcat/wflow/app_datasource-default.properties ; \
```

```
chmod -R 755 /usr/local/tomcat/wflow/

```

Copy

```
tomcat8.sh /usr/local/tomcat/
```

expose 8080

expose 8000

```ENV JAVA_MEMORY=${JAVA_MEMORY}```

```ENV TOMCAT_DEBUG=${TOMCAT_DEBUG}```

```CMD ["/usr/local/tomcat/tomcat8.sh", "run"]```


**Information :**
- The Memory Server For Example Is 12gigabytes

- The Limit Used For Java Is A Maximum Of 70% Of The Server's Memory, In Megabytes (Mb)
