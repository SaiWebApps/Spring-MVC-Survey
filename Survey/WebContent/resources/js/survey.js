var num = 2;

function textFocus(str) { document.getElementsByName(str)[0].focus(); }

function noBack() { window.history.forward(); }

function addChoice() {
	if(num < 8) {
		num++;
		document.getElementById("a"+num).style.display = "block";
	}
}