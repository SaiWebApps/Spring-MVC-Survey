package com.webapp.models;

import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name="QUESTIONS")
public class Question implements Serializable {
	
	/* Instance vars - Table cols & form inputs */
	@Id 
	@Column(name="ID") 
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long qid;
	
	@Column(name="QUESTION") 
	@NotEmpty(message="Question Required.")
	private String question;
	
	@Column(name="SURVEY_ID") 
	private long surveyId;
	
	
	/* Getters */
	public long getQid() { return qid; }
	public long getSurveyId() { return surveyId; }
	public String getQuestion() { return question; }
	
	/* Setters */
	public void setQid(long qid) { this.qid = qid; }
	public void setSurveyId(long sid) { this.surveyId = sid; }
	public void setQuestion(String question) { this.question = question; }
}