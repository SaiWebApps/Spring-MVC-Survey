<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
 <head>
   <title>Home</title>
   <meta name="author" content="Sairam Krishnan" />
   <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate" />
   <meta http-equiv="Pragma" content="no-cache" />
   <meta http-equiv="Expires" content="0" />
   <link href="resources/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
   <script src="http://code.jquery.com/jquery-latest.min.js" type="text/javascript"></script>
   <script type="text/javascript" src="resources/js/survey.js"></script>
   <script type="text/javascript" src="resources/js/bootstrap.min.js"></script>
 </head>

 <body onload="textFocus('email'); noBack();">  
  
  <!-- Display global login error message if user authentication failed. -->
  <c:if test="${not empty globalLoginError}">
   <div class="alert alert-error">
     <button type="button" class="close" data-dismiss="alert">&times;</button>
     <strong> Unsuccessful Login Attempt! </strong> ${globalLoginError}
   </div>
  </c:if>
  
  <!-- LOGIN FORM --> 
  <div class="container">
    <strong> LOGIN </strong>
    <form method="POST" action="login.html">
    
      <!-- Login Email -->
      <c:choose>
        <c:when test="${not empty loginEmailErrors}">
          <div class="control-group error">
            <div class="controls"> 
              Email: <input type="text" name="email" id="inputError">
            </div>
            <c:forEach var="e" items="${loginEmailErrors}">
              <label class="control-label" for="inputError"> ${e} </label>
            </c:forEach>
          </div>
        </c:when>
        
        <c:when test="${empty loginEmailErrors}">
           Email: <input type="text" name="email" /> <br/>
        </c:when>
      </c:choose>
            
      <!-- Login Password -->
      <c:choose>
        <c:when test="${not empty loginPasswordErrors}">
          <div class="control-group error">
            <div class="controls"> 
              Password: <input type="password" name="password" id="inputError">
            </div>
            <c:forEach var="e" items="${loginPasswordErrors}">
              <label class="control-label" for="inputError"> ${e} </label>
            </c:forEach>
          </div>
        </c:when>
        
        <c:when test="${empty loginPasswordErrors}">
           Password: <input type="password" name="password" /> <br/>
        </c:when>
      </c:choose>
      
      <input type="submit" class="btn btn-primary" value="Login" />
    </form>  
  </div>
  
  <br/>

  <!-- Display global registration error message if user is already registered. -->
  <c:if test="${not empty globalRegError}">
    <div class="alert alert-error">
      <button type="button" class="close" data-dismiss="alert">&times;</button>
      <strong> Unsuccessful Login Attempt! </strong> ${globalRegError}
    </div>
  </c:if>
  
  <!-- REGISTRATION FORM -->
  <div class="container">
    <strong> REGISTRATION </strong>
    <form method="POST" action="register.html">
    
      <!-- Registration Email -->
      <c:choose>
        <c:when test="${not empty regEmailErrors}">
          <div class="control-group error">
            <div class="controls"> 
              Email: <input type="text" name="email" id="inputError">
            </div>
            <c:forEach var="e" items="${regEmailErrors}">
              <label class="control-label" for="inputError"> ${e} </label>
            </c:forEach>
          </div>
        </c:when>
        
        <c:when test="${empty regEmailErrors}">
           Email: <input type="text" name="email" /> <br/>
        </c:when>
      </c:choose>
      
      <!-- Password -->
      <c:choose>
        <c:when test="${not empty regPasswordErrors}">
          <div class="control-group error">
            <div class="controls"> 
              Password: <input type="password" name="password" id="inputError">
            </div>
            <c:forEach var="e" items="${regPasswordErrors}">
              <label class="control-label" for="inputError"> ${e} </label>
            </c:forEach>
          </div>
        </c:when>
        
        <c:when test="${empty regPasswordErrors}">
           Password: <input type="password" name="password" /> <br/>
        </c:when>
      </c:choose>
      
      <input type="submit" class="btn btn-primary" value="Register" />
    </form>  
  </div>
 </body>
</html>
