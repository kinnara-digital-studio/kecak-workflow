### Data Hash Variable ###

---

***Prefix***

```
Form
```

***Description***

To get `field value` from ` form table`

***Attribute***

``` 
1. #form. tableName . fieldId #
2. #form. tableName . fieldId [recordId]# (Available in 3.1 and above)
```

***Scope of Use***

- In a Prosses Tool part of a prosses 
- In a form
- In a prosess design 

***Sample Atrribute***


`#form.registration.registeredDate#` 

will use the current record ID

`#form.registration.registeredDate[ 0001 ]#` 

0001 is the record ID to seek for

`#form.registration.registeredDate[{variable.recordId}]#` 

using nested Hash Variable with curly bracket.
