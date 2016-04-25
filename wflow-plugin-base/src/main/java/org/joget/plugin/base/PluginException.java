package org.joget.plugin.base;

public class PluginException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1709539740485352237L;

	public PluginException() {
    }

    public PluginException(String message) {
        super(message);
    }

    public PluginException(Throwable t) {
        super(t);
    }

    public PluginException(String message, Throwable t) {
        super(message, t);
    }
}
