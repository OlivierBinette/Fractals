package modele.gradients;

import java.util.ArrayList;
import java.util.List;

import modele.gradients.AbstractGradient.InterpolationType;
import modele.utils.IntColor;

public abstract class BezierGradientBuilder extends GradientBuilder<BezierGradientBuilder> 
{

	public static final List<C>	DEF_COLORS;
	public static final int		DEF_PALETTE_SIZE;

	static
	{
		DEF_COLORS = new ArrayList<C>();
		DEF_COLORS.add(C.c(IntColor.RED, 0));
		DEF_COLORS.add(C.c(IntColor.BLUE, 1));

		DEF_PALETTE_SIZE = 5;
	}

	private List<C>				colors;
	private int					paletteSize;

	public BezierGradientBuilder(double dimY)
	{
		super(dimY);
		this.colors = BezierGradientBuilder.DEF_COLORS;
		this.paletteSize = DEF_PALETTE_SIZE;
	}

	public BezierGradientBuilder colors(List<C> colors)
	{
		if (colors.size() < 2)
			throw new RuntimeException("Pas assez de couleurs!");

		this.colors = colors;
		return this;
	}

	public BezierGradientBuilder paletteSize(int size)
	{
		this.paletteSize = size;
		return this;
	}

	public BezierGradientBuilder beautifulGradient()
	{
		this.deadColor = IntColor.rgb(0, 0, 0);
		this.colors = new ArrayList<C>();
		this.colors.add(C.c(IntColor.rgb(0, 7, 100), 0));
		this.colors.add(C.c(IntColor.rgb(32, 107, 203), 1));
		this.colors.add(C.c(IntColor.rgb(237, 255, 255), 2));
		this.colors.add(C.c(IntColor.rgb(255, 160, 0), 3));
		this.colors.add(C.c(IntColor.rgb(160, 100, 0), 4));
		this.colors.add(C.c(IntColor.rgb(0, 0, 0), 5));
		this.colors.add(C.c(IntColor.rgb(0, 3, 50), 6));
		this.colors.add(C.c(IntColor.rgb(0, 7, 100), 7));

		this.interType = InterpolationType.CYCLIC;
		this.param = 100;

		return this;
	}

	public BezierGradient build()
	{
		return new BezierGradient(this.interType, this.maximumPosition, this.param, this.offset, this.deadColor, this.colors, this.paletteSize);
	}

}
