###Request Parameter Hash Variabel###

---

***Prefix***
```
requestParam
```

***Description***

```
To access request parameter's value
```

***Scope of Use***
- All components within th app.

***Sample Attributes***

- `#requestParam.key#`
```
 can be used specifically to obtain Userview Key value.
```

- `#requestParam.name[;]#`

``` to indicate 'name' variable will store multiple values separated by semicolon. For example, this SQL statement "select * from expenses where title in `#requestParam.title[,]#` will be translated to "select * from expenses where title in (a,b,c)" to cater for multiple values in "where-in" statement.
```
- In a Userview page, one may access the following attributes.
```
#requestParam.key#`
#requestParam.userviewId#`
#requestParam.menuId#`
#requestParam.appId#`
```
- `#requestParam.primaryKey#`

``` 
can be used to retrieve the value passed into Ajax Subforms. For example, when a select box is selected, the id value will be passed into the Ajax Subform and you can use the `#requestParam.primaryKey#` in JDBC SQL Where clause.
````