package modele.fractal.discrete;

import java.util.Objects;

import modele.fractal.DiscreteFractal;

public class GeneralFractal extends DiscreteFractal 
{

	public static final int[][]	SIERPINSKI		= {
												{ 1, 0 },
												{ 1, 1 } };

	public static final int[][]	EPONGE			= {
												{ 1, 1, 1 },
												{ 1, 0, 1 },
												{ 1, 1, 1 } };

	public static final int[][]	FLOCON			= {
												{ 1, 0, 1 },
												{ 0, 1, 0 },
												{ 1, 0, 1 } };

	public static final int[][]	CROIX			= {
												{ 0, 1, 0 },
												{ 1, 1, 1 },
												{ 0, 1, 0 } };

	public static final int[][]	SPACE_INVADER	= {
												{ 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0 },
												{ 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0 },
												{ 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0 },
												{ 0, 0, 1, 0, 1, 1, 1, 0, 1, 0, 0 },
												{ 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0 },
												{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
												{ 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1 },
												{ 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0 },
												{ 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0 } };

	// ...

	private final int[][]		base;

	/**
	 * Constructeur simple de GeneralFractal. Prend une base qui définit la forme de la fractale.
	 *
	 * @param base la base définissant la fractale.
	 */
	public GeneralFractal(int[][] base)
	{
		super(base[0].length, base.length);
		this.base = Objects.requireNonNull(base);
	}

	@Override
	public boolean contains(int x, int y)
	{
		do {
			if (this.base[y % this.baseHeight][x % this.baseWidth] == 0)
				return false;

			x /= this.baseWidth;
			y /= this.baseHeight;

		} while ((x != 0) || (y != 0));

		return true;
	}

	@Override
	public boolean contains(int x, int y, int width, int height)
	{
		// « Nétoyage » du coin supérieur gauche.
		if ((this.base[0][0] == 0) && (x <= (width / this.baseWidth)) && (y <= (height / this.baseHeight)))
			return false;

		return this.contains(x, y);
	}

	@Override
	public DiscreteFractal clone() 
	{
		return new GeneralFractal.Builder(this).build();
	}

	@Override
	public int getBaseWidth() 
	{
		return this.baseWidth;
	}

	@Override
	public int getBaseHeight() 
	{
		return this.baseHeight;
	}

	public int[][] getBase()
	{
		return this.base;
	}

	/**
	 * Builder à utiliser de façon faculative (le constructeur de GeneralFractal est publique). Je
	 * l'ai introduit simplement pour assurer l'homogénéité du code.
	 */
	public static class Builder
	{
		private int[][]	base;

		public Builder()
		{
			this.base = GeneralFractal.SIERPINSKI;
		}

		public Builder(GeneralFractal fractal)
		{
			this.base = fractal.getBase();
		}

		public Builder setBase(int[][] base)
		{
			this.base = base;
			return this;
		}

		public GeneralFractal build()
		{
			return new GeneralFractal(this.base);
		}

	}

}
