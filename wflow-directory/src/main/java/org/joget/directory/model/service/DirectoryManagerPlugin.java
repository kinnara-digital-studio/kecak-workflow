package org.joget.directory.model.service;

import java.util.Map;

/**
 * Interface of Directory Manager Plugin
 * 
 */
public interface DirectoryManagerPlugin {

    /**
     * Used by System to retrieve a directory manager implementation
     * @param properties
     * @return 
     */
    @SuppressWarnings("rawtypes")
	public DirectoryManager getDirectoryManagerImpl(Map properties);
}
