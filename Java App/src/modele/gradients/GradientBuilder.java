package modele.gradients;

import modele.gradients.AbstractGradient.InterpolationType;
import modele.utils.IntColor;

public abstract class GradientBuilder<T extends GradientBuilder<T>> 
{

	public static final int					DEF_DEAD_COLOR;
	public static final InterpolationType	DEF_INTER_TYPE;
	public static final double				DEF_PARAM;
	public static final double				DEF_OFFSET;

	static 
	{
		DEF_DEAD_COLOR = IntColor.rgb(0, 0, 0);
		DEF_INTER_TYPE = InterpolationType.CYCLIC;
		DEF_PARAM = 100.0;
		DEF_OFFSET = 1.0;
	}

	protected int							deadColor;
	protected InterpolationType				interType;
	protected double						param;
	protected double						offset;
	protected double						maximumPosition;

	protected GradientBuilder(double dimY)
	{
		this.deadColor = DEF_DEAD_COLOR;
		this.interType = DEF_INTER_TYPE;
		this.param = DEF_PARAM;
		this.offset = DEF_OFFSET;
		this.maximumPosition = dimY;
	}

	@SuppressWarnings("unchecked")
	public T deadColor(int c)
	{
		this.deadColor = c;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T interpolType(InterpolationType type)
	{
		this.interType = type;
		return (T) this;
	}
	
	@SuppressWarnings("unchecked")
	public T param(double param)
	{
		this.param = param;
		return (T) this;
	}
	
	@SuppressWarnings("unchecked")
	public T offset(double offset)
	{
		this.offset = offset;
		return (T) this;
	}
}
