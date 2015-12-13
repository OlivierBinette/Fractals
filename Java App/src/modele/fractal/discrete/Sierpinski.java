package modele.fractal.discrete;

import modele.fractal.DiscreteFractal;

public class Sierpinski extends DiscreteFractal 
{

	public Sierpinski() 
	{
		super(2, 2);
	}

	@Override
	public boolean contains(int x, int y) 
	{
		return (x & ~y) == 0;
	}

	@Override
	public boolean contains(int x, int y, int width, int height) 
	{
		return this.contains(x, y);
	}

	@Override
	public DiscreteFractal clone() 
	{
		return new Sierpinski();
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

	/**
	 * Builder à utiliser de façon faculative (le constructeur de Sierpinski est publique). Je l'ai
	 * introduit simplement pour assurer l'homogénéité du code.
	 */
	public static class Builder
	{
		public Builder()
		{}

		public Sierpinski build()
		{
			return new Sierpinski();
		}
	}
}
