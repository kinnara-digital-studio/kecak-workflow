# Datalist 

## Datalist Actions Plugins

---

### Descriptions 

To extend methods of executing an action on list item e.g delete a record 

### Abstract Classes

extend

`org.joget.apps.datalist.model.DataListActionDefault`

```

public String getLinkLabel() {
        return null;
    }

    @Override
    public String getHref() {
        return null;
    }

    @Override
    public String getTarget() {
        return null;
    }

    @Override
    public String getHrefParam() {
        return null;
    }

    @Override
    public String getHrefColumn() {
        return null;
    }

    @Override
    public String getConfirmation() {
        return null;
    }

    @Override
    public DataListActionResult executeAction(DataList dataList, String[] strings) {
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

## Datalist Binder Plugins

---

### Descriptions

To extend methods of loading data for a list

### Abstract Classes

extend 

`org.joget.apps.datalist.model.DataListBinderDefault`

```
@Override
    public DataListColumn[] getColumns() {
        return new DataListColumn[0];
    }

    @Override
    public String getPrimaryKeyColumnName() {
        return null;
    }

    @Override
    public DataListCollection getData(DataList dataList, Map map, DataListFilterQueryObject[] dataListFilterQueryObjects, String s, Boolean aBoolean, Integer integer, Integer integer1) {
        return null;
    }

    @Override
    public int getDataTotalRowCount(DataList dataList, Map map, DataListFilterQueryObject[] dataListFilterQueryObjects) {
        return 0;
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

## Datalist Column Formatter Plugins

---

### Descriptions

To extend ways od formatting column data 

### Abstract Classes

extends 

`org.joget.apps.datalist.model.DataListColumnFormatDefault`

```
@Override
    public String format(DataList dataList, DataListColumn dataListColumn, Object o, Object o1) {
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

