package spring.appengine.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import spring.appengine.model.HistoryItem;
import spring.appengine.model.Question;
import spring.appengine.model.Response;
import spring.appengine.model.Survey;
import spring.appengine.model.UploadedImage;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreFailureException;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Transaction;

/**
 * Manage database access. Uses AppEngine Entity objects to store beans'
 * properties in AppEngine Datastore.
 * @author Sairam Krishnan
 */
public class SurveyDAO {

	//AppEngine-provided "DAO" - handles access to datastore
	private DatastoreService datastoreService;
	//To convert AppEngine Entity into a JavaBean (Survey, Question, Response)
	private EntityToBean entityConverter;

	//DAO Constructor - Initialize instance variables
	public SurveyDAO() { 
		datastoreService = DatastoreServiceFactory.getDatastoreService();
		entityConverter = new EntityToBean();
	}

	//Create Methods
	/**
	 * Create a Survey with the given title, and associate it with its creator.
	 * @param ownerEmail   Survey creator
	 * @param title        Title of the survey
	 * @throws DAOException
	 */
	public Survey saveSurvey(String ownerEmail, String title) 
			throws DAOException {

		Transaction txn = null;
		try {
			txn = datastoreService.beginTransaction();
			
			//Create a Survey Entity with the given title and owner.
			Entity newSurvey = new Entity("Survey");
			newSurvey.setProperty("ownerEmail", ownerEmail);
			newSurvey.setProperty("surveyTitle", title);
			newSurvey.setProperty("dateCreated", new Date());
			
			//Save this Survey Entity in the database, and assign it an id.
			datastoreService.put(newSurvey);
			//Commit the Transaction b/c we're done accessing the database.
			txn.commit();
			
			//Return the JavaBean corresponding to this newly created Survey Entity.
			return entityConverter.entityToSurvey(newSurvey);
		} catch(DatastoreFailureException e) {
			throw new DAOException();
		} finally {
			//If the transaction was unable to complete successfully, roll
			//back (undo) all changes that it has made to the database.
			if (txn != null && txn.isActive()) txn.rollback();
		}
	}

	/**
	 * @param surveyKey  The Key of the Survey that this Question bean belongs to
	 * @param qBean The Question bean that must be saved to the datastore
	 * @return the Key of the saved Question bean
	 */
	private Key saveQuestion(Key surveyKey, Question qBean) {
		Entity newQuestion = new Entity("Question", surveyKey);
		newQuestion.setProperty("surveyId", surveyKey.getId());
		newQuestion.setProperty("question", qBean.getQuestion());
		newQuestion.setProperty("dateCreated", new Date());
		return datastoreService.put(newQuestion);
	}
	
	/**
	 * @param surveyKey  The Survey that these Responses are a part of
	 * @param questionId  The Question that these Responses belong to
	 * @param responseList List of Responses to save to the datastore
	 */
	private void saveResponses(Key surveyKey, long questionId, List<String> responseList) {
		for (String responseText : responseList) {
			Entity responseEntity = new Entity("Response", surveyKey);
			responseEntity.setProperty("surveyId", surveyKey.getId());
			responseEntity.setProperty("questionId", questionId);
			responseEntity.setProperty("response", responseText);
			responseEntity.setProperty("dateCreated", new Date());
			responseEntity.setProperty("numVotes", 0);
			datastoreService.put(responseEntity);
		}
	}
	
	private void saveImage(Key surveyKey, long questionId, UploadedImage imgBean) {
		Entity newImage = new Entity("UploadedImage", surveyKey);
		newImage.setProperty("contentType", imgBean.getContentType());
		newImage.setProperty("surveyId", surveyKey.getId());
		newImage.setProperty("fileName", imgBean.getFileName());
		newImage.setProperty("questionId", questionId);
		newImage.setProperty("image", new Blob(imgBean.getImgBytes()));
		datastoreService.put(newImage);
	}
	
	/**
	 * Create a Question, its associated Responses, and an image (optional) within 
	 * a single Transaction, so that if something goes wrong, we roll back both the
	 * Question and the Responses, rather than just one or the other.
	 * @throws DAOException
	 */
	public void saveQA(Question q, List<String> responseList, UploadedImage imgBean) 
			throws DAOException {
		
		Transaction txn = null;

		try {
			long surveyId = q.getSurveyId();
			Key surveyKey = KeyFactory.createKey("Survey", surveyId);
			
			txn = datastoreService.beginTransaction();
			//Save the Question.
			Key questionKey = saveQuestion(surveyKey, q);
			//Save all of the Response options associated with this Question.
			long questionId = questionKey.getId();			
			saveResponses(surveyKey, questionId, responseList);
			//If there was an image uploaded with this Question, then save it.
			if (imgBean.getImgBytes() != null) {
				saveImage(surveyKey, questionId, imgBean);
			}
			//Commit all changes to the database, and end the transaction.
			txn.commit();
		} catch(DatastoreFailureException e) {
			throw new DAOException();
		} finally {
			//If the transaction was unable to complete successfully, roll
			//back (undo) all changes that it has made to the database.
			if (txn != null && txn.isActive()) txn.rollback();
		}
	}

	/**
	 * Keep track of the Surveys being taken by the user with the given email address.
	 * @param email  The email of the user being tracked
	 * @param surveyId  The survey taken by the respondent
	 * @throws DAOException
	 */
	public void saveHistoryItem(String email, long surveyId) throws DAOException {
		Transaction txn = null;
		try {
			txn = datastoreService.beginTransaction();
			Key surveyKey = KeyFactory.createKey("Survey", surveyId);
			Entity historyEntity = new Entity("HistoryItem", surveyKey);
			historyEntity.setProperty("surveyId", surveyId);
			historyEntity.setProperty("userEmail", email);
			datastoreService.put(historyEntity);
			txn.commit();
		} catch(DatastoreFailureException e) { 
			throw new DAOException();
		}
		finally {
			if (txn != null && txn.isActive()) txn.rollback();
		}
	}
	
	//Read Methods - No Transactions Required
	/**
	 * @param surveyId  The id of the Survey that this image is a part of
	 * @param questionId  The id of the Question that this image was assigned to
	 * @return the UploadedImage bean with the given surveyId and questionId
	 * @throws DAOException
	 */
	public UploadedImage getImage(long surveyId, long questionId) 
			throws DAOException {

		//Query Entities of kind UploadedImage with the given surveyId and questionId.
		Filter surveyFilter = new FilterPredicate("surveyId",
				FilterOperator.EQUAL, surveyId);
		Filter questionFilter = new FilterPredicate("questionId", 
				FilterOperator.EQUAL, questionId);
		Query query = new Query("UploadedImage").setFilter(
				CompositeFilterOperator.and(surveyFilter, questionFilter));
		Iterable<Entity> entities = datastoreService.prepare(query).asIterable();
	
		//Convert the results into UploadedImage beans.
		UploadedImage imgBean = null;
		for (Entity imgEntity : entities) {
			imgBean = entityConverter.entityToImage(imgEntity);
		}
		
		//If there was no image for this question, then throw a DAOException.
		if (imgBean == null) {
			throw new DAOException();
		}
		
		//Otherwise, return the image bean.
		return imgBean;
	}

	/**
	 * @param surveyId  The id of the Survey that we're searching for
	 * @return the Survey with the specified id
	 * @throws DAOException
	 */
	public Survey getSurvey(long surveyId) throws DAOException {
		try {
			Key k = KeyFactory.createKey("Survey", surveyId);
			Entity surveyEntity = datastoreService.get(k);
			return entityConverter.entityToSurvey(surveyEntity);
		} catch (EntityNotFoundException e) {
			//Return false to indicate that no Entity was found for the given id.
			return null;
		} catch (DatastoreFailureException e) {
			//Encapsulate the error within a DAOException. What's done within
			//the database stays in the database.
			throw new DAOException();
		}
	}

	/**
	 * @return the list of all Surveys created thus far
	 * @throws DAOException
	 */
	public List<Survey> getAllSurveys() throws DAOException {
		List<Survey> allSurveys = new ArrayList<Survey>();
		PreparedQuery pq = datastoreService.prepare(new Query("Survey"));
		for (Entity surveyEntity : pq.asIterable()) {
			allSurveys.add(entityConverter.entityToSurvey(surveyEntity));
		}
		
		//Sort the Surveys by creation date.
		Collections.sort(allSurveys, new Comparator<Survey>() {
			@Override
			public int compare(Survey o1, Survey o2) {
				return o1.getDateCreated().compareTo(o2.getDateCreated());
			}
		});
		
		return allSurveys;
	}

	/**
	 * @param userEmail  The email of the user that we're tracking
	 * @return the list of Surveys taken by the given user
	 * @throws DAOException
	 */
	public List<Survey> getSurveysTaken(String userEmail) throws DAOException {
		Filter emailFilter = new FilterPredicate("userEmail", 
				FilterOperator.EQUAL, userEmail);
		Query query = new Query("HistoryItem").setFilter(emailFilter);
		Iterable<Entity> entities = datastoreService.prepare(query).asIterable();
		List<Survey> surveyList = new ArrayList<Survey>();

		/*
		 * For each HistoryItem entity, first convert the Entity into a Java bean,
		 * and then add the Survey with the corresponding surveyId into surveyList.
		 */
		for (Entity historyEntity : entities) {
			HistoryItem itemBean = entityConverter.entityToHistoryItem(historyEntity);
			Survey takenSurvey = getSurvey(itemBean.getSurveyId());
			surveyList.add(takenSurvey);
		}
		
		return surveyList;
	}
	
	/**
	 * @param surveyId  The id of the Survey being analyzed
	 * @return a list of all of the users who have taken the Survey with the given id
	 * @throws DAOException
	 */
	private List<String> getSurveyRespondents(long surveyId) throws DAOException {
		Filter surveyFilter = new FilterPredicate("surveyId", 
				FilterOperator.EQUAL, surveyId);
		Query query = new Query("HistoryItem").setFilter(surveyFilter);
		Iterable<Entity> entities = datastoreService.prepare(query).asIterable();
		List<String> respondentList = new ArrayList<String>();

		/*
		 * Convert each Entity into a HistoryItem bean, and then add the
		 * respondent's email address to respondentList. 
		 */
		for (Entity historyEntity : entities) {
			HistoryItem itemBean = entityConverter.entityToHistoryItem(historyEntity);
			String respondentEmail = itemBean.getUserEmail();
			respondentList.add(respondentEmail);
		}
		
		return respondentList;
	}
	
	public List<Survey> getSuggestedSurveys(String userEmail) throws DAOException {
		//List of Surveys taken so far by this user
		List<Survey> takenSurveys = getSurveysTaken(userEmail);
		List<Survey> remainingSurveys = new ArrayList<Survey>();
		
		for (Survey survey : takenSurveys) {
			//For each taken Survey, get the list of other respondents.
			//Remove this user from the list b/c he's already taken this survey.
			long surveyId = survey.getId();
			List<String> otherRespondents = getSurveyRespondents(surveyId);
			otherRespondents.remove(userEmail);
			
			//Add all of the Surveys taken by the other respondents
			//to remainingSurveys.
			for (String respondent : otherRespondents) {
				remainingSurveys.addAll(getSurveysTaken(respondent));
			}
			
			//Remove all of the Surveys that this user has already taken.
			remainingSurveys.removeAll(takenSurveys);
		}		
		
		return remainingSurveys;
	}

	public List<Question> getAllQuestions(long surveyId) throws DAOException {
		Filter surveyFilter = new FilterPredicate("surveyId", 
				FilterOperator.EQUAL, surveyId);
		Query query = new Query("Question").setFilter(surveyFilter);
		Iterable<Entity> entities = datastoreService.prepare(query).asIterable();
		List<Question> allQuestions = new ArrayList<Question>();

		for (Entity questionEntity : entities) {
			Question qBean = entityConverter.entityToQuestion(questionEntity);
			allQuestions.add(qBean);
		}

		Collections.sort(allQuestions, new Comparator<Question>() {
			@Override
			public int compare(Question o1, Question o2) {
				return o1.getDateCreated().compareTo(o2.getDateCreated());
			}
		});
		return allQuestions;
	}

	public List<Response> getAllResponses(long surveyId, long questionId) 
			throws DAOException {

		Filter surveyFilter = new FilterPredicate("surveyId", 
				FilterOperator.EQUAL, surveyId);
		Filter questionFilter = new FilterPredicate("questionId", 
				FilterOperator.EQUAL, questionId);
		Query query = new Query("Response").setFilter(
				CompositeFilterOperator.and(surveyFilter, questionFilter));
		Iterable<Entity> entities = datastoreService.prepare(query).asIterable();
		List<Response> allResponses = new ArrayList<Response>();

		for (Entity responseEntity : entities) {
			Response rBean = entityConverter.entityToResponse(responseEntity);
			allResponses.add(rBean);
		}

		Collections.sort(allResponses, new Comparator<Response>() {
			@Override
			public int compare(Response o1, Response o2) {
				return o1.getDateCreated().compareTo(o2.getDateCreated());
			}
		});
		return allResponses;
	}

	//Update Methods
	public boolean upVote(long surveyId, List<Long> responseIds) throws DAOException {
		Transaction txn = null;

		try {
			txn = datastoreService.beginTransaction();
			Key surveyKey = KeyFactory.createKey("Survey", surveyId);

			for (long responseId : responseIds) {
				Key k = KeyFactory.createKey(surveyKey, "Response", responseId);
				Entity responseEntity = datastoreService.get(k);
				long numVotes = (Long) responseEntity.getProperty("numVotes");
				responseEntity.setProperty("numVotes", numVotes + 1);
				datastoreService.put(responseEntity);
			}

			txn.commit();
			return true;
		} catch (EntityNotFoundException e) {
			//Return false to indicate that no Entity was found 
			//for the given id.
			return false;
		} catch(DatastoreFailureException e) { 
			//In case of some failure related to accessing Datastore, throw a
			//DAOException, to hide the actual error from the client.
			throw new DAOException();
		} finally {
			//If the transaction was unable to complete successfully, roll
			//back (undo) all changes that it has made to the database.
			if (txn != null && txn.isActive()) txn.rollback();
		}
	}

	public boolean updateSurvey(long surveyId, String newTitle) 
			throws DAOException {
		
		Transaction txn = null;
		try {
			txn = datastoreService.beginTransaction();
			Key k = KeyFactory.createKey("Survey", surveyId);
			Entity surveyEntity = datastoreService.get(k);
			surveyEntity.setProperty("surveyTitle", newTitle);
			datastoreService.put(surveyEntity);
			txn.commit();
			return true;
		} catch (EntityNotFoundException e) {
			return false;
		} catch (DatastoreFailureException e) {
			throw new DAOException();
		} finally {
			if (txn != null && txn.isActive()) txn.rollback();
		}
	}
	
	/**
	 * Delete the specified survey.
	 * @param surveyId  Id of the Survey to be deleted
	 */
	public void delete(long surveyId) throws DAOException {
		Transaction txn = null;

		try {
			Key surveyKey = KeyFactory.createKey("Survey", surveyId);

			txn = datastoreService.beginTransaction();
			List<Key> questionsToDelete = new ArrayList<Key>();
			List<Key> responsesToDelete = new ArrayList<Key>();
			List<Key> imagesToDelete = new ArrayList<Key>();
			for (Question qBean : getAllQuestions(surveyId)) {
				long questionId = qBean.getId();
				Key qKey = KeyFactory.createKey(surveyKey, "Question", questionId);	
				for (Response rBean : getAllResponses(surveyId, questionId)) {
					long responseId = rBean.getId();
					Key rKey = KeyFactory.createKey(surveyKey, "Response", responseId);
					responsesToDelete.add(rKey);
				}
				UploadedImage imgBean = getImage(surveyId, questionId);
				if (imgBean != null) {
					imagesToDelete.add(KeyFactory.createKey(surveyKey, "UploadedImage", imgBean.getId()));
				}
				questionsToDelete.add(qKey);
			}
			//Delete all images in the Survey.
			datastoreService.delete(imagesToDelete);
			//Delete all Responses in the Survey.
			datastoreService.delete(responsesToDelete);
			//Delete all Questions in the Survey.
			datastoreService.delete(questionsToDelete);
			//Delete the survey.
			datastoreService.delete(surveyKey);
			txn.commit();
		} catch(DatastoreFailureException e) {
			throw new DAOException();
		} finally {
			if (txn != null && txn.isActive()) txn.rollback();
		}
	}
}