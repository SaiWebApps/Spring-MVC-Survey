package com.webapp.controllers;

import java.util.Date;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.webapp.helpers.FormatErrors;
import com.webapp.models.Survey;
import com.webapp.models.SurveyDAO;
import com.webapp.models.User;

@Controller
public class HistoryController {

	@Autowired private SurveyDAO surveyDAO;

	/**
	 * Log the user out if she's logged in.
	 * @return "home.jsp" and clear session attributes
	 */
	@RequestMapping(value = "/logout.html", method = RequestMethod.POST)
	public String logout(Model model, HttpSession s) { 
		s.removeAttribute("user");
		return "home";
	}

	@RequestMapping(value={"/survey.html", "/done.html"}, method=RequestMethod.GET) 
	public String surveyHome(Model model, HttpSession s) {
		if(s.getAttribute("user") == null)
			return "home";

		User u = (User)s.getAttribute("user");
		model.addAttribute("surveyList", surveyDAO.allSurveys(u));
		return "surveyHistory";
	}

	@RequestMapping(value="/openSurvey.html", method=RequestMethod.POST)
	public String openSurvey(@ModelAttribute("sBean") @Valid Survey survey, 
			BindingResult res, Model m, HttpSession s) {

		//Make sure that user is logged in, in case user tries to use URL
		//to navigate to page without logging in.
		if(s.getAttribute("user") == null)
			return "home";

		//Send form errors to view, so that it can display them.
		if(res.hasErrors()) {
			m.addAttribute("titleError", FormatErrors.getErrorMsg(res,"surveyTitle"));
			return "surveyHistory";
		}

		User u = (User)s.getAttribute("user");
		survey.setUserEmail(u.getEmail());
		survey.setDateCreated(new Date().toString());
		surveyDAO.createSurvey(survey);
		s.setAttribute("survey", survey);
		return "qPage";
	}
}