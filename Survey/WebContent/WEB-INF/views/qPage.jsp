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
  </head>

  <body onload="textFocus('question'); noBack();">

    <!--  Welcome message - Greet user by email. -->
	<h1> Welcome, ${user.email}. </h1>
	
	<!--  Show list of questions & responses that have been created so far. -->
	<c:if test="${not empty map}">
	  <c:forEach var="entry" items="${map}">
	  	<strong> ${entry.key.question} </strong> <br/>
	  	<c:forEach var="r" items="${entry.value}">
	  	   <input type="radio" name="radio" /> ${r.response} <br/>
	  	</c:forEach>
	  </c:forEach>
	  <hr/>
	</c:if>
	
	<!-- Form to allow users to post questions -->
	<form action="submitQ.html" method="POST">
	   <input type="text" name="question" /> <br/>
   	   
   	   <!-- 1st 2 answer choices -->
   	   <input type="radio" name="r" /> 
   	   <input type="text"  name="choices"/> <br /> 
 	   <input type="radio" name="r" /> 
 	   <input type="text"  name="choices" /> <br /> 
 	   
 	   <!-- 6 more answer choices - added by user -->
 	   <div id="a3"> 
 	     <input type="radio" name="r" /> 
 	     <input type="text" name="choices" /> <br /> 
 	   </div>
 	   
 	   <div id="a4"> 
 	     <input type="radio" name="r" /> 
 	     <input type="text"  name="choices" /> <br /> 
 	   </div>
 	   
 	   <div id="a5"> 
 	     <input type="radio" name="r" /> 
 	     <input type="text" name="choices" /> <br /> 
 	   </div>
 	   
 	   <div id="a6"> 
 	     <input type="radio" name="r" /> 
 	     <input type="text" name="choices" /> <br /> 
 	   </div>
 	   
 	   <div id="a7"> 
 	     <input type="radio" name="r" /> 
 	     <input type="text" name="choices" /> <br /> 
 	   </div>
 	   
 	   <div id="a8"> 
 	     <input type="radio" name="r" /> 
 	     <input type="text" name="choices" /> <br /> 
 	   </div>
 	   
	   <input type="submit" class="btn btn-primary" value="Post Question" />
	</form>
	
	<!-- Allow user to add choices dynamically -->
	<button class="btn btn-info" onclick="addChoice()"> Add Choice </button> <br/>
	
	<!-- Finish Survey. -->
	<form action="done.html" method="POST">
	  <input type="submit" class="btn" value="Done" /> <br/>
	</form>
	
	<!-- Logout functionality -->
	<form action="logout.html" method="POST"> 
	   <input type="submit" class="btn btn-danger" value="Logout" /> <br/>
	</form>
  </body>
</html>