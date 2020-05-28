package org.joget.directory.dao;

import org.joget.directory.model.ClientApp;

import java.util.Collection;

public interface ClientAppDao {

    Boolean addClientApp(ClientApp userToken);

    Boolean updateClientApp(ClientApp userToken);

    Boolean deleteClientApp(String id);

    ClientApp getClientApp(String id);

    ClientApp getClientApp(ClientApp clientApp);

    Boolean deleteClientApp(ClientApp userToken);

    Collection<ClientApp> getClientAppList(String filterString, String active, String sort, Boolean desc, Integer start, Integer rows);

    Long getTotalClientApp(String filterString, String active);


}
