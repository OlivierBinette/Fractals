# Fractals
A Javascript Mandelbrot set explorer and it's more experimental (and performant) Java counterpart.

##Javascript App

See it running [here](http://olivierbinette.github.io/Fractals/index.html). You will need to adjust the image parameters with the menu as you zoom in. It's multi-touch compatible.

##Java App

Made with Alexandre Audet-Bouchard as a Cégep school project in 2013-2014. This is a slightly more polished version, with less features, of the original app. 

The project was highly experimental for us. We played around with multithreading and optimisation, dynamical compilation of Java code (to be able to modify in-app the fractal generating code), (naive) natural language command input and string parsing, spline smoothing of the color gradient, etc.

Here is a list of commands to type in:
- `export [horizontal size (pixels)]x[vertical size (pixel)]` Computes and saves the screen image as png, with the specified dimensions.
- `sample [n]`            Number of samples by pixel. Increase to get a smooth image.
- `[n] iterations`        Set the number of iterations to n. Default 500. Increase for more depth.
- `color bleeding [n]`    Default n=100. Increase when you zoom in.
- `color offset [n]`      Default n=0. Changes the background color (0 < n < color bleeding).
- `escape radius 1`       If you like bubbles.
- `reset`

Most of the application's internal parameters can be accessed in this way. The commands can be very loosly typed in. This feature was used during demonstrations, as it also allowed to change the color gradient, lock the zooming center to a specific point, and set boundaries.
