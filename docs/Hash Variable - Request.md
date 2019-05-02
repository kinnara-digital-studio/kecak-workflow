###Request Hash Variable###

---

***Prefix***

```
request
```

***Description***

```
To get the value from the current HttpServletRequest object of the page view.
```

***Attributes***

```
1.  #request.characterEncoding#
2.  #request.contextPath#
3.  #request.header.NAME# , where NAME is the custom header name.
4.  #request.locale#
5.  #request.method#
6.  #request.pathInfo#
7.  #request.protocol#
8.  #request.queryString#
9.  #request.remoteAddr#
10  #request.requestURI#
11. #request.requestURL#
12. #request.requestedSessionId#
13. #request.scheme#
14. #request.serverName#
15. #request.serverPort#
16. #request.servletPath#
```

***Scope of Use***

- All components within the App where there is valid HttpServletRequest object. 
Such object will not be available in background activity such as in Process Tool triggered as a result of Deadlines.

***Sample Attributes***


To retrieve the "Referer" header attribute value in the screenshot above, one may use the following hash variable.


```
Hash Variable 

#request.header.Referer#
```