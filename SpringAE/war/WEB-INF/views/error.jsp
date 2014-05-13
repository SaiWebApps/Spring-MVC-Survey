<!DOCTYPE html>
<html>
 <head>
   <title> Internal Server Error </title>
   <jsp:include page="head.jsp" />
 </head>

 <body>
   <div class="navbar navbar-static-top navbar-inner container">
	<ul class="nav">
	  <li><a href="/">Home</a></li>
	  <li><a href="/logout">Logout</a></li>
	</ul>
   </div>
   
   <h1> Internal Server Error - ${error}</h1>
 </body>
</html>