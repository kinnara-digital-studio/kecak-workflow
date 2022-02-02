package org.kecak.apps.comments.dao;

import java.util.ArrayList;
import java.util.Collection;

import org.kecak.apps.comments.model.Comments;


public interface CommentsDao {
	   /**
    *
    * @param comment
    */
   public void saveOrUpdate(Comments comment);

   /**
    * Mark as delete
    *
    * @param comment
    */
   public void delete(Comments comment);

   /**
    * Delete from table
    *
    * @param comment
    */
   public void hardDelete(Comments comment);

   /**
    * Load by ID
    *
    * @param id
    * @return
    */
   public Comments load(String id);
   
   /**
    * Load by Process Id
    *
    * @param foreignKey
    * @return
    */
   public ArrayList<Comments> loadByForeignKey(String foreignKey);
   
   /**
    * Load by Process Id
    *
    * @param processId
    * @return
    */
   public ArrayList<Comments> loadByProcessId(String processId);
   
   /**
    * Load by Activity Id
    *
    * @param activityId
    * @return
    */
   public ArrayList<Comments> loadByActivityId(String activityId);

   /**
    *
    * @param condition
    * @param args
    * @param sort
    * @param desc
    * @param start
    * @param rows
    * @return
    */
   public Collection<Comments> find(final String condition, final String[] args, final String sort, final Boolean desc, final Integer start, final Integer rows);
}
