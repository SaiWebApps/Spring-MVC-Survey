package spring.appengine;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * Intercept HTTP requests and ensure that users are logged in
 * before passing the requests onto the Controller.
 * @author Sairam Krishnan
 */
public class SurveyInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request, 
			HttpServletResponse response, Object handler) throws Exception {
		
		//AppEngine's "UserDAO" - used to handle authentication and user info
		UserService userService = UserServiceFactory.getUserService();
		
		//If the user is not logged in, then redirect to the login page.
		if (userService.getCurrentUser() == null) {
			response.sendRedirect(userService.createLoginURL("/"));
			return false;
		}
		//Otherwise, forward the request to the Controller, which will invoke
		//the method mapped to this request's URI.
		return true;
	}
}