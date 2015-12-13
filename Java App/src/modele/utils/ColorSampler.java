package modele.utils;

public class ColorSampler {

	private int[]	colorArray;
	private int		position;
	private double	threashold;

	public ColorSampler(int size, double threashold)
	{
		this.colorArray = new int[size];
		this.position = 0;
		this.threashold = threashold;
	}

	public void reset()
	{
		this.position = 0;
	}

	public void add(int c)
	{
		colorArray[position] = c;
		position++;
	}

	public int getAverage()
	{
		double r = 0, g = 0, b = 0;

		for (int i = 0; i < position; i++)
		{
			r += IntColor.red(colorArray[i]);
			g += IntColor.green(colorArray[i]);
			b += IntColor.blue(colorArray[i]);
		}

		return IntColor.rgb((int) (r / (double) (position)), (int) (g / (double) (position)), (int) (b / (double) (position)));
	}

	public boolean addOverThreshold(int c)
	{
		colorArray[position] = c;
		position++;

		return threashold < Math.max(Math.abs(IntColor.red(colorArray[position - 2]) - IntColor.red(c)),
			Math.abs(Math.max(IntColor.blue(colorArray[position - 2]) - IntColor.blue(c), Math.abs(IntColor.green(colorArray[position - 2]) - IntColor.green(c)))));
	}

}
