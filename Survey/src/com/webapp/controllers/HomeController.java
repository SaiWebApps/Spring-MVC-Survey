package com.webapp.controllers;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.*;
import org.springframework.web.bind.annotation.*;

import com.webapp.helpers.FormatErrors;
import com.webapp.helpers.ProtectPassword;
import com.webapp.models.*;
import java.util.*;

/**
 * Manage the Home page (login and registration forms).
 * @author Sairam Krishnan
 */
@Controller
public class HomeController {

	@Autowired private UserDAO userDAO;
	
	@RequestMapping(value = {"/", "/login.html", "/register.html"}, method = RequestMethod.GET)
	public String home(Model model, HttpSession s) { 
		if(s.getAttribute("user")!=null)
			return "surveyHistory";
		return "home"; 
	}

	@RequestMapping(value="/login.html", method = RequestMethod.POST)
	public String login(@ModelAttribute("userBean") @Valid User u, 
			BindingResult res, Model m, HttpSession s) {

		//Check for annotation failures, and display the appropriate error messages.
		if(res.hasErrors()) {			
			//Format the email field's annotation failures into a list of strings.
			List<String> emailErrors = FormatErrors.getErrorMessages(res, "email");
			
			//Format the password field's annotation failures into a list of strings.
			List<String> passwordErrors = FormatErrors.getErrorMessages(res, "password");
			
			//Set attributes, so that the view can iterate through each list,
			//and display each error message for the relevant fields.
			m.addAttribute("loginEmailErrors", emailErrors);
			m.addAttribute("loginPasswordErrors", passwordErrors);
			return "home";
		}
		
		//Check to see if a User w/ the specified email and password exists.
		if(!userDAO.userFound(u)) {
			m.addAttribute("globalLoginError", "No such user exists!");
			return "home";
		}

		//No errors found, so log the user in to her account.
		s.setAttribute("user",u);
		return "redirect:/survey.html";
	}

	@RequestMapping(value="/register.html", method = RequestMethod.POST)
	public String register(@ModelAttribute("userBean") @Valid User u, 
			BindingResult res, Model m) {

		if(res.hasErrors()) {
			//Format the email field's annotation failures into a list of strings.
			List<String> emailErrors = FormatErrors.getErrorMessages(res,"email");
			
			//Format the password field's annotation failures into a list of strings.
			List<String> passwordErrors = FormatErrors.getErrorMessages(res,"password");
			
			m.addAttribute("regEmailErrors", emailErrors);
			m.addAttribute("regPasswordErrors", passwordErrors);
			return "home";
		}
		if(userDAO.findUserByEmail(u)) {
			m.addAttribute("globalRegError","User already exists!");
			return "home";
		}

		//Generate a salt and a hash of the password+salt.
		String salt = ProtectPassword.generateSalt();
		String hashed_passwd = ProtectPassword.hash(u.getPassword(), salt);
		
		//Set the User's salt and password.
		u.setSalt(salt);
		u.setPassword(hashed_passwd);
		
		//Create an entry for this user in the database.
		userDAO.createUser(u);
		
		return "home";
	}
}