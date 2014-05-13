package com.webapp.controllers;

import java.util.*;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.webapp.helpers.FormatErrors;
import com.webapp.models.*;

@Controller
public class QuestionController {

	@Autowired private QuestionDAO qDAO;

	@RequestMapping(value="/questionPage.html", method=RequestMethod.GET) 
	public String index(Model model, HttpSession s) {
		//Make sure that the user is logged in.
		if(s.getAttribute("user") == null)
			return "home";
		//Make sure that the user is currently working on a survey.
		if(s.getAttribute("survey") == null)
			return "surveyHistory";
		
		//If possible, then get the list of questions & response options
		//that have been created thus far.
		Survey currentSurvey = (Survey)s.getAttribute("survey");
		Map<Question, List<ResponseOption>> m = new HashMap<Question, List<ResponseOption>>();
		List<Question> qList = qDAO.allQuestions(currentSurvey);
		
		for(Question q: qList) {
			List<ResponseOption> rList = qDAO.allResponses(q);
			if(m.get(q) != null) {
				List<ResponseOption> list = m.get(q);
				list.addAll(rList);
				m.remove(q);
			}
			m.put(q, rList);
		}
		
		model.addAttribute("map", m);
		return "qPage";
	}

	@RequestMapping(value="/submitQ.html", method=RequestMethod.POST)
	public String createQuestion(@ModelAttribute("qBean") @Valid Question q,
			@RequestParam("choices") List<String> responses, BindingResult res, 
			Model m, HttpSession session) {

		//Make sure that user is logged in, in case user tries to use URL
		//to navigate to page without logging in.
		if(session.getAttribute("user") == null)
			return "home";
		if(res.hasErrors()) {
			m.addAttribute("qError", FormatErrors.getErrorMsg(res, "question"));
			return "qPage";
		}
		
		Survey s = (Survey)session.getAttribute("survey");
		q.setSurveyId(s.getSurveyId());
		qDAO.createQuestion(q);

		for(int i = 0; i<responses.size(); i++) {
			String option = responses.get(i);
			if(option != null && !option.trim().isEmpty()) {
				ResponseOption r = new ResponseOption();
				r.setResponse(option);
				r.setQuestionId(q.getQid());
				qDAO.createResponseOption(r);
			}
		}
		return "redirect:/questionPage.html";
	}

	@RequestMapping(value="/done.html", method=RequestMethod.POST)
	public String completedSurvey(Model m, HttpSession session) {
		//Make sure that the user is logged in.
		if(session.getAttribute("user") == null)
			return "home";
		
		//Make sure that the user is currently working on a survey.
		if(session.getAttribute("survey")==null)
			return "surveyHistory";

		session.removeAttribute("survey");
		return "redirect:/survey.html";
	}
}