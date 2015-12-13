package modele.navigator;

import modele.fractal.DiscreteFractal;
import modele.utils.DoublePoint;

public class DiscreteFracNav extends AbstractFracNav 
{

	public DiscreteFracNav(DiscreteFractal fractal) 
	{
		this(fractal, new DoublePoint(0.0, 0.0), new DoublePoint(1.0, 1.0));
	}

	public DiscreteFracNav(DiscreteFractal fractal, DoublePoint P0, DoublePoint P1) 
	{
		super(fractal, P0, P1);
	}

	public void zoom(double scale, double pzx, double pzy)
	{
		if (this.focalPoint != null)
		{
			pzx = this.focalPoint.getX();
			pzy = this.focalPoint.getY();
		}

		final int boundX = 1;
		final int boundY = 1;

		double p0x, p0y, p1x, p1y;

		final double scalex = 1.0 + scale * this.getFractal().getBaseWidth();

		// CORRECTION DE L'ACCUMULATION D'ERREUR POUR UN 3X4 : ( / 1.065 );
		final double scaley = 1.0 + scale * this.getFractal().getBaseHeight();
		p0x = P0.getX();
		p0y = P0.getY();
		p1x = P1.getX();
		p1y = P1.getY();

		p0x = pzx - pzx * scalex + p0x * scalex;
		p0y = pzy - pzy * scaley + p0y * scaley;

		p1x = pzx - pzx * scalex + p1x * scalex;
		p1y = pzy - pzy * scaley + p1y * scaley;

		// Limites arbitraires du zoom in
		if (p1x - p0x <= 1.7724454418610591E-6)
		{
			return;
		}
		if (p1y - p0y <= 1.7724454423051483E-6)
		{
			return;
		}
		// Limites du zoom out
		if (p0x < 0)
		{
			p1x = (p1x - p0x > boundX ? boundX : p1x - p0x);
			p0x = 0;
		}
		if (p1x > boundX)
		{
			p0x = (p0x + boundX - p1x < 0 ? 0 : p0x + boundX - p1x);
			p1x = boundX;
		}
		if (p0y < 0)
		{
			p1y = (p1y - p0y > boundY ? boundY : p1y - p0y);
			p0y = 0;
		}
		if (p1y > boundY)
		{
			p0y = (p0y + boundY - p1y < 0 ? 0 : p0y + boundY - p1y);
			p1y = boundY;
		}

		if (p0x >= 0 && p1x <= boundX && p0y >= 0 && p1y <= boundY)
		{
			P0 = new DoublePoint(p0x, p0y);
			P1 = new DoublePoint(p1x, p1y);
		}
	}

	public void translate(double x, double y)
	{
		double p0x, p0y, p1x, p1y;

		p0x = P0.getX() + x;
		p0y = P0.getY() + y;
		p1x = P1.getX() + x;
		p1y = P1.getY() + y;

		final int boundX = 1;
		final int boundY = 1;

		// On subdivise pour pouvoir " glisser " le long des bords.
		if (p0x < 0)
		{
			p1x += 0 - p0x; // On laisse le zéro ici pour bien voir qu'il représente une
							// limite (bound). Si on change la limite, il faut changer ce zéro.
			p0x = 0;
		}
		if (p1x > boundX)
		{
			p0x += boundX - p1x;
			p1x = boundX;
		}
		if (p0y < 0)
		{
			p1y += 0 - p0y;
			p0y = 0;
		}
		if (p1y > boundY)
		{
			p0y += boundY - p1y;
			p1y = boundY;
		}

		if (p0x >= 0 && p1x <= boundX && p0y >= 0 && p1y <= boundY)
		{
			P0 = new DoublePoint(p0x, p0y);
			P1 = new DoublePoint(p1x, p1y);
		}
	}

	public void tryToRescale()
	{
		DoublePoint cir0 = new DoublePoint(-1, -1);
		DoublePoint cir1 = new DoublePoint(-1, -1);

		int basex = this.getFractal().getBaseWidth();
		int basey = this.getFractal().getBaseHeight();

		final int boundX = this.getFractal().getBaseWidth();
		final int boundY = this.getFractal().getBaseHeight();

		double boundingWidth = boundX / basex;
		double boundingHeight = boundY / basey;

		double startx = Math.floor(basex * P0.getX()) / basex;
		double starty = Math.floor(basey * P0.getY()) / basey;

		search:
		for (double j = starty; j < P1.getY(); j += boundingHeight)
		{
			for (double i = startx; i < P1.getX(); i += boundingWidth)
			{
				// On s'assure de recadrer seulement si on est dans le
				// fractal...
				if (this.getFractal().contains((int) (i * 10000), (int) (j * 10000)))
				{
					if (P0.getX() >= i && P0.getY() >= j && P1.getX() <= i + boundingWidth && P1.getY() <= j + boundingWidth)
					{
						cir0 = new DoublePoint(i, j);
						cir1 = new DoublePoint(i + boundingWidth, j + boundingHeight);
						this.reCadrer(cir0, cir1);

						break search;
					}
				}
			}
		}

		DoublePoint[] points = this.findGreatestVertexBounds();

		points[0] = points[0].midPoint(points[1]);
		points[2] = points[1].midPoint(points[2]);

		double p0x, p0y, p1x, p1y;
		double pzx = points[1].getX();
		double pzy = points[1].getY();
		double scalex = this.getFractal().getBaseHeight();
		double scaley = this.getFractal().getBaseHeight();

		p0x = P0.getX();
		p0y = P0.getY();
		p1x = P1.getX();
		p1y = P1.getY();

		p0x = pzx - pzx * scalex + p0x * scalex;
		p0y = pzy - pzy * scaley + p0y * scaley;

		p1x = pzx - pzx * scalex + p1x * scalex;
		p1y = pzy - pzy * scaley + p1y * scaley;

		if (p0x >= points[0].getX() && p0y >= points[0].getY() && p1x <= points[2].getX() && p1y <= points[2].getY())
		{
			this.P0 = new DoublePoint(p0x, p0y);
			this.P1 = new DoublePoint(p1x, p1y);
		}
	}

	private DoublePoint[] findGreatestVertexBounds()
	{
		int basex = this.getFractal().getBaseWidth();
		int basey = this.getFractal().getBaseHeight();

		boolean found = false;
		int n = 1;
		DoublePoint vertexC = new DoublePoint(0.0, 0.0);
		DoublePoint vertex0 = new DoublePoint(0.0, 0.0);
		DoublePoint vertex1 = new DoublePoint(0.0, 0.0);

		search:
		while (!found)
		{
			double powx = Math.pow(basex, n);
			double powy = Math.pow(basey, n);
			int kx = (int) Math.ceil(powx * P0.getX());
			int ky = (int) Math.ceil(powy * P0.getY());

			for (int j = ky; j / powy < P1.getY(); j++)
			{
				for (int i = kx; i / powx < P1.getX(); i++)
				{
					vertexC = new DoublePoint(i / powx, j / powy);
					if (vertexC.inBounds(P0.getX(), P0.getY(), P1.getX(), P1.getY()))
					{
						found = true;
						vertex0 = new DoublePoint((i - 1) / powx, (j - 1) / powy);
						vertex1 = new DoublePoint((i + 1) / powx, (j + 1) / powy);
						break search;
					}
				}
			}
			n++;
		}

		return new DoublePoint[] { vertex0, vertexC, vertex1 };
	}

	private void reCadrer(DoublePoint cir0, DoublePoint cir1)
	{
		final int boundX = this.getFractal().getBaseWidth();
		final int boundY = this.getFractal().getBaseHeight();

		P0 = P0.translate(-cir0.getX(), -cir0.getY());
		P1 = P1.translate(-cir0.getX(), -cir0.getY());
		cir1 = cir1.translate(-cir0.getX(), -cir0.getY());

		P0 = P0.scale(boundX / (double) cir1.getX(), boundY / (double) cir1.getY());
		P1 = P1.scale(boundX / (double) cir1.getX(), boundY / (double) cir1.getY());
	}

	public boolean contains(int x, int y, int width, int height) 
	{
		return this.getFractal().contains(x, y, width, height);
	}

	@Override
	public DiscreteFractal getFractal() 
	{
		return (DiscreteFractal) this.fractal;
	}

	@Override
	public DiscreteFracNav clone() 
	{
		DiscreteFractal frac = this.getFractal().clone();
		DoublePoint p0 = this.P0.clone();
		DoublePoint p1 = this.P1.clone();
		return new DiscreteFracNav(frac, p0, p1);
	}
}
