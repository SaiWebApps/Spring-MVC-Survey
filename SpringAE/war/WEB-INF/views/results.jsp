<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
 <head>
   <title> Survey Results </title>
   <jsp:include page="head.jsp" />
 </head>

 <body>   
   <!-- Navbar -->
   <div class="navbar navbar-inner container">
     <ul class="nav">
	  <li><a href="/"> Home </a></li>
	  <li><a href="/fetchSurvey/${surveyId}"> Return To Survey </a></li>
	  <li><a href="/logout"> Logout </a></li>
	 </ul>
   </div>
   
   <c:if test="${not empty questionList}">
    <c:forEach var="question_obj" items="${questionList}">
      <div class = "featurette">
       <h2 class="featurette-heading"> ${question_obj.question} </h2>
       <div class="row">
        <div class="span4 offset1">
         <table class="table table-bordered table-condensed table-hover">
          <thead>         
            <tr>
             <th> Response Option </th>
             <th> # of Votes </th>
            </tr>
          </thead> 
       
          <tbody>
           <c:forEach var="resp_obj" items="${map[question_obj.id]}">
             <tr>
               <td> ${resp_obj.response} </td>
               <td> ${resp_obj.numVotes}</td>
             </tr>
           </c:forEach>
          </tbody>
         </table>
        </div>
       </div>
      </div>
      <hr class="featurette-divider" />
   	</c:forEach>
   </c:if>
   
 </body>
</html>