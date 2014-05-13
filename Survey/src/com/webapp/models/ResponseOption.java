package com.webapp.models;

import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.validator.constraints.*;

@Entity
@Table(name="RESPONSE_OPTIONS")
public class ResponseOption implements Serializable {
	
	/* Instance Vars */
	@Id 
	@Column(name="ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long responseId;

	@Column(name="QUESTION_ID")
	private long questionId;
	
	@Column(name="RESPONSE")
	@NotEmpty(message="Response is required.")
	private String response;
	
	
	/* Getters */
	public long getResponseId() { return responseId; }
	public long getQuestionId() { return questionId; }
	public String getResponse() { return response; }

	/* Setters */
	public void setResponseId(long rid) { this.responseId = rid; }
	public void setQuestionId(long qid) { this.questionId = qid; }
	public void setResponse(String r) { this.response = r; }
}