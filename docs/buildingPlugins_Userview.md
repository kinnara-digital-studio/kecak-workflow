# Userview 

## Userview Menu Plugins 

---

### Descriptions

To extends types of pages available in userview builder

### Abstract Classes 

extends`org.joget.apps.userview.model.UserviewPermission`

```

 @Override
    public boolean isAuthorize() {
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
	
```

## Userview Permission Plugins 

---

### Descriptions

To handle permission and access right in a useview

### Abstract Classes

extends`org.joget.apps.userview.model.UserviewPermission`

```

@Override
    public boolean isAuthorize() {
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
	
```

## Userview Theme Plugins

---

### Descriptions

To Change the ui design of Userview

### Abstract Classes

extends `org.joget.apps.userview.model.UserviewTheme`

```
@Override
    public String getCss() {
        return null;
    }

    @Override
    public String getJavascript() {
        return null;
    }

    @Override
    public String getHeader() {
        return null;
    }

    @Override
    public String getFooter() {
        return null;
    }

    @Override
    public String getPageTop() {
        return null;
    }

    @Override
    public String getPageBottom() {
        return null;
    }

    @Override
    public String getBeforeContent() {
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