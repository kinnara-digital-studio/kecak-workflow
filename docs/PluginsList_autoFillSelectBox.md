# Autofill Select Box #

### Plugin Type ###
Form Element

### Overview ###
Provides autofill capabilities based on key specified by selecting a value from the select box. Everytime a selectbox value is selected, the system will execute Load Binder -specified in properties- and use the selected value as key for

### Plugin Properties ###
* ID - Element ID
* Label - Element Label
* Options (Hardcoded) - Static option items
* Or Choose Options Binder - Load option items from **Options Binder**
* Value - Default value
* Validator
* Remove Duplicate - Remove duplicate option items
* Field ID to control available options based on Grouping
* Width - Element width in %
* Readonly
* Display field as Label when readonly
* Workflow variable - Bind this field with workflow variable
* Autofill Load Binder - Load Binder Plugin that will be executed when an option item is selected
* Lazy Mapping - Map automatically based on field name
* Fields Mapping - Specify any field mapping manually

### Example ###
This step-by-step will help you to put simple configuration to load data from any other **Form** (e.g. form master data) using **Autofill Load Binder**
1. In the Edit Select Box property, choose **Default Form Options Binder** to load option items data from other **Form** (e.g. form master data)
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/autofill-selectbox1.png" alt="autofill-selectbox1" />
2. Choose *Form* from which you want to load the option items and specify the *ID* and *Label* field just like normal Select Box configuration
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/autofill-selectbox2.png" alt="autofill-selectbox2" />
3. In *Autofill* tab, choose **Autofill Form Binder** as *Load Binder* and configure the mapping mechanism. *Lazy Mapping* will map fields automatically based on field name while *Fields Mapping* will let you specify which field to be mapped
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/autofill-selectbox3.png" alt="autofill-selectbox3" />
4. In the next tab, choose the *Form* from where you want to get the autofill data, typically this is the same form with point no. 2
<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/autofill-selectbox4.png" alt="autofill-selectbox4" />


### Version History ###
*  **1.2.2**
   * Fix bugs
   * Load autofill when web page ready
*  **1.2.0**
   * Load autofill data from any **Load Binder** plugin
*  **1.1.0**
   * Load autofill data from form

*  **1.0.0**
   * Initial creation
   * Isti Fatimah



