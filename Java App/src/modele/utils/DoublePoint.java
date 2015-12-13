package modele.utils;

public class DoublePoint 
{

	private double	x;
	private double	y;

	public DoublePoint(double x, double y) 
	{
		this.x = x;
		this.y = y;
	}

	/**
	 * ATTENTION: Ne modifie pas cet objet.
	 * 
	 * @return un DoublePoint, le résultat de cette opération.
	 */
	public DoublePoint translate(double x, double y)
	{
		return new DoublePoint(this.x + x, this.y + y);
	}

	/**
	 * ATTENTION: Ne modifie pas cet objet.
	 * 
	 * @return un DoublePoint, le résultat de cette opération.
	 */
	public DoublePoint scale(double x, double y)
	{
		return new DoublePoint(this.x * x, this.y * y);
	}

	/** ATTENTION: Ne modifie pas cet objet. */
	public DoublePoint midPoint(DoublePoint p)
	{
		return new DoublePoint((p.getX() + this.x) / 2.0, (p.getY() + this.y) / 2.0);
	}

	/** ATTENTION: Ne modifie pas cet objet. */
	public DoublePoint fractionalPoint(DoublePoint p, double scale)
	{
		return new DoublePoint((p.getX() - this.x) * scale + this.x, (p.getY() - this.y) * scale + this.y);
	}

	public boolean inBounds(DoublePoint p0, DoublePoint p1)
	{
		return this.inBounds(p0.getX(), p0.getY(), p1.getX(), p1.getY());
	}

	public boolean inBounds(double xmin, double ymin, double xmax, double ymax)
	{
		return this.x >= xmin && this.x <= xmax && this.y >= ymin && this.y <= ymax;
	}

	public double distanceTo(DoublePoint p)
	{
		double x = this.horizontalDistanceTo(p);
		double y = this.verticalDistanceTo(p);
		return Math.sqrt(x * x + y * y);
	}

	public double horizontalDistanceTo(DoublePoint p)
	{
		return p.x - this.x;
	}

	public double verticalDistanceTo(DoublePoint p)
	{
		return p.y - this.y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public void set(DoublePoint p)
	{
		this.x = p.getX();
		this.y = p.getY();
	}

	public String toString()
	{
		return "[" + x + " " + y + "]";
	}

	@Override
	public DoublePoint clone() {
		return new DoublePoint(this.x, this.y);
	}
}
