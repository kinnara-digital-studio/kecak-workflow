package org.kecak.apps.app.model;

import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginWebSupport;
import org.json.JSONObject;
import org.kecak.apps.exception.MessengerWebhookException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * @author aristo
 *
 * Plugin for webhook API. Currently is configured for Telegram, Line, and Slack messenger
 *
 * @param <Client>
 * @param <Incoming> type for incoming message
 */
public interface MessengerWebhookPlugin<Client, Incoming> extends PluginWebSupport {
    /**
     * Main method to handle incoming message
     *
     * @param client
     * @return JSONObject for web api response, set NULL to ignore response
     * @throws MessengerWebhookException
     */
    @Nullable
    JSONObject service(@Nonnull Client client, @Nonnull Incoming incoming) throws MessengerWebhookException;

    /**
     * Get client
     *
     * @param request
     * @return
     * @throws MessengerWebhookException
     */
    @Nonnull
    Client getClient(HttpServletRequest request) throws MessengerWebhookException;

    @Nonnull
    Incoming getIncomingMessage(Client client, HttpServletRequest request) throws MessengerWebhookException;

    /**
     * Get info of current webhook, executed when accessing API in GET method
     *
     * @return
     */
    JSONObject getInfo(@Nonnull Client client) throws MessengerWebhookException;

    @Override
    default void webService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LogUtil.info(getClass().getName(), "Executing webhook [" + request.getRequestURI() + "] in method [" + request.getMethod() + "]");

        try {
            AppDefinition appDefinition = Optional.ofNullable(AppUtil.getCurrentAppDefinition())
                    .filter(AppDefinition::isPublished)
                    .orElseThrow(() -> new MessengerWebhookException("Application definition not found or not published"));

            Client client = getClient(request);

            if("GET".equalsIgnoreCase(request.getMethod())) {
                JSONObject jsonResponse = getInfo(client);
                response.getWriter().write(jsonResponse.toString());
            } else if("POST".equalsIgnoreCase(request.getMethod())) {
                Incoming incomingMessage = getIncomingMessage(client, request);
                JSONObject jsonResponse = service(client, incomingMessage);
                if(jsonResponse != null) {
                    response.getWriter().write(jsonResponse.toString());
                }
            }

            response.setStatus(HttpServletResponse.SC_OK);
        } catch (MessengerWebhookException e) {
            LogUtil.error(getClass().getName(), e, e.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}
