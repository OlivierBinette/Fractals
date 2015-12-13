package modele.gradients;

import java.util.List;

import modele.gradients.AbstractGradient.InterpolationType;
import modele.utils.IntColor;

public abstract class ColorGradientBuilder extends GradientBuilder<ColorGradientBuilder> 
{

	public static final int		DEF_COLOR_0;
	public static final int		DEF_COLOR_1;
	public static final int[]	DEF_COLOR_SUPP;

	static
	{
		DEF_COLOR_0 = IntColor.rgb(255, 0, 0);
		DEF_COLOR_1 = IntColor.rgb(0, 0, 0);
		DEF_COLOR_SUPP = new int[] {};
	}

	private int					color0;
	private int					color1;
	private int[]				colorsSupp;

	public ColorGradientBuilder(double dimX)
	{
		super(dimX);
		this.color0 = ColorGradientBuilder.DEF_COLOR_0;
		this.color1 = ColorGradientBuilder.DEF_COLOR_1;
		this.colorsSupp = ColorGradientBuilder.DEF_COLOR_SUPP;
	}

	public ColorGradientBuilder firstColor(int c)
	{
		this.color0 = c;
		return this;
	}

	public ColorGradientBuilder secondColor(int c)
	{
		this.color1 = c;
		return this;
	}

	public ColorGradientBuilder nextColors(int... colors)
	{
		this.colorsSupp = colors;
		return this;
	}

	public ColorGradientBuilder colors(List<C> colors)
	{
		this.color0 = colors.get(0).getColor();
		this.color1 = colors.get(1).getColor();
		this.colorsSupp = new int[colors.size() - 2];
		for (int i = 2; i < colors.size(); i++)
		{
			this.colorsSupp[i - 2] = colors.get(i).getColor();
		}

		return this;
	}

	public ColorGradientBuilder beautifulGradient()
	{
		this.deadColor = IntColor.rgb(0, 0, 0);
		this.color0 = IntColor.rgb(0, 7, 100);
		this.color1 = IntColor.rgb(32, 107, 203);
		this.colorsSupp = new int[] { IntColor.rgb(237, 255, 255),
			IntColor.rgb(255, 160, 0),
			IntColor.rgb(160, 100, 0),
			IntColor.rgb(0, 0, 0),
			IntColor.rgb(0, 3, 50),
			IntColor.rgb(0, 7, 100) };
		this.interType = InterpolationType.CYCLIC;
		this.param = 100;

		return this;
	}

	public ColorGradient build()
	{
		int[] colors = new int[this.colorsSupp.length + 2];
		colors[0] = this.color0;
		colors[1] = this.color1;
		System.arraycopy(this.colorsSupp, 0, colors, 2, this.colorsSupp.length);

		return new ColorGradient(this.interType, this.maximumPosition, this.param, this.offset, this.deadColor, colors);
	}

}
