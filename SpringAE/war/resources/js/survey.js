var xmlhttp;
if(window.XMLHttpRequest) { xmlhttp = new XMLHttpRequest(); }
else { xmlhttp = new ActiveXObject("Microsoft.XMLHTTP"); }
	
function textFocus(str) { document.getElementsByName(str)[0].focus(); }

function addChoice() {
	var answerChoices = document.getElementById("answerChoices");
	var listItem = document.createElement('li');
	listItem.innerHTML = "<input type=\"text\" name=\"choices\" placeholder=\"Enter a choice.\" />";
	answerChoices.appendChild(listItem);
}

function removeChoice() {
	var answerChoices = document.getElementById("answerChoices");
	var length = answerChoices.childNodes.length;
	if(length > 2) {
	   answerChoices.removeChild(answerChoices.childNodes[length-1]);
	}
}

function changeTitle(elem, id) {
    var newTitle = elem.innerHTML;
    if(newTitle.length >= 6) {
    	newTitle = newTitle.substring(6);
    }
	xmlhttp.open("POST", "/changeSurveyTitle/" + id + "/" + newTitle, true);
	xmlhttp.send();
}

function deleteWithoutJQuery(n) {
	xmlhttp.onreadystatechange = function() {
		if(xmlhttp.readyState==4 && xmlhttp.status==200) {
			var data = JSON.parse(xmlhttp.responseText);
			var msg = document.getElementById("msg");
			msg.style.display = "block";
			msg.innerHTML = data.msg;

			if(data.code) {	
				var targetRow = document.getElementById("row_" + n);
				document.getElementById("table").deleteRow(targetRow.rowIndex);
			}
			else if(data.internalError) {
				window.navigate("/error/" + data.internalError);
			}
		}
	}
	xmlhttp.open("POST", "/deleteSurvey/" + n, true);
	xmlhttp.send();
}

function deleteViaJQuery(n) {
	$.post("/deleteSurvey/" + n, function(results) {
		var data = JSON.parse(results);
		var msg = document.getElementById("msg");
		msg.style.display = "block";
		msg.innerHTML = data.msg;

		if(data.code) {	
			var targetRow = document.getElementById("row_" + n);
			document.getElementById("table").deleteRow(targetRow.rowIndex);
		}
		else if(data.internalError) {
			window.navigate("/error/" + data.internalError);
		}
	});
}

function deleteSurvey(method, n) {
	if(method == 0) {
		deleteWithoutJQuery(n);
	}
	else if(method == 1) {
		deleteViaJQuery(n);
	}
}