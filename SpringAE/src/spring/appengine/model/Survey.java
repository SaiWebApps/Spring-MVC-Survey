package spring.appengine.model;

import java.util.Date;

public class Survey {
	
	/* Instance variables  */
	private long id;
	private String ownerEmail;
	private String surveyTitle;
	private Date dateCreated;
	
	/* Getters */
	public long getId() { return id; }
	public String getOwnerEmail() { return ownerEmail; }
	public String getSurveyTitle() { return surveyTitle; }
	public Date getDateCreated() { return dateCreated; }
	
	/* Setters */
	public void setId(long id) { this.id = id; }
	public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }
	public void setSurveyTitle(String title) { this.surveyTitle = title; }
	public void setDateCreated(Date date) { this.dateCreated = date; }
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Survey)) {
			return false;
		}
		Survey s = (Survey) o;
		return id == s.id && ownerEmail.equals(s.ownerEmail) && 
				surveyTitle.equals(s.surveyTitle) && dateCreated.equals(s.dateCreated);				
	}
}