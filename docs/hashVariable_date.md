## Date Hash Variable 

---
***Prefix***
```
date
```

***Description***

```
To get date time according to a specified format.

```

***Atrribute***

- `#date.dateFormat#`
- `#date.dateUnit[+-]integerValue.dateFormat#`

```
Options

	dateFormat

	In Java date format; e.g., yyyy-MM-dd for 2011-06-01
	dateUnit

	YEAR
	MONTH
	DAY
	integerValue

	Numeric integer value. E.g. 10
	
```

- `#date.DATE_FORMAT_TO[INPUT_DATE_VALUE | INPUT_VALUE_FORMAT]#`

- `#date.dateUnit[+-]integerValue.DATE_FORMAT_TO[INPUT_DATE_VALUE | INPUT_VALUE_FORMAT]#`

```
INPUT_DATE_VALUE accepts any date value and even nested hash variables, e.g: form data hash variable. See example in sample attributes below.

INPUT_VALUE_FORMAT is the INPUT_DATE_VALUE original format.

DATE_FORMAT_TO defines the format to change to.
```


***Scope of Use***

- All components within the App.

***Sample Attribute***

```
#date.h:mm a# // shows current time of 12:08 PM

#date.EEE,d MMM yyyy h:mm:ss a# // shows current date time of Wed, 4 Jul 2014 12:08:56 PM

#date.DAY+7.EEE,d MMM yyyy h:mm:ss a# // Add 7 days on top of current date time - Wed, 11 Jul 2014 12:08:56 PM

#date.DAY-1.EEE,d MMM yyyy h:mm:ss a# // Minus 1 days on top of current date time - Wed, 3 Jul 2014 12:08:56 PM

#date.dd-MM-yyyy[{form.j_expense_claim.title}|yyyy-MM-dd]# // Retrieves date from j_expense_claim table, form field title, changes its origin format of yyyy-MM-dd to dd-MM-yyyy.

```

Example In Kecak :

1. Open form


2. Drag and drop text field

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/hashvariable_date.png" alt="textField" />


3. Choose edit option in form

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/hashvariable_date2.png" alt="editForm" />


4. Edit text field

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/hashvariable_date3.png" alt="editText" />


5. Edit advanced option in the next tab

Put hash variable date format (**example: #date.EEE,d MMM yyyy h:mm:ss a#**) at the value field like in this picture :

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/hashvariable_date4.png" alt="advanced" />

6. Click Ok

7. Save form

8. Result for hash variable date

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/hashvariable_date5.png" alt="advanced" />
