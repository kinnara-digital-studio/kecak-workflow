## Description
   Plugins From Grid berfungsi untuk menampilkan data Grid yang biasanya data tersebut bisa lebih dari 1 data.
   Form Grid ini membutuhkan form tambahan sebagai tempat penyimpanan datanya.

   berikut adalah data yang harus diisi ketika akan menggunakan Form Grid :

   Contoh penggunaan Form Grid ini adalah pada aplikasi PT Timah Pengajuan SHP 


#### Keterangan ####

## Membuat Form Data (contoh untuk data Attachment) ##

*gambar 2*

# Kecak Audit Trail Plugin

Kecak Audit Trail Plugin bundle contains a set of plugins to support field-level audit trail history.

# Plugins Components

* AuditTrailFormBinder
* AuditTrailElementBinder
* AuditTrailMultirowFormBinder

# Usage

There are 3 main steps to need to be configured when implementing the plugins

1. Develop **Main Form**
2. Develop **Audit Form**
3. Bind **Main Form** with **Audit Form**

### Develop Main Form

**Main Form** is the form that contains fields that will be captured. Basically this is the normal transactional form for user to fill in.

### Develop Audit Form

**Audit Form** is the form which structure is needed to capture the audit history for fields in **Main Form**. You need to use the same field name with the one in **Main Form** in order to capture the data. The field type is not necessarily have to be the same. Add another field that can be used to store foreign key (**Main Form**'s id).

### Bind **Main Form** with **Audit Form**

Easiest way to implement Kecak Audit Trail Plugins is by binding using section's store binder of **Main Form* *which contains fields need to be audited to **Audit Form** by using **Kecak Audit Trail Form Binder**. Set the 
Audit Form properties to **Audit Form**.

# Development

To start developing:

```
$ git clone https://kinnarastudio@bitbucket.org/kinnarastudio/kecak-plugins-audit-trail.git/wiki
```

Have fun!

### Version History ###

*  **1.1.0**
   * mei 2019
*  **1.0.0**
   * Initial creation : Isti Fatimah
