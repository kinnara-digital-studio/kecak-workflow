# Process 

## Deadline Plugins 

---
### Descriptions

Provide the ability to recalculate deadline limit and SLA limit based on programming logic

### Abstract Classes

extends

`org.joget.workflow.model.DefaultDeadlinePlugin`

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
    public WorkflowDeadline evaluateDeadline(Map map) {
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

## Process Participant Plugins 

---

### Descriptions

Are used to provide custom selection of users to workflow participants

### Abstract Classes

extends

`org.joget.workflow.model.DefaultParticipantPlugin`

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
    public Collection<String> getActivityAssignments(Map map) {
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

## Process Tool Plugins

---

### Descriptions

To Integrate with external systems.

### Abstract Classes

extends

`org.joget.plugin.base.DefaultApplicationPlugin`

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
    public Object execute(Map map) {
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