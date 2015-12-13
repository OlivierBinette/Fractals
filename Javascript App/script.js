/**
 * @author Olivier Binette
 * 
 * Copyright (c) 2014 Olivier Binette
 * Licensed under the MIT license */

/**
 * A 8-bit rgb color object.
 *
 * @param {number} r - the red color component, an integer between 0 and 255 (inclusive).
 * @param {number} g - the green color component, an integer between 0 and 255 (inclusive).
 * @param {number} b - the blue color component, an integer between 0 and 255 (inclusive).
 */

function Color ( r, g, b ) {
    this.r = r;
    this.g = g;
    this.b = b;
};
	/**
     * Returns the Color interpolated between this color and the color passed as a parameter, at the
     * position specified (between 0.0 - 1.0, inclusively).
     *
     * @param {Color} color - the color to interpolate towards.
     * @param {number} position - the position between this color and the next.
     */
    Color.prototype.interpolateTo = function ( color, position ) {
        //@format:false
        return new Color(
            this.r + position * ( color.r - this.r ),
            this.g + position * ( color.g - this.g ),
            this.b + position * ( color.b - this.b )
        );
    };

    /**
     * Returns a copy of this Color.
     */
    Color.prototype.clone = function ( ) {
        return new Color( this.r, this.g, this.b );
    };

    /**
     * Returns a String representation of this Color.
     */
    Color.prototype.toString = function ( ) {
        return '{ RGB Color : ( ' + this.r + ', ' + this.g + ', ' + this.b + ' ) }';
    };

	/**
	 * Deserialises value and returns a new Color.
	 * @param {Object} value - the object to deserialise.
	 */
	Color.revive = function ( value ) {
	    return new Color( value.r, value.g, value.b );
	};


/**
 * A vector object, with basic operations.
 *
 * @param {number} x - the horizontal component of the vector.
 * @param {number} y - the vertical component of the vector.
 */

function Vector ( x, y ) {
    this.x = x;
    this.y = y;
}

	/**
     * Returns a new Vector corresponding to this + vector.
     * @param {Vector} vector - the vector to sum with this.
     */
    Vector.prototype.plus = function ( vector ) {
        return new Vector( this.x + vector.x, this.y + vector.y );
    };

    /**
     * Returns a new Vector corresponding to this * scalar.
     * @param {number} scalar - the number to multiply with this.
     */ 
    Vector.prototype.times = function ( scalar ) {
        return new Vector( this.x * scalar, this.y * scalar );
    };

    /**
     * Returns a copy of this Vector.
     */
    Vector.prototype.clone = function ( ) {
        return new Vector( this.x, this.y );
    };

    /**
     * Returns a String representation of this Vector.
     */
    Vector.prototype.toString = function ( ) {
        return '[ ' + this.x + ', ' + this.y + ' ]';
    };

	/**
	 * Deserialises value and returns a new Vector.
	 * @param {Object} value - the object to deserialise.
	 */
	Vector.revive = function( value ) {
	    return new Vector( value.x, value.y );
	};

/**
 * A custom color gradient that can be used to get colors corresponding to a position between 0.0 and maximalPosition, inclusively.
 *
 * @param {number} maximalPosition - the position corresponding to the last color in the specified list.
 * @param {Array} colorList - the list of colors used to create the gradient.
 */

function ColorGradient( maximalPosition, colorList ) {
    this.DEAD_COLOR = new Color( 0, 0, 0 );

    this.maximalPosition = maximalPosition;
    this.colorList = colorList;
    this.param = 100.0;
    this.offset = 0.0;
}

    /**
     * Returns the Color corresponding to the position specified.
     *
     * @param {number} position - the position at which to get the Color (between 0.0 and maximalPosition).
     */
    ColorGradient.prototype.interpolate = function ( position ) {
        if ( position >= this.maximalPosition ) {
            return this.DEAD_COLOR;
        }

        var colorPosition;

        position += this.offset;
        position %= this.maximalPosition;
        if ( position < 0 ) {
            position += this.maximalPosition;
        }

        colorPosition = ~~ ( position * ( this.colorList.length - 1 ) / this.param );
        colorPosition %= ( this.colorList.length - 1 );
        position %= this.param / ( this.colorList.length - 1 );
        position /= this.param / ( this.colorList.length - 1 );

        return this.colorList[ colorPosition ].interpolateTo( this.colorList[ colorPosition + 1 ], position );
    };

    ColorGradient.prototype.clone = function ( ) {
        var gradient = new ColorGradient( this.maximalPosition, this.colorList );
        gradient.param = this.param;
        gradient.offset = this.offset;
        return gradient;
    };

	/**
	 * Deserialises value and returns a new ColorGradient.
	 * @param {Object} value - the object to deserialise.
	 */
	ColorGradient.revive = function ( value ) {
	    var list = [ ];
	    for ( var i = 0; i < value.colorList.length; i++ ) {
	        list.push( Color.revive( value.colorList[ i ] ) );
	    }
	    var grad = new ColorGradient( value.maximalPosition, list );
	    grad.param = value.param;
	    grad.offset = value.offset;
	    return grad;
	};

/**
 * A Mandelbrot set with default parameters, such as a 'beautiful' color gradient.
 */

function Mandelbrot( ) {
    var DEFAULT_MAX_POSITION = 400.0;
    //@format:false
    var DEFAULT_COLOR_GRADIENT = new ColorGradient( DEFAULT_MAX_POSITION, [ new Color( 0, 7, 100 ),
        new Color( 237, 255, 255 ),
        new Color( 255, 160, 0 ),
        new Color( 160, 100, 0 ),
        new Color( 0, 0, 0 ),
        new Color( 0, 3, 50 ),
        new Color( 0, 7, 100 )
    ] );

    this.colorGradient = DEFAULT_COLOR_GRADIENT;
    this.maxIteration = DEFAULT_MAX_POSITION;
    this.escapeRadius = 50;
}

    /**
     * Returns true if the specified point is located in the main cardioid of the Mandelbrot set, false otherwise.
     *
     * @param {number} x - the x coordinate of the point, on the real axis.
     * @param {number} y - this y coordinate of the point, on the imaginary axis.
     */
    Mandelbrot.prototype._inCardiod = function ( x, y ) {
        var t, rn, r1, r2;

        t = Math.atan( ( x - 0.25 ) / y ) - Math.PI / 2.0;

        rn = Math.sqrt( ( x - 0.25 ) * ( x - 0.25 ) + y * y ) * ( y > 0 ? 1 : -1 );
        r1 = -( 1 + Math.cos( t ) ) / 2.0;
        r2 = ( 1 - Math.cos( t ) ) / 2.0;

        if ( rn >= r1 && rn <= r2 ) {
            return true;
        }

        return false;
    };

	/**
     * Calculates the Color corresponding to a particular point in the plane.
     *
     * @param {number} x - the x coordinate in the complex plane, on to the real axis.
     * @param {number} y - the y coordinate in the complex plane, on the imaginary axis.
     */
    Mandelbrot.prototype.getColor = function ( x, y ) {

        if ( this._inCardiod( x, y ) ) {
            return this.colorGradient.interpolate( this.maxIteration );
        }

        var iteration = 0.0,
            zRe = 0.0,
            zImg = 0.0,
            temp, z, n;

        while ( ( zRe * zRe + zImg * zImg < this.escapeRadius ) && ( iteration < this.maxIteration ) ) {
            temp = zRe * zRe - zImg * zImg + x;
            zImg = 2 * zRe * zImg + y;
            zRe = temp;

            iteration += 1.0;
        }

        // On ajuste la couleur pour un gradient continu. Formule de wikipédia.
        if ( iteration < this.maxIteration ) {
            z = zRe * zRe + zImg * zImg;
            n = Math.log( ( 0.5 * Math.log( z ) ) / Math.log( 2.0 ) ) / Math.log( 2.0 );
            iteration = iteration - n + 1.0;
        }

        return this.colorGradient.interpolate( iteration );
    };

	/**
	 * Deserialises value and returns a new Mandelbrot.
	 * @param {Object} value - the object to deserialise.
	 */
	Mandelbrot.revive = function ( value ) {
	    var m = new Mandelbrot( );
	    m.colorGradient = ColorGradient.revive( value.colorGradient );
	    m.maxIteration = value.maxIteration;
	    m.escapeRadius = value.escapeRadius;
	    return m;
	};

/**
 * A navigator that allows to move in a fractal.
 */

function Navigator( ) {
    this.p0 = new Vector( -2.0, -1.5 );
    this.p1 = new Vector( 1.0, 1.5 );
    this.fractal = new Mandelbrot( );
}

    /**
     * Translates this Navigator.
     * @param {number} dx - the horizontal translation.
     * @param {number} dy - the vertical translation.
     */
    Navigator.prototype.translate = function ( dx, dy ) {
        var delta = new Vector( dx, dy );
        this.p0 = this.p0.plus( delta );
        this.p1 = this.p1.plus( delta );
    };

    /**
     * Translastes p0 and p1 to create a focalized zoom.
     * @param  {number} scale - the scale of the zoom.
     * @param  {number} pzx - the x coordinate of the focal point, in this Navigator's referencial.
     * @param  {[number} pzy - the y coordinate of the focal point, in this Navigator's referencial.
     */
    Navigator.prototype.zoom = function ( scale, pzx, pzy ) {
        scale += 1;
        var p0x, p0y, p1x, p1y;

        p0x = this.p0.x;
        p0y = this.p0.y;
        p1x = this.p1.x;
        p1y = this.p1.y;

        p0x = pzx - pzx * scale + p0x * scale;
        p0y = pzy - pzy * scale + p0y * scale;

        p1x = pzx - pzx * scale + p1x * scale;
        p1y = pzy - pzy * scale + p1y * scale;

        this.p0 = new Vector( p0x, p0y );
        this.p1 = new Vector( p1x, p1y );
    };

    /**
     * Returns the width of this Navigator, that is the horizontal distance between p0 and p1.
     */
    Navigator.prototype.getWidth = function( ) {
        return this.p1.x - this.p0.x;
    };

    /**
     * Returns the height of this Navigator, that is the vertical distance between p0 and p1.
     */
    Navigator.prototype.getHeight = function( ) {
        return this.p1.y - this.p0.y;
    };

    Navigator.prototype.clone = function( ) {
        var nav = new Navigator( );
        nav.p0 = this.p0.clone( );
        nav.p1 = this.p1.clone( );
        nav.fractal = this.fractal.clone( );
        return nav;
    };

	/**
	 * Deserialises value and returns a new Navigator.
	 * @param {Object} value - the object to deserialise.
	 */
	Navigator.revive = function( value ) {
	    var n = new Navigator( );
	    n.fractal = Mandelbrot.revive( value.fractal );
	    n.p0 = Vector.revive( value.p0 );
	    n.p1 = Vector.revive( value.p1 );
	    return n;
	};

/**
 * A general instruction to be sent to the generator worker.
 * @param {Navigator} navigator - the Navigator to render.
 * @param {number} sample - the number of sample to take per pixel.
 * @param {number} width - the width of the image to generate.
 * @param {[type]} height - the height of the image to generate.
 */

function GeneratorInstruction( navigator, sample, width, height ) {
    this.navigator = navigator;
    this.width = width;
    this.height = height;
    this.sample = sample;

    this.toString = function( ) {
        return 'GeneratorInstruction : navigator : ' + this.navigator +
            ', width : ' + this.width +
            ', height : ' + this.height +
            ', sample : ' + this.sample + ' .';
    };
}