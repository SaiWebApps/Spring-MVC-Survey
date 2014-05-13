package com.webapp.models;

import org.hibernate.*;
import org.springframework.stereotype.Repository;
import com.webapp.helpers.ProtectPassword;
import java.io.Serializable;
import java.util.List;

@Repository
public class UserDAO implements Serializable {
	
	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory f) { 
		this.sessionFactory = f; 
	}

	/**
	 * Given user details in registration form, create a new user.
	 * @param u  User object containing details from reg. form
	 */
	public void createUser(User u) {
		Session s = sessionFactory.getCurrentSession();
		Transaction txn = null;
		
		try {
			txn = s.beginTransaction();
			s.save(u);
			txn.commit();
		} catch(Exception e) {
			if(txn!=null) txn.rollback();
		}
	}
	
	/**
	 * Query database to find users w/ specified email address.
	 */
	private List<?> emailQuery(Session s, User u) {
		Query q = s.createQuery("from User where email=:e");
		q.setParameter("e", u.getEmail());
		return q.list();
	}
	
	/**
	 * Determine if there are any users w/ specified email address.
	 */
	public boolean findUserByEmail(User u) {
		Session s = sessionFactory.getCurrentSession();
		boolean res = false;
		
		Transaction txn = s.beginTransaction();
		List<?> list = emailQuery(s,u);
		res = list != null && list.size() > 0;
		txn.commit();
		
		return res;
	}
	
	/**
	 * Return true if a user w/ the specified email & password is found.
	 */
	public boolean userFound(User u) {
		Session s = sessionFactory.getCurrentSession();
		boolean res = false;
		
		Transaction txn = s.beginTransaction();
		List<User> uList = (List<User>) emailQuery(s,u);

		if(uList != null && uList.size() > 0) {
			User target = uList.get(0);
			String entered = ProtectPassword.hash(u.getPassword(),target.getSalt());
			String actual = target.getPassword();
			res = entered.equals(actual);
		}
		
		txn.commit();
		return res;
	}
}