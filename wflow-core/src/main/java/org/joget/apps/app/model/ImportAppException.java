package org.joget.apps.app.model;

public class ImportAppException extends RuntimeException {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -2719940381473417900L;

	public ImportAppException(String msg) {
        super(msg);
    }
    
    public ImportAppException(String msg, Throwable e) {
        super(msg, e);
    }
}
