package com.webapp.helpers;

import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

/**
 * Collection of helper methods for the HomeController.
 * @author Sairam Krishnan
 */
public class FormatErrors {

	/**
	 * Helper method that retrieves the String error messages associated with
	 * each FieldError (an object that describes an annotation failure for a field).
	 * @param res     Results of parsing form and checking annotations
	 * @param field   A particular field for which we're getting the error msgs
	 * @return        A list of String error messages for field
	 */
	public static List<String> getErrorMessages(BindingResult res, String field) {
		List<FieldError> src = res.getFieldErrors(field);
		List<String> result = new ArrayList<String>();
		for(FieldError f: src)
			result.add(f.getDefaultMessage());
		return result;
	}
	
	/**
	 * Return a single error message associated with this field.
	 * @param res       Results of parsing form and checking annotations
	 * @param field     A particular field for which we're getting an error msg
	 * @return a single error message for this field
	 */
	public static String getErrorMsg(BindingResult res, String field) {
		return res.getFieldError(field).getDefaultMessage();
	}
}