package spring.appengine.dao;

import java.util.Date;
import spring.appengine.model.HistoryItem;
import spring.appengine.model.Question;
import spring.appengine.model.Response;
import spring.appengine.model.Survey;
import spring.appengine.model.UploadedImage;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Entity;

/**
 * Convert an AppEngine Entity into a JavaBean. We want to separate the Controller
 * from the DAO and database-specific code, so we use our Beans in the Controller and 
 * AppEngine Entities in the DAO. This class helps bridge the Controller and DAO.
 * @author Sairam Krishnan
 */
public class EntityToBean {

	/**
	 * @param e  The Survey Entity that we wish to convert into a JavaBean
	 * @return a Survey bean with the properties in the given Entity
	 * @throws DAOException
	 */
	public Survey entityToSurvey(Entity e) throws DAOException {
		//Make sure that the Entity corresponds to a Survey bean.
		if (!e.getKind().equals("Survey")) {
			throw new DAOException();
		}

		//Read the Entity's properties.
		long id = e.getKey().getId();
		String title = (String) e.getProperty("surveyTitle");
		String creator = (String) e.getProperty("ownerEmail");
		Date dateCreated = (Date) e.getProperty("dateCreated");
		
		//Write the Entity's properties into a Survey bean.
		Survey surveyBean = new Survey();
		surveyBean.setId(id);
		surveyBean.setSurveyTitle(title);
		surveyBean.setOwnerEmail(creator);
		surveyBean.setDateCreated(dateCreated);

		return surveyBean;
	}

	/**
	 * @param e  The Question Entity that we wish to convert into a JavaBean
	 * @return a Question bean with the properties in the given Entity
	 * @throws DAOException
	 */
	public Question entityToQuestion(Entity e) throws DAOException {
		//Make sure that the Entity corresponds to a Question bean.
		if (!e.getKind().equals("Question")) {
			throw new DAOException();
		}

		//Read the Entity's properties.
		long id = e.getKey().getId();
		long surveyId = (Long) e.getProperty("surveyId");
		String questionText = (String) e.getProperty("question");
		Date dateCreated = (Date) e.getProperty("dateCreated");

		//Write the Entity's properties into a Question bean.
		Question questionBean = new Question();
		questionBean.setId(id);
		questionBean.setSurveyId(surveyId);
		questionBean.setQuestion(questionText);
		questionBean.setDateCreated(dateCreated);
		
		return questionBean;
	}

	/**
	 * @param e  The Response Entity that we wish to convert into a JavaBean
	 * @return a Response bean with the properties in the given Entity
	 * @throws DAOException
	 */
	public Response entityToResponse(Entity e) throws DAOException {
		//Make sure that the Entity corresponds to a Response bean.
		if (!e.getKind().equals("Response")) {
			throw new DAOException();
		}
	
		//Read the Entity's properties.
		long id = e.getKey().getId();
		long surveyId = (Long) e.getProperty("surveyId");
		long questionId = (Long) e.getProperty("questionId");
		String responseText = (String) e.getProperty("response");
		long numVotes = (Long) e.getProperty("numVotes");
		Date dateCreated = (Date) e.getProperty("dateCreated");

		//Write the Entity's properties into a Response bean.
		Response responseBean = new Response();
		responseBean.setId(id);
		responseBean.setSurveyId(surveyId);
		responseBean.setQuestionId(questionId);
		responseBean.setResponse(responseText);
		responseBean.setNumVotes(numVotes);
		responseBean.setDateCreated(dateCreated);
		
		return responseBean;
	}
	
	/**
	 * @param e  The UploadedImage Entity that we wish to convert into a JavaBean
	 * @return an UploadedImage bean with the properties in the given Entity
	 * @throws DAOException
	 */
	public UploadedImage entityToImage(Entity e) throws DAOException {
		//Make sure that the Entity corresponds to an UploadedImage bean.
		if (!e.getKind().equals("UploadedImage")) {
			throw new DAOException();
		}
		
		//Read the Entity's properties.
		long id = e.getKey().getId();
		long surveyId = (Long) e.getProperty("surveyId");
		long questionId = (Long) e.getProperty("questionId");
		Blob imgBlob = (Blob) e.getProperty("image");
		String fileName = (String) e.getProperty("fileName");
		String contentType = (String) e.getProperty("contentType");
		
		//Write the Entity's properties into an UploadedImage bean.
		UploadedImage imgBean = new UploadedImage();
		imgBean.setId(id);
		imgBean.setSurveyId(surveyId);
		imgBean.setQuestionId(questionId);
		imgBean.setContentType(contentType);
		imgBean.setFileName(fileName);
		imgBean.setImgBytes(imgBlob.getBytes());
		
		return imgBean;
	}
	
	/**
	 * @param e  The HistoryItem Entity that we wish to convert into a JavaBean
	 * @return a HistoryItem bean with the properties in the given Entity
	 * @throws DAOException
	 */
	public HistoryItem entityToHistoryItem(Entity e) throws DAOException {
		//Make sure that the Entity corresponds to a HistoryItem bean.
		if (!e.getKind().equals("HistoryItem")) {
			throw new DAOException();
		}
		
		//Read the Entity's properties.
		long id = (Long) e.getKey().getId();
		long surveyId = (Long) e.getProperty("surveyId");
		String email = (String) e.getProperty("userEmail");
		
		//Write the Entity's properties into a HistoryItem bean.
		HistoryItem itemBean = new HistoryItem();
		itemBean.setId(id);
		itemBean.setSurveyId(surveyId);
		itemBean.setUserEmail(email);
		
		return itemBean;
	}
}