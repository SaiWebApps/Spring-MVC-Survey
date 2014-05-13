package spring.appengine.dao;

/**
 * Used to encapsulate database-specific errors from the Controller and the User.
 * @author Sairam Krishnan
 */
public class DAOException extends Exception {
	public DAOException() { super(); }
	public DAOException(String msg) { super(msg); }
	public DAOException(RuntimeException e) { super(e.getMessage()); }
}
