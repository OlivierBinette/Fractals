
/**
 * @author Olivier Binette
 * 
 * Copyright (c) 2014 Olivier Binette
 * Licensed under the MIT license */

var menuShowing = false;

function toggleMenu() {
	menuShowing = !menuShowing;
	if(menuShowing)
		document.getElementById("menu").style.display = "block";
	else
		document.getElementById("menu").style.display = "none";
};

function hideMenu() {
	if(menuShowing) {
		document.getElementById("menu").style.display = "none";
		menuShowing = false;
	}
};