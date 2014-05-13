<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
 <head>
   <title> Home </title>
   <jsp:include page="head.jsp" />
 </head>

 <body onload="textFocus('surveyTitle');">
 
   <div class="navbar navbar-inner container">
	<ul class="nav">
	  <li><a href="/">Home</a></li>
	  <li><a href="/getSuggestedSurveys"> Suggested Surveys </a></li>
	  <li><a href="/logout">Logout</a></li>
	</ul>
   </div>
   
   <h1> Welcome, ${email}. </h1>

   <div class="row">   
    <div class="span4 offset4">
      <div id="msg" class="alert alert-info"><strong> </strong></div>
    </div>
   </div>
	     
   <div class="row">
    <div class="span10 offset4">
     <table id="table" class="table table-condensed table-hover">
	  <tr> 
	   <th />
	   <th> Survey Title </th>  
	   <th> Creator </th>
	   <th> Date Created </th> 
	  </tr>
	
	  <c:forEach var="s" items="${surveyList}">
	   <tr id="row_${s.id}">
	    <td> 
	      <input type="submit" class="btn-mini btn-danger" 
	      	onclick="deleteSurvey(1, ${s.id});" value="&times;" />
	    </td>
	    <td> <a href="/fetchSurvey/${s.id}"> ${s.surveyTitle} </a> </td>
	    <td> ${s.ownerEmail} </td>
	    <td> ${s.dateCreated} </td>
	    <td> <a href="/results/${s.id}"> See Results </a> </td>
	   </tr>
	  </c:forEach>
     </table>
	</div>
   </div>
	
   <div class="row">
    <div class="span6 offset2">
     <label> Survey Title </label>
     <form action="/createSurvey" method="POST">
      <div class="input-append">
        <input type="text" id="appendedInputButton" name="surveyTitle" />
        <input type="submit" class="btn btn-primary" value="Create Survey" />
      </div>
     </form>
    </div>
   </div>
 </body>
</html>