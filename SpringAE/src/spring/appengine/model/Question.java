package spring.appengine.model;

import java.util.Date;

public class Question {
	
	/* Instance variables */
	private long id;
	private long surveyId;
	private Date dateCreated;
	private String question;
	
	//Getters
	public long getId() { return id; }
	public long getSurveyId() { return surveyId; }
	public Date getDateCreated() { return dateCreated; }
	public String getQuestion() { return question; }
	
	//Setters
	public void setId(long id) { this.id = id; }
	public void setSurveyId(long surveyId) { this.surveyId = surveyId; }
	public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }
	public void setQuestion(String question) { this.question = question; }
}