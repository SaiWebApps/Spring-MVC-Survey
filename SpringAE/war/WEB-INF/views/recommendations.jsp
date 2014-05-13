<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
  <head>
    <title> Suggested Surveys </title>
    <jsp:include page="head.jsp" />
  </head>
  
  <body>
   <div class="navbar navbar-inner container">
     <ul class="nav">
	  <li><a href="/"> Home </a></li>
	  <li><a href="/logout"> Logout </a></li>
	 </ul>
   </div>
   
   <h1> Suggested Surveys for ${email}</h1>
   
   <c:if test="${not empty suggestedSurveys}">
    <div class="row">
     <div class="span6 offset2">
      <table class="table table-bordered table-condensed table-hover">
       <thead>
        <tr>
          <th> Survey Title </th>
          <th> Survey Owner </th>
          <th />
        </tr>
       </thead>
     
       <tbody>
        <c:forEach var="survey" items="${suggestedSurveys}">
         <tr>
          <td> ${survey.surveyTitle} </td>
          <td> ${survey.ownerEmail} </td>
          <td><a href="/results/${survey.id}"> See Results </a></td>
         </tr>
        </c:forEach>
       </tbody>
      </table>
     </div>
    </div>
   </c:if>
  </body>
</html>