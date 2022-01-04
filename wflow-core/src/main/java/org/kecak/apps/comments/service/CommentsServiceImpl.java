package org.kecak.apps.comments.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.kecak.apps.comments.dao.CommentsDao;
import org.kecak.apps.comments.model.Comments;

public class CommentsServiceImpl implements CommentsService{
	private CommentsDao commentsDao;
	
	public CommentsDao getCommentsDao() {
		return commentsDao;
	}


	public void setCommentsDao(CommentsDao commentsDao) {
		this.commentsDao = commentsDao;
	}


	@Override
	public Collection<Comments> getComments(String processId) {
		return Optional.ofNullable(commentsDao.find("where processId = ? ", new String[] {processId}, "dataCreated", true, null, null)).orElse(new ArrayList<>());
	}


	@Override
	public void deleteComment(String id) {
		Comments comments = commentsDao.load(id);
        if(comments != null)
        	commentsDao.delete(comments);
	}


	@Override
	public Collection<Comments> getUserComments(String processId, String username) {
		// TODO Auto-generated method stub
		return null;
	}

}
