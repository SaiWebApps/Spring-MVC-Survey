package com.webapp.models;

import java.io.Serializable;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public class QuestionDAO implements Serializable {
	
	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory f) { 
		this.sessionFactory = f; 
	}
	
	/**
	 * Create a new question in this survey.
	 */
    public void createQuestion(Question q) {
    	Session s = sessionFactory.getCurrentSession();
		Transaction txn = null;
		
		try {
			txn = s.beginTransaction();		
			s.save(q);
			txn.commit();
		} catch(Exception e) {
			e.printStackTrace();
			if(txn!=null)
				txn.rollback();
		}
    }
    
    /**
     * Create a new response option in this survey.
     */
    public void createResponseOption(ResponseOption r) {
    	Session s = sessionFactory.getCurrentSession();
		Transaction txn = null;
		
		try {
			txn = s.beginTransaction();		
			s.save(r);
			txn.commit();
		} catch(Exception e) {
			e.printStackTrace();
			if(txn!=null)
				txn.rollback();
		}
    }
    
	/**
	 * @return all Questions in survey
	 */
	public List<Question> allQuestions(Survey survey) {
		Session session = sessionFactory.getCurrentSession();
		Transaction txn = null;
		List<Question> qList = null;
		
		try {
			txn = session.beginTransaction();
			Query q = session.createQuery("from Question where surveyId=:i");
			q.setParameter("i", survey.getSurveyId());
			qList = (List<Question>)q.list();
			txn.commit();
		} catch(Exception e) {
			e.printStackTrace();
			if(txn != null) 
				txn.rollback();
		}
		
		return qList;
	}
	
	public List<ResponseOption> allResponses(Question question) {
		Session session = sessionFactory.getCurrentSession();
		Transaction txn = null;
		List<ResponseOption> qList = null;
		
		try {
			txn = session.beginTransaction();
			Query q = session.createQuery("from ResponseOption where questionId=:i");
			q.setParameter("i", question.getQid());
			qList = (List<ResponseOption>)q.list();
			txn.commit();
		} catch(Exception e) {
			e.printStackTrace();
			if(txn != null) 
				txn.rollback();
		}
		
		return qList;
	}
}