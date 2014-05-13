package spring.appengine.model;

public class HistoryItem {

	//Instance Variables
	private long id;
	private long surveyId;
	private String userEmail;
	
	//Getters
	public long getId() { return id; }
	public long getSurveyId() { return surveyId; }
	public String getUserEmail() { return userEmail; }
	
	//Setters
	public void setId(long id) { this.id = id; }
	public void setSurveyId(long surveyId) { this.surveyId = surveyId; }
	public void setUserEmail(String email) { this.userEmail = email; }
}