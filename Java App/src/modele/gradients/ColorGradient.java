package modele.gradients;

import java.util.Objects;

import modele.utils.IntColor;

public class ColorGradient extends AbstractGradient 
{

	protected final int[]	colors;
	private double			stepSize;

	protected ColorGradient(InterpolationType interpolationType, double maxPos, double var, double offset, int deadColor, int... colors)
	{
		super(interpolationType, maxPos, var, offset, deadColor);
		this.colors = ColorGradient.verifyColors(colors);
		setMaxPosition(maxPos);
	}

	public synchronized int interpolate(double position)
	{
		if (position >= this.maximumPosition)
			return this.deadColor;

		int colorPos;

		position = (position + this.offset) % this.maximumPosition;
		if (position < 0)
			position += this.maximumPosition;

		if (this.interpolationType == InterpolationType.LINEAR)
		{
			position = this.maximumPosition - (this.maximumPosition / (1.0 + (position / this.param)));
			colorPos = findColor(position, this.stepSize);
			position %= this.stepSize;
			position /= this.stepSize;
		}
		else if (this.interpolationType == InterpolationType.CYCLIC)
		{
			colorPos = findColor(position, this.param / (double) (this.colors.length - 1));
			colorPos %= this.colors.length - 1;
			position %= this.param / (double) (this.colors.length - 1);
			position /= this.param / (double) (this.colors.length - 1);
		}
		else
		{
			colorPos = 0;
			position = 0;
		}

		return rgbInterpolation(this.colors[colorPos], this.colors[colorPos + 1], position);
	}

	private static int rgbInterpolation(int c0, int c1, double position)
	{
		return IntColor.rgb(
			(int) ((IntColor.red(c0) + (position * (IntColor.red(c1) - IntColor.red(c0))))),
			(int) ((IntColor.green(c0) + (position * (IntColor.green(c1) - IntColor.green(c0))))),
			(int) ((IntColor.blue(c0) + (position * (IntColor.blue(c1) - IntColor.blue(c0)))))
			);
	}

	private static int[] verifyColors(int[] colors)
	{
		Objects.requireNonNull(colors);
		if (colors.length < 2)
			throw new RuntimeException("Pas assez de couleurs!");

		return colors;
	}

	private static int findColor(double position, double stepSize)
	{
		return (int) (position / stepSize);
	}

	@Override
	public synchronized void setMaxPosition(double maxPos)
	{
		if (maxPos < 0)
			throw new IllegalArgumentException("Le paramètre doit être positif");

		this.maximumPosition = maxPos;
		this.stepSize = maxPos / (double) (this.colors.length - 1);
	}

	public static class Builder extends ColorGradientBuilder
	{
		public Builder(double dimX) 
		{
			super(dimX);
		}
	}

	public String toString()
	{
		String s = "{";
		for (int c : colors)
			s += IntColor.red(c) + ", ";
		s = s.substring(0, s.length() - 2) + "}\n";

		s += "{";
		for (int c : colors)
			s += IntColor.green(c) + ", ";
		s = s.substring(0, s.length() - 2) + "}\n";

		s += "{";
		for (int c : colors)
			s += IntColor.blue(c) + ", ";
		s = s.substring(0, s.length() - 2) + "}\n";

		return s;
	}

	/**
	 * Crée une copie de ce Gradient de couleur.
	 */
	@Override
	public ColorGradient newInstance()
	{
		return new ColorGradient(this.interpolationType, this.maximumPosition, this.param, this.offset, this.deadColor, this.colors);
	}
}
