
/* ************************************************************************
 *                  Event Handlers Definitions : Touch
 * ************************************************************************/
var debugging = false;
if( debugging ) {

    console.log( 'Debugging. ' );
    //Hammer.plugins.showTouches();
    Hammer.plugins.fakeMultitouch();

}

var hammer = new Hammer( document.getElementById('canvas'), {preventDefault: true} );

var mouseX, mouseY;
var centerPoint = {
    x : 0,
    y : 0
}
var scale = 0;

/*
 * Zooms on double tap.
 */
function onDoubleTapHammer( e ) {

    e.preventDefault();
    if(debugging) { console.log( 'Event : doubletap' ); console.log( e ); console.log( 'end' ); }

    nav.zoom( -0.4, //zoom in
        workerNav.p0.x + workerNav.getWidth() * e.gesture.center.pageX/ canvas.width, 
        workerNav.p0.y + workerNav.getHeight() * e.gesture.center.pageY / canvas.height 
    );

    instruction = JSON.stringify( new WorkerMessage( 'generate', new GeneratorInstruction( nav, 1, canvas.width, canvas.height ) ) );
    createWorker( instruction );

}

/*
 * Zooms on double click.
 */
function onDoubleClickHandler( e ) {

    e.preventDefault();
    if(debugging) { console.log( 'Event : doubleclick' ); console.log( e ); console.log( 'end' ); }

    nav.zoom( -0.4, //zoom in
        workerNav.p0.x + workerNav.getWidth() * e.pageX/ canvas.width, 
        workerNav.p0.y + workerNav.getHeight() * e.pageY / canvas.height 
    );

    instruction = JSON.stringify( new WorkerMessage( 'generate', new GeneratorInstruction( nav, 1, canvas.width, canvas.height ) ) );
    createWorker( instruction );

}



/*
 * Defines the position of the drag start.
 */
function onDragStartHammer( e ) {
    e.preventDefault( );
    if(debugging) {console.log( 'Event : dragstart' ); console.log( e ); console.log( 'end' ); }

    mouseX = e.gesture.touches[ 0 ].clientX;
    mouseY = e.gesture.touches[ 0 ].clientY;

};

/*
 * Translates the Navigator accordingly to the difference between this position and the position on the drag start. 
 * Calls an update command to the worker.
 */
function onDragEndHammer( e ) {

    if(debugging) { console.log( 'Event : dragend' ); console.log( e ); console.log( 'end' ); }

    nav.translate(
        workerNav.getWidth( ) * ( mouseX - e.gesture.touches[0].clientX ) / canvas.width,
        workerNav.getHeight( ) * ( mouseY - e.gesture.touches[0].clientY ) / canvas.height
    );

    instruction = JSON.stringify( new WorkerMessage( 'generate', new GeneratorInstruction( nav, sampling, canvas.width, canvas.height ) ) );
    createWorker( instruction );
};

/*
 * Defines the center point of the gesture.
 */
function onTransformStartHammer( e ) {

    if(debugging) { console.log( 'Event : transformstart' ); console.log( e ); console.log( 'end' ); }

    scale = 0;
    if( e.gesture.touches.length >= 2 ) {

        centerPoint.x = 0.5 * ( e.gesture.touches[ 0 ].pageX + e.gesture.touches[1].pageX );
        centerPoint.y = 0.5 * ( e.gesture.touches[ 0 ].pageY + e.gesture.touches[1].pageY );

    }

};

/*
 * Zoom out.
 */
function onPinchInHammer( e ) {

    if(debugging) { console.log( 'Event : pinchin' ); console.log( e ); console.log( 'end' ); }

    scale += 0.1 * e.gesture.scale;

};

/*
 * Zoom in.
 */
function onPinchOutHammer( e ) {

    if(debugging) { console.log( 'Event : pinchout' ); console.log( e ); console.log( 'end' ); }

    scale -= 0.04 * e.gesture.scale;

};

/*
 * Scales the Navigator on the center point.
 */
function onTransformEdHammer( e ) {

    if(debugging) { console.log( 'Event : transformend' ); console.log( e ); console.log( 'end' ); }

    if( scale > 10 )
        scale = 10;
    else if( scale < -0.7 )
        scale = -0.7;

    nav.zoom( scale, 
        workerNav.p0.x + workerNav.getWidth() * centerPoint.x / canvas.width, //TODO Le zoom se fait au mauvais endroit.
        workerNav.p0.y + workerNav.getHeight() * centerPoint.y / canvas.height 
    );

    instruction = JSON.stringify( new WorkerMessage( 'generate', new GeneratorInstruction( nav, 1, canvas.width, canvas.height ) ) );
    createWorker( instruction );

};


/* ************************************************************************
 *                  Event Handlers Definitions : Mouse
 * ************************************************************************/

/*
 * Translates the Navigator accordingly to the difference between this position and the position on the click start. 
 * Calls an update command to the worker.
 */
function onMouseUpHandler( e ) {

    if(debugging) {console.log( 'Event : mouseup' ); console.log( e ); console.log( 'end' ); }

    var deltax = mouseX - e.clientX;
    var deltay = mouseY - e.clientY;

    document.body.style.cursor = 'default';
    if(deltax || deltay) {

        nav.translate(
            workerNav.getWidth( ) * deltax / canvas.width,
            workerNav.getHeight( ) * deltay / canvas.height
        );

        instruction = JSON.stringify( new WorkerMessage( 'generate', new GeneratorInstruction( nav, 2, canvas.width, canvas.height ) ) );
        createWorker( instruction );

    }

};

/*
 * Defines the position of the click start.
 */
function onMouseDownHandler( e ) {

    if(debugging) { console.log( 'Event : mousedown' ); console.log( e ); console.log( 'end' ); }

    document.body.style.cursor = 'move';
    mouseX = e.clientX;
    mouseY = e.clientY;

};

var scale = 1;

/*
 * Zooms in or out and calls and update command to the worker.
 */
var timeoutTask;
function onMouseWheelHandler( e ) {
    e.preventDefault( );
    clearTimeout( timeoutTask );
    
    if(debugging) { console.log( 'Event : mouseWheel' ); console.log( e ); console.log( 'end' ); }

    var posX = workerNav.p0.x + ( ( e.pageX / canvas.width ) * workerNav.getWidth( ) );
    var posY = workerNav.p0.y + ( ( e.pageY / canvas.height ) * workerNav.getHeight( ) );
    var delta = 0.01 * e.detail || -0.0005 * e.wheelDelta;
    nav.zoom( ( 2 * Math.atan( delta ) ) / Math.PI, posX, posY );
    
    timeoutTask = setTimeout( onScrollEnd, 100);
};

function onScrollEnd( ) {
    instruction = JSON.stringify( new WorkerMessage( 'generate', new GeneratorInstruction( nav, 2, canvas.width, canvas.height ) ) );
    createWorker( instruction );
}


/* ************************************************************************
 *              Event Handlers Definitions : Window Events
 * ************************************************************************/

/*
 * Reinitialises the canvas, context and calls an update command to the worker.
 */
function onResizeHandler( e ) {

    if(debugging) { console.log( 'Event : onresize' ); console.log( e ); console.log( 'end' ); }

    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;

    context = canvas.getContext( '2d' );
    imageData = context.createImageData( canvas.width, canvas.height );

    instruction = JSON.stringify( new WorkerMessage( 'generate', new GeneratorInstruction( nav, 2, canvas.width, canvas.height ) ) );
    createWorker( instruction );

};


/* ************************************************************************
 *                                  MAIN
 * ************************************************************************/

function WorkerMessage( cmd, parameter ) {

    this.cmd = cmd;
    this.parameter = parameter;

    this.toString = function ( ) {
        return 'Message : cmd : ' + this.cmd + ', parameter : ' + this.parameter + ' .';
    };

};

function initCanvas( ) {

    canvas = document.getElementById( 'canvas' );
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;

    console.log( 'canvas size : ' + canvas.width + ' ' + canvas.height);

    context = canvas.getContext( '2d' );
    imageData = context.createImageData( canvas.width, canvas.height );

};

function workerOnMessage( e ) {

    if ( typeof e.data === 'string' ) {

        workerNav = Navigator.revive( JSON.parse( e.data ) );

    } else {

        imageData.data.set( e.data );
        context.putImageData( imageData, 0, 0);

    }

};

function createWorker( instruction ) {

    worker.terminate( );
    worker = new Worker( 'generator.js' );

    worker.postMessage( instruction );
    worker.onmessage = workerOnMessage;

};

function initInputs( ) {

    nav.fractal.maxIteration = 300;
    nav.fractal.colorGradient.maximalPosition = nav.fractal.maxIteration;
    document.getElementById( 'iterationsInput' ).value = 300;

    nav.fractal.colorGradient.param = 100;
    document.getElementById( 'paramInput' ).value = 100;

    nav.fractal.colorGradient.offset = 0;
    document.getElementById( 'offsetInput' ).value = 0;

    sampling = 2;
    document.getElementById( 'samplingInput' ).value = 2;

}

function updateNavigatorFromInput( ) {

    nav.fractal.maxIteration = +document.getElementById( 'iterationsInput' ).value;
    nav.fractal.colorGradient.maximalPosition = +nav.fractal.maxIteration;

    nav.fractal.colorGradient.param = +document.getElementById( 'paramInput' ).value;
    nav.fractal.colorGradient.offset = +document.getElementById( 'offsetInput' ).value;

    sampling = +document.getElementById( 'samplingInput' ).value;

}

function refreshGenerator( ) {

    updateNavigatorFromInput();
    instruction = JSON.stringify( new WorkerMessage( 'generate', new GeneratorInstruction( nav, sampling, canvas.width, canvas.height ) ) );
    createWorker( instruction );

}


/*
 * Main execution.
 */

var canvas, context, imageData,
    sampling = 2,
    supportsTouch = 'ontouchstart' in window || navigator.msMaxTouchPoints,
    worker, 
    nav = new Navigator( ),
    workerNav = nav, 
    instruction;

initCanvas( );

if ( supportsTouch ) {
    
    document.getElementById( 'menuIcon' ).className = ""; //Removes the hoverEnabled class.
    document.getElementById( 'resetButton' ).className = "button"; //Removes the buttonHover class.
    console.log( 'Device supports touch.' );

}
else {

    console.log( 'Device does not support touch.' );

}

worker = new Worker( 'generator.js' );
console.log(worker);
worker.onmessage = workerOnMessage;
instruction = JSON.stringify( new WorkerMessage( 'generate', new GeneratorInstruction( nav, sampling, canvas.width, canvas.height ) ) );
worker.postMessage( instruction );

initInputs();

/* ************************************************************************
 *                      Listeners Initialisation
 * ************************************************************************/

if ( supportsTouch ) {

    hammer.on( 'touch', function ( e ) {hideMenu();} );
    hammer.on( 'doubletap', onDoubleTapHammer );
    hammer.on( 'dragstart', onDragStartHammer );
    hammer.on( 'dragend', onDragEndHammer );
    hammer.on( 'transformstart', onTransformStartHammer );
    hammer.on( 'pinchin', onPinchInHammer );
    hammer.on( 'pinchout', onPinchOutHammer );
    hammer.on( 'transformend', onTransformEdHammer );

}
else {

    canvas.onmouseup = onMouseUpHandler;
    canvas.onmousedown = onMouseDownHandler;
    canvas.addEventListener( 'DOMMouseScroll', onMouseWheelHandler, false ); //Firefox
    canvas.addEventListener( 'mousewheel', onMouseWheelHandler, false ); //Others, but for old IE (which I don't care about).
	canvas.addEventListener('dblclick', onDoubleClickHandler, false)
}


if ( 'onorientationchange' in window ) {

    window.addEventListener( 'orientationchange', onResizeHandler );

}
window.addEventListener( 'resize', onResizeHandler );

