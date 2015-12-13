package modele.utils;

public class P4D implements Comparable<P4D> 
{

	public int[]	colors;
	public double	x;

	public P4D(int c0, int c1, int c2, double x)
	{
		this.colors = new int[3];
		this.colors[0] = c0;
		this.colors[1] = c1;
		this.colors[2] = c2;
		this.x = x;
	}

	public String toString()
	{
		return colors[0] + " " + colors[1] + " " + colors[2] + " " + x;
	}

	@Override
	public int compareTo(P4D p) 
	{
		if (this.x < p.x)
			return -1;

		if (this.x == p.x)
			return 0;

		return 1;
	}
}
