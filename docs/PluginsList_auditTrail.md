## Kecak Audit Trail Plugin

Plugins From Grid functions to display Grid data which usually can be more than 1 data.
This Form Grid requires an additional form as a place to store data.


The following is data that must be filled when using Form Grid:

Below is example of using Grid :

<img src="https://raw.githubusercontent.com/kinnara-digital-studio/kecak-workflow/master/docs/assets/auditTrailExample.png" alt="auditTrailExample" />


Kecak Audit Trail Plugin bundle contains a set of plugins to support field-level audit trail history.

### Plugins Components

* AuditTrailFormBinder
* AuditTrailElementBinder
* AuditTrailMultirowFormBinder

### Usage

There are 3 main steps to need to be configured when implementing the plugins

1. Develop **Main Form**
2. Develop **Audit Form**
3. Bind **Main Form** with **Audit Form**

### Develop Main Form

**Main Form** is the form that contains fields that will be captured. Basically this is the normal transactional form for user to fill in.

### Develop Audit Form

**Audit Form** history form fields for **Main Form**. You need to use the same name field with the one in **Main Form** in order to capture the data. The field type is not necessarily have to be the same. Add another field that can be used to store foreign keys (**Main Form**'s id).

### Bind **Main Form** with **Audit Form**

Easiest way to implement Kecak Audit Trail Plugins are bindings using store binder section of **Main Form** which contains fields needed to be audited **Audit Form** by using **Kecak Audit Trail Form Binder**. Set the Audit Form properties to **Audit Form**.

### Development

To start developing:

```html
$ git clone https: //kinnarastudio@bitbucket.org/kinnarastudio/kecak-plugins-audit-trail.git/wiki
```

Have fun!

### Version History ###

* ** 1.1.0 **
   * May 2019
* ** 1.0.0 **
   * Initial creation: Isti Fatimah
