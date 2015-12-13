
/**
 * @author Olivier Binette
 * 
 * Copyright (c) 2014 Olivier Binette
 * Licensed under the MIT license */

importScripts( 'script.js' );

/**
 * Allows to calculate the average of multiple colors and be reused.
 *
 * @param {number} size
 * @param {number} threashold
 */

function ColorSampler( size, threashold ) {
    this.position = 0;
    this.size = size;
    this.threashold = threashold;
    this.colorList = new Array( size );
};

    /**
     * Resets this ColorSampler at 0.
     */
    ColorSampler.prototype.reset = function ( ) {
        this.position = 0;
    };

    /**
     * Adds a color to the average.
     *
     * @param {Color} color - the color to add.
     */
    ColorSampler.prototype.add = function ( color ) {
        this.colorList[ this.position ] = color;
        this.position++;
    };

    /**
     * Adds a color to the average and returns true if the color difference was over the threashold specified at construction.
     * @param {Object} color - the color to add.
     */
    ColorSampler.prototype.addAndIsOverThreashold = function ( color ) {
        this.colorList[ this.position ] = color;
        this.position++;

        //@format:false;
        return this.threashold < Math.max(
            Math.abs( ( this.colorList[ this.position - 2 ].r - color.r ) ),
            Math.max(
                Math.abs( this.colorList[ this.position - 2 ].b - color.b ),
                Math.abs( this.colorList[ this.position - 2 ].g - color.g )
            )
        );
    };

    /**
     * Returns the Color average of all the colors added.
     */
    ColorSampler.prototype.getAverage = function ( ) {
        var r = 0.0,
            g = 0.0,
            b = 0.0;

        for ( var i = 0; i < this.position; i++ ) {
            r += this.colorList[ i ].r;
            g += this.colorList[ i ].g;
            b += this.colorList[ i ].b;
        }

        return new Color( r / this.position, g / this.position, b / this.position );
    };

/**
 * Parent's messages listener.
 */
onmessage = function ( onEvent ) {
    var generatorInstruction = JSON.parse( onEvent.data ).parameter;
    generateAndPost( generatorInstruction );
};

var computeImgSizes = function ( nav, screenWidth, screenHeight ) {
    var computedWidth, computedHeight;

    var width = nav.getWidth( );
    var height = nav.getHeight( );

    if ( screenWidth * height < screenHeight * width ) {
        computedWidth = screenWidth;
        computedHeight = ~~ ( ( computedWidth * height ) / width );
    } else {
        computedHeight = screenHeight;
        computedWidth = ~~ ( ( computedHeight * width ) / height );
    }

    varx = width * ( screenWidth - computedWidth ) / ( 2.0 * computedWidth );
    nav.p0 = nav.p0.plus( new Vector( -varx, 0 ) );
    nav.p1 = nav.p1.plus( new Vector( varx, 0 ) );

    vary = height * ( screenHeight - computedHeight ) / ( 2.0 * computedHeight );
    nav.p0 = nav.p0.plus( new Vector( 0, -vary ) );
    nav.p1 = nav.p1.plus( new Vector( 0, vary ) );
};

var generateAndPost = function ( generatorInstruction ) {
   	var nav = Navigator.revive( generatorInstruction.navigator ),
    	width = generatorInstruction.width,
    	height = generatorInstruction.height,
    	sample = generatorInstruction.sample;

    computeImgSizes( nav, width, height );
    self.postMessage( JSON.stringify( nav ) );

    var array = new ArrayBuffer( width * height * 4 ),
    	buffer = new Uint8Array( array );

    var deltax = nav.getWidth( ) / width,
    	deltay = nav.getHeight( ) / height;

    var index = 0,
    	c,
    	ave = new ColorSampler( sample * sample, 0.05 ),
    	x, y;

    var renderingStep = 5,
    	step = Math.pow( 2, renderingStep ),
    	variation, kx, ky, i, j, px, py;

    // Étape de génération.
    for ( variation = step; variation >= 1; variation /= 2 ) {
        // Sauts en x et y selon l'étape de génération.
        for ( kx = 0; kx < step; kx += variation ) {
            for ( ky = 0; ky < step; ky += variation ) {
                // On s'assure de ne jamais repasser sur un point déjà calculé.
                if ( kx % ( 2 * variation ) !== 0 || ky % ( 2 * variation ) !== 0 || variation === step ) {
                    for ( j = ky; j < height; j += step ) {
                        for ( i = kx; i < width; i += step ) {
                            x = nav.p0.x + i * deltax;
                            y = nav.p0.y + j * deltay;

                            ave.add( nav.fractal.getColor( x, y ) );
                            if ( sample > 1 && ave.addAndIsOverThreashold( nav.fractal.getColor( x + deltax * ( sample - 1 ) / sample, y + deltay * ( sample - 1 ) / sample ) ) ) {
                                for ( p = 1; p < sample * sample - 1; p++ ) {
                                    ave.add( nav.fractal.getColor( x + deltax * ( p % sample ) / sample, y + deltay * ( p / sample ) / sample ) );
                                }
                            }
                            c = ave.getAverage( );
                            ave.reset( );

                            // Dessin des carrés.
                            for ( py = j; py < j + variation && py < height; py++ ) {
                                for ( px = i; px < i + variation && px < width; px++ ) {
                                    index = 4 * ( width * py + px );
                                    buffer[ index ] = c.r;
                                    buffer[ index + 1 ] = c.g;
                                    buffer[ index + 2 ] = c.b;
                                    buffer[ index + 3 ] = 255;
                                }
                            }
                        }
                    }
                }
            }
        }
        self.postMessage( buffer );
    }

};