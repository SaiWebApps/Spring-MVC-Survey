<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
  <head>
    <title> Survey </title>
    <jsp:include page="head.jsp" />
  </head>

 <body onload="textFocus('question');">
      
   <!-- Navbar -->
   <div class="navbar navbar-static-top navbar-inner">
    <div class="container">
     <ul class="nav">
       <li><a href="/">Home</a></li>
	   <li><a href="/results/${surveyId}">Results</a></li>
	   <li><a href="/logout">Logout</a></li>
     </ul>
     <div class="nav-collapse collapse"></div>
    </div>
   </div>
  
   <h1 onblur="changeTitle(this, ${surveyId});" contenteditable="true"> ${surveyTitle} </h1>

   <c:if test="${not empty errorMsg}">
    <div class="row">   
     <div class="span6">
      <div class="alert alert-error"> ${errorMsg} </div>
     </div>
    </div>
   </c:if>
       
   <div class="container-fluid">
    <div class="row-fluid">
     <c:choose>
      <c:when test="${isOwner}"> <div class="span6"> </c:when>
      <c:when test="${not isOwner}"> <div class="span4 offset4"> </c:when>
     </c:choose>
     
       <div class="hero-unit">
        <c:if test="${not empty questionList}">
         <form action="/submitResponse/${surveyId}" method="POST">
           <ol>
            <c:forEach var="question_obj" items="${questionList}">
	         <li><strong> ${question_obj.question} </strong></li>
	         <img src="/fetchImage/${question_obj.surveyId}/${question_obj.id}" /> <br/>
	         <c:forEach var="response_option" items="${map[question_obj.id]}">
	           <input type="radio" name="${question_obj.id}" value="${response_option.id}" /> 
	           ${response_option.response} <br />
	         </c:forEach>
	         <br />
            </c:forEach>
           </ol>
           <input type="submit" value="Submit Survey" class="btn btn-primary" />
           <input type="reset" value="Clear Survey" class="btn btn-inverse" />
         </form>
        </c:if>
       </div>
     </div>
     
     <c:if test="${isOwner}">
      <div class="span6"> 
       <div class="hero-unit">        
         <form action="/createQuestion" method="POST" enctype="multipart/form-data">
           <input type="hidden" name="surveyId" value="${surveyId}" />
           <input type="text" name="question" placeholder="Enter a question."/> <br/>
           <input type="file" name="file" /> <br/> <br/>
           <ul id="answerChoices">       
             <!-- 1st 2 answer choices -->
   	         <li> <input type="text" name="choices" placeholder="Enter a choice." /> </li>
 	         <li> <input type="text" name="choices" placeholder="Enter a choice." /> </li>	
           </ul>
           <button class="btn btn-info" onclick="addChoice(); return false;"> Add Choice </button>
           <button class="btn btn-info" onclick="removeChoice(); return false;"> Remove Choice </button>
           <input type="submit" class="btn btn-warning" value="Post Question" />
         </form>
       </div> 
      </div>
     </c:if>
    </div>
  </div>
  
 </body>
</html>