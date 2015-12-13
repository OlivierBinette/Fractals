package modele.gradients;

import java.util.List;

import modele.utils.IntColor;
import modele.utils.Vect;

/**
 * On ajoute une layer statique par dessus ColorGradient pour créer (une seule fois, à la
 * construction) l'approximation de l'interpolation.
 */
public class BezierGradient extends ColorGradient 
{

	public BezierGradient(InterpolationType interType, double maxPosition, double var, double offset, int deadColor, List<C> colorPoints, int nbrPointsApproximation)
	{
		super(interType, maxPosition, var, offset, deadColor, makeColorArray(colorPoints, nbrPointsApproximation, interType));
	}

	public static class Builder extends BezierGradientBuilder
	{
		public Builder(double dimY) {
			super(dimY);
		}
	}

	private static int[] makeColorArray(List<C> colorPoints, int nbrPointsApproximation, InterpolationType interType)
	{
		if (colorPoints.size() < 2)
			throw new RuntimeException("La liste de couleurs doit contenir au moins 2 couleurs.");

		if (colorPoints.size() == 2)
		{
			int[] colors = new int[2];
			colors[0] = colorPoints.get(0).getColor();
			colors[1] = colorPoints.get(1).getColor();
			return colors;
		}

		return makeBezierApproximation(colorPoints, nbrPointsApproximation, interType);
	}

	private static int[] makeBezierApproximation(List<C> colorPoints, int nbrPointsApproximation, InterpolationType interType)
	{
		int[] bezierApproxColors = new int[nbrPointsApproximation * colorPoints.size() * 3];

		colorPoints.sort(null);
		double maxPos = colorPoints.get(colorPoints.size() - 1).getPosition();
		double distance = maxPos / (double) (nbrPointsApproximation * colorPoints.size());

		double factor = 0.49;
		Vect p0, p1, d0, d1, p2, p3;

		int maxArrayPos = 0;
		for (int colorNo = 0; colorNo < 3; colorNo++)
		{
			int arrayPos = 0;

			p0 = makeVector(colorPoints.get(colorPoints.size() - 2), colorNo).minus(new Vect(maxPos, 0));
			p1 = makeVector(colorPoints.get(0), colorNo);
			p2 = makeVector(colorPoints.get(1), colorNo);
			p3 = makeVector(colorPoints.get(2), colorNo);

			if (interType == InterpolationType.CYCLIC)
				d0 = p2.minus(p1).plus(p1.minus(p0)).normalized().times(p2.minus(p1).length() * factor).plus(p1);
			else
				d0 = p2.minus(p1).times(factor).plus(p1);

			d1 = p2.minus(p3).plus(p1.minus(p2)).normalized().times(p2.minus(p1).length() * factor).plus(p2);

			double length = p2.x - p1.x;
			double off = (distance - (p1.x % distance)) / length;
			int nbrPoints = (int) (p2.x / distance) - (int) (p1.x / distance);
			for (int x = 0; x < nbrPoints; x++)
			{
				double p = off + x / (double) nbrPoints;
				double k = Math.pow(1 - p, 3) * p1.y + 3 * p * Math.pow(1 - p, 2) * d0.y + 3 * (1 - p) * Math.pow(p, 2) * d1.y + Math.pow(p, 3) * p2.y;
				try 
				{
					bezierApproxColors[(arrayPos) * 3 + colorNo] = between0And256((int)k);
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
				arrayPos++;
			}

			for (int i = 1; i < colorPoints.size() - 2; i++)
			{
				p0 = p1;
				p1 = p2;
				p2 = p3;
				p3 = makeVector(colorPoints.get(i + 2), colorNo);

				// d0 =
				// p2.minus(p1).normalized().plus(p1.minus(p0).normalized()).times(p2.minus(p1).length()*factor/2.0).plus(p1).putYInBound(0.0,
				// 0.99);
				d0 = d1.minus(p1).times(-1).plus(p1);
				d1 = p2.minus(p3).plus(p1.minus(p2)).normalized().times(p2.minus(p1).length() * factor).plus(p2);

				length = p2.x - p1.x;
				off = (distance - (p1.x % distance)) / length;
				nbrPoints = (int) (p2.x / distance) - (int) (p1.x / distance);
				for (int x = 0; x < nbrPoints; x++)
				{
					double p = off + x / (double) nbrPoints;
					double k = Math.pow(1 - p, 3) * p1.y + 3 * p * Math.pow(1 - p, 2) * d0.y + 3 * (1 - p) * Math.pow(p, 2) * d1.y + Math.pow(p, 3) * p2.y;
					try 
					{
						bezierApproxColors[(arrayPos) * 3 + colorNo] = between0And256((int)k);
					}
					catch (Exception e) 
					{
						e.printStackTrace();
					}
					arrayPos++;
				}
			}

			p0 = p1;
			p1 = p2;
			p2 = p3;
			p3 = makeVector(colorPoints.get(1), colorNo).plus(new Vect(maxPos, 0));

			// d0 =
			// p2.minus(p1).normalized().plus(p1.minus(p0).normalized()).times(p2.minus(p1).length()*factor/2.0).plus(p1).putYInBound(0.0,
			// 1.0);
			d0 = d1.minus(p1).times(-1).plus(p1);
			if (interType == InterpolationType.CYCLIC)
				d1 = p2.minus(p3).plus(p1.minus(p2)).normalized().times(p2.minus(p1).length() * factor).plus(p2);
			else
				d1 = p1.minus(p2).times(factor).plus(p2);

			length = p2.x - p1.x;
			off = (distance - (p1.x % distance)) / length;
			nbrPoints = (int) (p2.x / distance) - (int) (p1.x / distance);
			for (int x = 0; x < nbrPoints; x++)
			{
				double p = off + x / (double) nbrPoints;
				double k = Math.pow(1 - p, 3) * p1.y + 3 * p * Math.pow(1 - p, 2) * d0.y + 3 * (1 - p) * Math.pow(p, 2) * d1.y + Math.pow(p, 3) * p2.y;
				try 
				{
					bezierApproxColors[(arrayPos) * 3 + colorNo] = between0And256((int)k);
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
				arrayPos++;
			}

			maxArrayPos = Math.max(maxArrayPos, arrayPos - 1);
		}

		// On a parfois les trois dernières cases de l'array non assignées (propagation de l'erreur
		// double).
		if (maxArrayPos < bezierApproxColors.length - 1)
		{
			int[] newArray = new int[(maxArrayPos + 1) * 3];
			System.arraycopy(bezierApproxColors, 0, newArray, 0, (maxArrayPos + 1) * 3);
			bezierApproxColors = newArray;
		}

		return makeColorArray(bezierApproxColors);
	}

	private static int between0And256(int pos)
	{
		if (pos < 0)
			return 0;
		if (pos > 255)
			return 255;

		return pos;
	}

	private static int[] makeColorArray(int[] colors)
	{
		int[] colorArray = new int[colors.length / 3];

		for (int i = 0; i < colors.length; i += 3)
			colorArray[i / 3] = IntColor.rgb(colors[i], colors[i + 1], colors[i + 2]);

		return colorArray;
	}

	private static Vect makeVector(C colorPoint, int colorNo)
	{
		if (colorNo < 0 || colorNo > 2)
			throw new RuntimeException("Le no. de couleur n'est pas valide");

		if (colorNo == 0)
			return new Vect(colorPoint.getPosition(), IntColor.red(colorPoint.getColor()));
		else if (colorNo == 1)
			return new Vect(colorPoint.getPosition(), IntColor.green(colorPoint.getColor()));
		else
			return new Vect(colorPoint.getPosition(), IntColor.blue(colorPoint.getColor()));
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
}
