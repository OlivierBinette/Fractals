package modele.fractal;

import modele.utils.DoublePoint;

public abstract class ContinuousFractal implements Fractal {

	public static final DoublePoint	DEF_UP_LEFT_BOUND		= new DoublePoint(-2.2, -1.2);
	public static final DoublePoint	DEF_DOWN_RIGHT_BOUND	= new DoublePoint(1.0, 1.2);

	/**
	 * Limite supérieure gauche du fractal. Ce point est pris comme position de base par le
	 * navigateur, qui ne peut le dépasse.
	 */
	protected DoublePoint			upLeftBound;

	/**
	 * Limite inférieure droite du fractal. Ce point est pris comme position de base par le
	 * navigateur, qui ne peut le dépasse.
	 */
	protected DoublePoint			downRightBound;

	public ContinuousFractal(DoublePoint P0, DoublePoint P1)
	{
		this.upLeftBound = P0;
		this.downRightBound = P1;
	}

	/**
	 * Calcule la couleur associée à un point de la fractale.
	 * 
	 * @param x la coordonnée x dans la fractale.
	 * @param y la coordonnée y dans la fractale.
	 * @return la couleur du point.
	 */
	public abstract int getColor(double x, double y);

	/**
	 * Getter de la dimension horizontale de la base.
	 * 
	 * @return baseWidth la dimension horizontale de la base.
	 */
	public abstract double getBaseWidth();

	/**
	 * Getter de la dimension verticale de la base.
	 * 
	 * @return baseWidth la dimension verticale de la base.
	 */
	public abstract double getBaseHeight();

	/**
	 * Getter de limite supérieure gauche de la fractale.
	 * 
	 * @return la limite supérieure gauche.
	 */
	public DoublePoint getUpLeftBound() 
	{
		return upLeftBound;
	}

	/**
	 * Getter de la limite inférieure droite de la fractale.
	 * 
	 * @return la limite inférieure droite.
	 */
	public DoublePoint getDownRightBound() 
	{
		return downRightBound;
	}

	public void setUpLeftBound(DoublePoint bound)
	{
		this.upLeftBound = bound;
	}

	public void setDownRightBound(DoublePoint bound)
	{
		this.downRightBound = bound;
	}

	@Override
	public abstract ContinuousFractal clone();
}
