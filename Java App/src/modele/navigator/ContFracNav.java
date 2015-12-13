package modele.navigator;

import modele.fractal.ContinuousFractal;
import modele.utils.DoublePoint;

public class ContFracNav extends AbstractFracNav 
{

	public ContFracNav(ContinuousFractal fractal) 
	{
		this(fractal, fractal.getUpLeftBound(), fractal.getDownRightBound());
	}

	public ContFracNav(ContinuousFractal fractal, DoublePoint P0, DoublePoint P1)
	{
		super(fractal, P0, P1);
	}

	public void zoom(double scalex, double scaley, double pzx, double pzy)
	{
		if (this.focalPoint != null)
		{
			pzx = this.focalPoint.getX();
			pzy = this.focalPoint.getY();
		}

		scalex += 1;
		scaley += 1;
		double p0x, p0y, p1x, p1y;

		p0x = P0.getX();
		p0y = P0.getY();
		p1x = P1.getX();
		p1y = P1.getY();

		p0x = pzx - pzx * scalex + p0x * scalex;
		p0y = pzy - pzy * scaley + p0y * scaley;

		p1x = pzx - pzx * scalex + p1x * scalex;
		p1y = pzy - pzy * scaley + p1y * scaley;

		// Limites arbitraires du zoom in
		if (p1x - p0x <= Math.pow(10, -15) * (getFractal().getDownRightBound().getX() - getFractal().getUpLeftBound().getX()))
		{
			return;
		}
		if (p1y - p0y <= Math.pow(10, -15) * (getFractal().getDownRightBound().getY() - getFractal().getUpLeftBound().getY()))
		{
			return;
		}

		P0 = new DoublePoint(p0x, p0y);
		P1 = new DoublePoint(p1x, p1y);
	}

	@Override
	public void zoom(double scale, double pzx, double pzy) 
	{
		this.zoom(scale, scale, pzx, pzy);
	}

	public void translate(double x, double y)
	{
		double p0x, p0y, p1x, p1y;

		p0x = P0.getX() + x;
		p0y = P0.getY() + y;
		p1x = P1.getX() + x;
		p1y = P1.getY() + y;

		this.P0 = new DoublePoint(p0x, p0y);
		this.P1 = new DoublePoint(p1x, p1y);
	}

	@Override
	public ContinuousFractal getFractal() 
	{
		return (ContinuousFractal) this.fractal;
	}

	@Override
	public ContFracNav clone() 
	{
		return new ContFracNav(this.getFractal().clone(), this.P0.clone(), this.P1.clone());
	}

}
