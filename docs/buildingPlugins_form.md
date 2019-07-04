# Form

## Form Element Plugins

---

### Description 

Process Tools Plugins are used for integrating with external system

### Abtract Classes 

extends 

`org.joget.apps.form.model.Element`

implements

`org.joget.apps.form.model.FormBuilderPaletteElement`

```
 public String getFormBuilderCategory() {
        return null;
    }

    @Override
    public int getFormBuilderPosition() {
        return 0;
    }

    @Override
    public String getFormBuilderIcon() {
        return null;
    }

    @Override
    public String getDefaultPropertyValues() {
        return null;
    }

    @Override
    public String getFormBuilderTemplate() {
        return null;
    }
}

```

## Form Load Binder Plugins 

---

### Description

To extends methods of loading data in a form form any data source

### Abtract Classes

extends 

`org.joget.apps.form.model.FormBinder`

implements 	

`org.joget.apps.form.model.FormLoadBinder`

`org.joget.apps.form.model.FormLoadElementBinder`

## Form Options Binder Plugins

---

### Description

To extends method to loading data for a form field options form any data source

### Abtract Classes

extends 

`org.joget.apps.form.model.FormBinder`

implements

`org.joget.apps.form.model.FormLoadOptionsBinder`


```
	public FormRowSet load(Element element, String s, FormData formData) {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getLabel() {
        return null;
    }

    @Override
    public String getClassName() {
        return null;
    }

    @Override
    public String getPropertyOptions() {
        return null;
    }
	
```

## Form Store Binder

---

### Description

To extends  methods of storing data in a form to any data source

### Abtract Classes

extends 

`org.joget.apps.form.model.FormBinder`

implements 

`org.joget.apps.form.model.FormStoreBinder`
`org.joget.apps.form.model.FormStoreElementBinder`

```
@Override
    public String getName() {
        return null;
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getLabel() {
        return null;
    }

    @Override
    public String getClassName() {
        return null;
    }

    @Override
    public String getPropertyOptions() {
        return null;
    }

```

## Form Validator Plugins

---

### Description

To extends ways to validate form data

### Abtract Classes

extends

`org.joget.apps.form.model.FormValidator`

```
 public boolean validate(Element element, FormData formData, String[] strings) {
        return false;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getLabel() {
        return null;
    }

    @Override
    public String getClassName() {
        return null;
    }

    @Override
    public String getPropertyOptions() {
        return null;
    }
}

```