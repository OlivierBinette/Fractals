package modele.gradients;

public class Vect 
{

	public double	x;
	public double	y;

	public Vect()
	{
		this.x = 0;
		this.y = 0;
	}

	public Vect(Vect v)
	{
		this.x = v.x;
		this.y = v.y;
	}

	public Vect(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	public Vect plus(Vect v)
	{
		return new Vect(x + v.x, y + v.y);
	}

	public Vect minus(Vect v)
	{
		return new Vect(x - v.x, y - v.y);
	}

	public double length()
	{
		return Math.sqrt(x * x + y * y);
	}

	public Vect normalized()
	{
		double l = this.length();
		return new Vect(this.x / l, this.y / l);
	}

	public Vect over(double d)
	{
		return new Vect(this.x / d, this.y / d);
	}

	public Vect times(double m)
	{
		return new Vect(this.x * m, this.y * m);
	}

	public Vect putYInBound(double min, double max)
	{
		if (y < min)
			y = min;
		else if (y > max)
			y = max;

		return this;
	}

	public String toString()
	{
		return "[" + x + " " + y + "]";
	}
}
