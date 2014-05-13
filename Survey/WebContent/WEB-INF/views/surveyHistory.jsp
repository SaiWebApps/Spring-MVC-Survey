<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
  <head>
    <title> Account </title>
    <meta name="author" content="Sairam Krishnan" />
    <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate" />
    <meta http-equiv="Pragma" content="no-cache" />
    <meta http-equiv="Expires" content="0" />
    <link href="resources/css/survey.css" rel="stylesheet" type="text/css" />
    <link href="resources/css/bootstrap.min.css" rel="stylesheet" type="text/css" /> 
    <script type="text/javascript" src="resources/js/survey.js"></script>
    <script type="text/javascript" src="resources/js/bootstrap.min.js"></script>
  </head>

  <body onload="textFocus('question'); noBack();">

    <!--  Welcome message - Greet user by email. -->
	<h1> Welcome, ${user.email}. </h1>
	
	<!--  Display error message if no survey title was entered. -->
	<c:if test="${not empty titleError}">
      <div class="alert alert-error">
        <button type="button" class="close" data-dismiss="alert">&times;</button>
        <strong> Error! </strong> ${titleError}
      </div>
    </c:if>
  
	<!--  List of Previously Created Surveys -->
	<c:if test="${not empty surveyList}">
	 <table class="table table-hover">
	   <tr>
	     <th> Survey Title </th>
	     <th> Date Created </th>
	   </tr>
	   
	   <tr>
	     <c:forEach var="s" items="${surveyList}">
		    <td> ${s.surveyTitle} </td>
		    <td> ${s.dateCreated} </td>
	     </c:forEach>
	   </tr>
	 </table>
	</c:if>
	
	<!-- Form to Create New Surveys -->
	<form action="openSurvey.html" method="POST">
      <label> Survey Title </label> 
	  <div class="input-append">
		<input type="text" id="appendedInputButton" name="surveyTitle" />
		<input type="submit" class="btn btn-primary" value="Create New Survey" />
	  </div>
	</form>
	
	<!-- Logout functionality -->
	<form action="logout.html" method="POST"> <input type="submit" value="Logout" /> </form>
  </body>
</html>