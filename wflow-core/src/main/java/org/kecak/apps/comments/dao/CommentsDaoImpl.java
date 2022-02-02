package org.kecak.apps.comments.dao;

import java.util.ArrayList;
import java.util.Collection;

import org.joget.commons.spring.model.AbstractSpringDao;
import org.kecak.apps.comments.model.Comments;
import org.springframework.transaction.annotation.Transactional;

public class CommentsDaoImpl extends AbstractSpringDao<Comments> implements CommentsDao {
	public static final String ENTITY_NAME = "Comments";
	
	@Override
	@Transactional
	public void saveOrUpdate(Comments comment) {
		saveOrUpdate(ENTITY_NAME, comment);
		
	}

	@Override
	public void delete(Comments comment) {
		internalDelete(comment, false);
	}

	@Override
	public void hardDelete(Comments comment) {
		internalDelete(comment, true);
	}

	@Override
	@Transactional
	public Comments load(String id) {
		return internalLoad(id);
	}
	
	@Override
	@Transactional
	public ArrayList<Comments> loadByForeignKey(String foreignKey) {
		Comments param = new Comments();
		param.setForeignKey(foreignKey);
		ArrayList<Comments> comment = (ArrayList<Comments>) findByExample(ENTITY_NAME, param);
        return comment;
	}
	
	@Override
	@Transactional
	public ArrayList<Comments> loadByProcessId(String processId) {
		Comments param = new Comments();
		param.setProcessId(processId);
		ArrayList<Comments> comment = (ArrayList<Comments>) findByExample(ENTITY_NAME, param);
        return comment;
	}
	
	@Override
	@Transactional
	public ArrayList<Comments> loadByActivityId(String activityId) {
		Comments param = new Comments();
		param.setActivityId(activityId);
		ArrayList<Comments> comment = (ArrayList<Comments>) findByExample(ENTITY_NAME, param);
        return comment;
	}

	@Override
	@Transactional
	public Collection<Comments> find(String condition, String[] args, String sort, Boolean desc, Integer start, Integer rows) {
		return internalFind(condition, args, sort, desc, start, rows);
	}

	/**
     * Internal Delete
     *
     * @param comment
     * @param deleteFromTable hard delete
     */
    private void internalDelete(final Comments comment, boolean deleteFromTable) {
        if(deleteFromTable) {
            // delete from table
            delete(ENTITY_NAME, comment);
        } else {
            // mark as deleted
        	comment.setDeleted(true);
            saveOrUpdate(ENTITY_NAME, comment);
        }
    }

    /**
     * Internal Load
     *
     * @param id 
     * @return null if id not found
     */
    private Comments internalLoad(String id) {
    	Comments comment = (Comments) find(ENTITY_NAME, id);
        return comment == null || comment.getDeleted() ? null : comment;
    }

    /**
     * Internal Find
     *
     * @param condition
     * @param args
     * @param sort
     * @param desc
     * @param start
     * @param rows
     * @return collection of {@link Comments}
     */
    private Collection<Comments> internalFind(final String condition, final String[] args, final String sort, final Boolean desc, final Integer start, final Integer rows) {
        final String where;
        // filter by delete setatus
        if(condition != null) {
            where = "where deleted = false and (1 = 1 " + condition.trim().replaceAll("where ", "and ") + ")";
        } else {
            where = "where deleted = false";
        }

        return find(ENTITY_NAME, where, args, sort, desc, start, rows);
    }
}
