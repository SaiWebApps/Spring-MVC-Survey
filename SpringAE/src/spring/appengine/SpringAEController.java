package spring.appengine;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;
import spring.appengine.dao.DAOException;
import spring.appengine.dao.SurveyDAO;
import spring.appengine.model.Question;
import spring.appengine.model.Response;
import spring.appengine.model.Survey;
import spring.appengine.model.UploadedImage;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@Controller
public class SpringAEController {

	//Initialize DAO, to manage access to the database.
	private SurveyDAO surveyDAO = new SurveyDAO();
	//AppEngine's "UserDAO" - used to handle authentication and user info
	private UserService userService = UserServiceFactory.getUserService();

	/**
	 * @param model  Spring object used to pass request attributes to the views
	 * @return "home.jsp", which will greet the user with "Welcome userEmail"
	 * and will display all the surveys that have been created thus far
	 */
	@RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST})
	public String home(Model model) {	
		User currentUser = userService.getCurrentUser();

		try {		
			//Sanitize survey titles before displaying them.
			List<Survey> surveyList = surveyDAO.getAllSurveys();
			for (Survey survey : surveyList) {
				String title = survey.getSurveyTitle();
				survey.setSurveyTitle(HtmlUtils.htmlEscape(title));
			}
			model.addAttribute("surveyList", surveyList);
			//To display current user's email in welcome message
			model.addAttribute("email", currentUser.getEmail());	
			
			/*
			 * Return the name of the JSP we want to display (home.jsp in this case). 
			 * We can just return "home" because in WEB-INF/spring/appServlet/
			 * servlet-context.xml,we configured Spring's InternalResourceViewResolver 
			 * to see "home" as WEB-INF/views/home.jsp.
			 */
			return "home";
		} catch(DAOException e) {
			//If there is a DAO-related error, return "error.jsp".
			model.addAttribute("error", "Unable To Retrieve a List of All Surveys");
			return "error";
		}
	}

	/**
	 * Log the user out of his account using AppEngine 
	 * authentication functionality.
	 */
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(HttpSession session) {
		return "redirect:" + userService.createLogoutURL("/");
	}

	/**
	 * Redirect user to the error page, and display an error message.
	 * This will be used to handle internal errors.
	 * @param model   Spring object used to pass the error message to the view
	 * @param error   The message to display
	 * @return "error.jsp"
	 */
	@RequestMapping(value = "/error/{errorMsg}", method = RequestMethod.GET) 
	public String handleInternalErrors(Model model, @PathVariable("errorMsg") String error) {
		model.addAttribute("error", error);
		return "error";
	}

	@RequestMapping(value="/createSurvey", method=RequestMethod.POST)
	public String createSurvey(Model model, @ModelAttribute("surveyBean") Survey survey) {
		String title = survey.getSurveyTitle();

		//If no title was specified, then display an error message.
		if (title.trim().isEmpty()) {
			model.addAttribute("errMsg", "Title Required");
		}
		//Otherwise, create a new Survey with the specified title, and pass
		//it along to the view.
		try {
			String email = userService.getCurrentUser().getEmail();
			Survey newSurvey = surveyDAO.saveSurvey(email,title);
			return "redirect:/fetchSurvey/" + newSurvey.getId();
		} catch(DAOException e) {
			model.addAttribute("error", "Unable to create survey.");
			return "error";
		}
	}

	/**
	 * Upon the survey owner's request, delete the Survey with the specified id.
	 * @param id   Id of the Survey that the user wishes to delete
	 * @return JSON containing a success message or an error message
	 */
	@RequestMapping(value="/deleteSurvey/{surveyId}", method=RequestMethod.POST)
	public @ResponseBody String deleteSurvey(@PathVariable("surveyId") long id) {	
		try {
			User currentUser = userService.getCurrentUser();
			String currentEmail = currentUser.getEmail();
			Survey survey = surveyDAO.getSurvey(id);
			String json = "{\"msg\":";

			//If no Survey with the given id was found, then display an 
			//error message.
			if(survey == null) {
				json += "\"No such survey exists.\"}";
				return json;
			}

			//If the current user is not the Survey's creator, then
			//display an error message.
			String ownerEmail = survey.getOwnerEmail();
			if (!currentEmail.equals(ownerEmail)) {
				json += "\"This Survey Can Only Be Deleted By Its Creator.\"}";
				return json;
			}

			//Otherwise, delete the survey and all of the questions & response
			//options associated with it.
			surveyDAO.delete(id);

			//Send over a success code and a success message.
			json += "\"Survey Successfully Deleted.\", \"code\": 1}";
			return json;
		} catch(DAOException e) {
			return "{\"internalError\":\"Unable to Delete Survey.\"}";
		}
	}

	private void mapResponsesToQuestion(List<Question> questionList, 
			Map<Long, List<Response>> map) throws DAOException {

		for (Question questionObject : questionList) {
			String questionText = questionObject.getQuestion();
			long questionId = questionObject.getId();
			long surveyId = questionObject.getSurveyId();
			List<Response> optionList = surveyDAO.getAllResponses(surveyId, questionId);

			//Sanitize the question before displaying it.
			questionObject.setQuestion(HtmlUtils.htmlEscape(questionText));

			//Sanitize the responses before displaying them.
			for (Response respObj : optionList) {
				String respText = respObj.getResponse();
				respObj.setResponse(HtmlUtils.htmlEscape(respText));
			}
			map.put(questionId, optionList);
		}
	}

	/**
	 * Fetch the requested survey.
	 * @param title   The title of the requested survey
	 * @param model   Spring object that handles controller's interactions with the views
	 * @return "survey.jsp" if the user is logged in
	 */
	@RequestMapping(value = "/fetchSurvey/{id}", method = {RequestMethod.GET, RequestMethod.POST})
	public String fetchSurvey(Model model, @PathVariable("id") long surveyId) {

		try {
			//Check whether a Survey with the given id exists.
			Survey survey = surveyDAO.getSurvey(surveyId);

			//If no Survey exists for the given id, then display an error message.
			if (survey == null) {
				model.addAttribute("msg", "The requested Survey could not be found.");
				return "forward:/";
			}

			//Pass the sanitized survey title onto the view so that 
			//it can be displayed in the page heading.
			String cleanedTitle = HtmlUtils.htmlEscape(survey.getSurveyTitle());
			model.addAttribute("surveyTitle", cleanedTitle);

			//Display all Q&A that have been created in the survey so far.
			List<Question> questionList = surveyDAO.getAllQuestions(surveyId);
			Map<Long, List<Response>> map = new HashMap<Long, List<Response>>();

			mapResponsesToQuestion(questionList, map);
			model.addAttribute("questionList", questionList);
			model.addAttribute("map", map);

			//To indicate that survey is currently being taken or is being changed;
			//value will be stored in hidden fields
			model.addAttribute("surveyId", surveyId);
			
			//To determine if the current user can create Q&A
			String email = userService.getCurrentUser().getEmail();
			model.addAttribute("isOwner", survey.getOwnerEmail().equals(email));
			return "survey";
		} catch(DAOException e) {
			//In case of error while accessing database
			String err = "Unable To Display All Q&A Created Thus Far.";
			model.addAttribute("error", err);
			return "error";
		}
	}

	@RequestMapping(value = "/submitResponse/{surveyId}", method = RequestMethod.POST)
	public String submitResponse(@PathVariable long surveyId, HttpServletRequest request) {

		try {
			//Make sure that a Survey exists for this id.
			if (surveyDAO.getSurvey(surveyId) == null) {
				return "redirect:/";
			}

			//Populate a list with the ids of all selected responses.
			List<Long> responseIdList = new ArrayList<Long>();
			for (Question questionObject : surveyDAO.getAllQuestions(surveyId)) {
				long questionId = questionObject.getId();
				long responseId = Long.parseLong(request.getParameter("" + questionId));
				responseIdList.add(responseId);
			}

			//Increment the number of votes for the Responses corresponding
			//to each id in the list.
			surveyDAO.upVote(surveyId, responseIdList);
			
			//Track user's Survey history.
			String userEmail = userService.getCurrentUser().getEmail();
			surveyDAO.saveHistoryItem(userEmail, surveyId);
			
			return "redirect:/fetchSurvey/" + surveyId; 
		} catch(NumberFormatException e) {
			request.setAttribute("error", "Invalid response option.");
			return "error";
		} catch (DAOException e) {
			request.setAttribute("error", "Unable to process survey submission.");
			return "error";
		}			
	}

	@RequestMapping(value = "/results/{surveyId}", method = RequestMethod.GET)
	public String showResults(Model model, @PathVariable long surveyId) {

		try {
			//Check whether a Survey with the given id exists. If not,
			//redirect to the home page.
			if (surveyDAO.getSurvey(surveyId) == null) {
				return "redirect:/";
			}

			List<Question> questionList = surveyDAO.getAllQuestions(surveyId);
			Map<Long, List<Response>> map = new HashMap<Long, List<Response>>();

			mapResponsesToQuestion(questionList, map);
			model.addAttribute("questionList", questionList);
			model.addAttribute("map", map);
			model.addAttribute("surveyId", surveyId);
			return "results";
		} catch(DAOException e) {
			model.addAttribute("error", "Cannot Summarize Survey Results Due" +
					"To Database Access Failure.");
			return "error";
		}
	}

	/**
	 * Create a Question, with the specified list of responses (required) and
	 * an image (not required).
	 */
	@RequestMapping(value = "/createQuestion", method = RequestMethod.POST)
	public String createQuestion(HttpServletRequest request) {
		try {
			long surveyId = 0;
			long validResponseCount = 0;
			UploadedImage imgBean = new UploadedImage();
			List<String> responseList = new ArrayList<String>();
			Question questionBean = new Question();

			if (ServletFileUpload.isMultipartContent(request)) {
				ServletFileUpload upload = new ServletFileUpload();
				FileItemIterator iterator = upload.getItemIterator(request);

				while (iterator.hasNext()) {
					FileItemStream item = iterator.next();
					String name = item.getFieldName();
					InputStream stream = item.openStream();

					if (item.isFormField()) {
						String fieldValue = Streams.asString(stream);
						if (name.equals("surveyId")) {
							surveyId = Long.parseLong(fieldValue);
							if (surveyDAO.getSurvey(surveyId) == null) {
								return "home";
							}
							imgBean.setSurveyId(surveyId);
							questionBean.setSurveyId(surveyId);
						}
						if (name.equals("question")) {
							questionBean.setQuestion(fieldValue);
						}
						if(name.equals("choices") && !fieldValue.trim().isEmpty()) {
							responseList.add(fieldValue);
							validResponseCount++;
						}
					} else {
						imgBean.setContentType(item.getContentType());
						imgBean.setFileName(item.getName());
						imgBean.setImgBytes(IOUtils.toByteArray(stream));
					}
				}

				if (questionBean.getQuestion().trim().isEmpty()) {
					request.setAttribute("errorMsg", "Question Required.");
					return "forward:/fetchSurvey/" + surveyId;
				}
				if (validResponseCount < 2) {
					request.setAttribute("errorMsg", "Please enter at least 2 response options.");
					return "forward:/fetchSurvey/" + surveyId;
				}
				surveyDAO.saveQA(questionBean, responseList, imgBean);		
			}

			return "redirect:/fetchSurvey/" + surveyId;
		} catch(FileUploadException e) {
			request.setAttribute("error", "Unable to upload image file.");
			return "error";
		} catch (IOException e) {
			request.setAttribute("error", "Unable to upload image file.");
			return "error";
		} catch (DAOException e) {
			request.setAttribute("error", "Unable to upload image file.");
			return "error";
		}
	}

	/**
	 * Fetch the image corresponding to the Survey with the given surveyId
	 * the Question with the given questionId.
	 */
	@RequestMapping(value="/fetchImage/{surveyId}/{questionId}", method=RequestMethod.GET)
	public void fetchImage(@PathVariable long surveyId, 
			@PathVariable long questionId, HttpServletResponse response) {

		try {
			UploadedImage imgBean = surveyDAO.getImage(surveyId, questionId);
			byte[] imgBytes = imgBean.getImgBytes();
			ServletOutputStream imageStream = response.getOutputStream();
			response.setContentType(imgBean.getContentType());
			imageStream.write(imgBytes);
			imageStream.close();
		} catch (DAOException e) { 
			//If there is no such image in the database
		} catch (IOException e) {
			//If there was a problem with writing the bytes to the output stream
		}
	}
	
	/**
	 * Change the title of the Survey with the given surveyId to newTitle.
	 * @param id   Id of the target Survey
	 * @param newTitle  User-specified new title for the target Survey
	 */
	@RequestMapping(value = "/changeSurveyTitle/{id}/{newTitle}", method = RequestMethod.POST)
	public void changeSurveyTitle(@PathVariable long id, @PathVariable String newTitle) {
		try {
			//Change the survey's title only if the new title is not empty.
			if (!newTitle.trim().isEmpty()) {
				surveyDAO.updateSurvey(id, newTitle);
			}
		} catch (DAOException e) {	
			
		}
	}
	
	@RequestMapping(value = "/getSuggestedSurveys", method = RequestMethod.GET)
	public String getSuggestedSurveys(Model model) {
		try {
			String email = userService.getCurrentUser().getEmail();
			List<Survey> surveyList = surveyDAO.getSuggestedSurveys(email);
			if (!surveyList.isEmpty()) {
				model.addAttribute("suggestedSurveys", surveyList);
			}
			model.addAttribute("email", email);
			return "recommendations";
		} catch (DAOException e) {
			model.addAttribute("error", "Unable to Retrieve Survey Suggestions.");
			return "error";
		}
	}
}