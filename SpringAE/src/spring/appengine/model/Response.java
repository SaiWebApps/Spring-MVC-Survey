package spring.appengine.model;

import java.util.Date;

public class Response {

	/* Instance Vars */
	private long id;
	private long surveyId;
	private long questionId; //Question being answered
	private String response; //Response
	private long numVotes; //Number of users who have chosen this option
	private Date dateCreated;

	/* Getters */
	public long getId() { return id; }
	public long getSurveyId() { return surveyId; }
	public long getQuestionId() { return questionId; }
	public String getResponse() { return response; }
	public long getNumVotes() { return numVotes; } 
	public Date getDateCreated() { 	return dateCreated; }
	
	/* Setters */
	public void setId(long id) { this.id = id; }
	public void setSurveyId(long surveyId) { this.surveyId = surveyId; }
	public void setQuestionId(long questionId) { this.questionId = questionId; }
	public void setResponse(String r) { this.response = r; }
	public void setNumVotes(long n) { this.numVotes = n; }
	public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }
}