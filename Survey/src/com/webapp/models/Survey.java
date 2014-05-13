package com.webapp.models;

import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name="SURVEYS")
public class Survey implements Serializable {
	
	/* Instance vars - Table cols & form inputs */
	@Id @Column(name="ID") 
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long surveyId;
	
	@Column(name="POSTER") 
	private String userEmail;
	
	@Column(name="SURVEY_TITLE")
	@NotEmpty(message="A Title Is Required For This Survey.")
	private String surveyTitle;
	
	@Column(name="DATE_CREATED")
	private String dateCreated;
	
	
	/* Getters */
	public long getSurveyId() { return surveyId; }
	public String getUserEmail() { return userEmail; }
	public String getSurveyTitle() { return surveyTitle; }
	public String getDateCreated() { return dateCreated; }
	
	/* Setters */
	public void setSurveyId(long sid) { this.surveyId = sid; }
	public void setUserEmail(String uem) { this.userEmail = uem; }
	public void setSurveyTitle(String t) { this.surveyTitle = t; }
	public void setDateCreated(String d) { this.dateCreated = d; }
}