package org.joget.workflow.model;

import java.io.Serializable;

public class WorkflowTool implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7597827776939966175L;
	private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
