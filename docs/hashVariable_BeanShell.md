### Bean Shell Hash Variable ###

---

***Prefix***
```
beanShell
```

***Description***

```
Using environment variable to execute bean shell script. Passing parameter using URL query string syntax
```

***Attributes***

```
#beanshell.ENVIRONMENT_VARIABLE#

#beanshell.ENVIRONMENT_VARIABLE[PARAMETERS_URL_QUERY_STRING]#
```

***Scope of Use***

-All components within the App.

***Sample Attributes***

To execute a script stored in "welcome" environment variable with parameter "username" and "dept":

```java

if (username != null && username.length == 1 && !username[0].isEmpty()) {
       return "Welcome " + username[0] + " (" + dept[0] + "),";
} else {
       return "";
}

```

```
#beanshell.welcome[username={currentUser.username}&dept={currentUser.department.name}]#
```

