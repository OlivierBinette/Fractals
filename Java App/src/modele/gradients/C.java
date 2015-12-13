package modele.gradients;


public class C implements Comparable<C> 
{

	private final int		color;
	private final double	position;

	private C(int c, double pos)
	{
		this.color = c;
		this.position = pos;
	}

	public int getColor()
	{
		return this.color;
	}

	public double getPosition()
	{
		return this.position;
	}

	@Override
	public int compareTo(C o) 
	{
		return this.getPosition() < o.getPosition() ? -1 : (this.getPosition() == o.getPosition() ? 0 : 1);
	}

	public static C c(int c, double pos)
	{
		return new C(c, pos);
	}
}
