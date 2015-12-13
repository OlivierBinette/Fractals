
var menuShowing;
hideMenu( );

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
};