package org.kecak.apps.comments.service;

import java.util.Collection;

import javax.annotation.Nonnull;

import org.kecak.apps.comments.model.Comments;


public interface CommentsService {
	@Nonnull
    Collection<Comments> getComments(String processId);

    /**
     * Get device list for particular user
     *
     * @param username
     * @param includeBlockedDevice
     * @return
     */
    @Nonnull
    Collection<Comments> getUserComments(String processId, String username);

    /**
     * Delete current device
     *
     * @param deviceId
     */
    void deleteComment(String id);

}
