### Date Hash Variable ###

---

**Prefix** 
```
Date
```
**Description** 
```
To get date time according to a specified format
```
**Attributes** 

`1. #date.dateFormat#` 

`2. #date.dateUnit[+-]integerValue.dateFormat#`

```
OPTIONS
dateFormat 
- in Java date format eg., yyyy-mm-dd for 2011-06-01

dateUnit 
- Year
- Month 
- Day

IntegerValue 
- Numeric integer value eg., 10 
```
`3. #date.DATE_FORMAT_TO[INPUT_DATE_VALUE | INPUT_VALUE_FORMAT]#`

`4. #date.dateUnit[+-]integerValue.DATE_FORMAT_TO[INPUT_DATE_VALUE | INPUT_VALUE_FORMAT]#`


```
INPUT_DATE_VALUE accepts any date value and even nested hash variables, e.g: form data hash variable. See example in sample attributes below.
INPUT_VALUE_FORMAT is the INPUT_DATE_VALUE original format.
DATE_FORMAT_TO defines the format to change to.
```

**Scope of Use 
```
All components within App.
```
**Sapmple Attribute**
```
1. #date.h:mm a# // shows current time of 12:08 PM
2. #date.EEE,d MMM yyyy h:mm:ss a# // shows current date time of Wed, 4 Jul 2014 12:08:56 PM
3. #date.DAY+7.EEE,d MMM yyyy h:mm:ss a# // Add 7 days on top of current date time - Wed, 11 Jul 2014 12:08:56 PM
4. #date.DAY-1.EEE,d MMM yyyy h:mm:ss a# // Minus 1 days on top of current date time - Wed, 3 Jul 2014 12:08:56 PM
5. #date.dd-MM-yyyy[{form.j_expense_claim.title}|yyyy-MM-dd]# // Retrieves date from j_expense_claim table, form field title, changes      its origin format of yyyy-MM-dd to dd-MM-yyyy.
```
