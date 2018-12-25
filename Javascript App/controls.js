
var menuShowing = false;
var helpMenuShowing = false;

hideMenu();
hideHelp();

function toggleMenu() {
	if(menuShowing)
		hideMenu();
	else
		showMenu();
};

function hideMenu() {
	document.getElementById( 'menu' ).style.display = 'none';
	menuShowing = false;
};

function showMenu() {
	document.getElementById( 'menu' ).style.display = 'block';
	menuShowing = true;
	hideHelp();
};

function toggleHelp() {
	if(helpMenuShowing)
		hideHelp();
	else
		showHelp();
};

function hideHelp() {
	document.getElementById( 'helpMenu' ).style.display = 'none';
	helpMenuShowing = false;
};

function showHelp() {
	document.getElementById( 'helpMenu' ).style.display = 'block';
	helpMenuShowing = true;
	hideMenu();
};
