package com.webapp.models;

import java.io.Serializable;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public class SurveyDAO implements Serializable {
	
	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory f) { 
		this.sessionFactory = f; 
	}
	
	/**
	 * Create a new survey.
	 */
	public void createSurvey(Survey survey) {
		Session s = sessionFactory.getCurrentSession();
		Transaction txn = null;
		
		try {
			txn = s.beginTransaction();		
			s.save(survey);
			txn.commit();
		} catch(Exception e) {
			e.printStackTrace();
			if(txn!=null)
				txn.rollback();
		}
	}
	
	/**
	 * @return all Surveys created by User u
	 */
	public List<Survey> allSurveys(User u) {
		Session session = sessionFactory.getCurrentSession();
		Transaction txn = session.beginTransaction();
		
		Query q = session.createQuery("from Survey where userEmail=:e");
		q.setParameter("e", u.getEmail());
		List<Survey> list = (List<Survey>)q.list();
		
		txn.commit();
		return list;
	}
}