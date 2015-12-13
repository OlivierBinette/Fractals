package modele.fractal;

import console.Commandable;
import console.implementations.SimpleCommandable;

public abstract class DiscreteFractal implements Fractal 
{

	private Commandable	commandableNode;

	/**
	 * Dimension de la base horizontale du fractale, utilisée pour la construction récursive de
	 * celle-ci.
	 */
	protected final int	baseWidth;

	/**
	 * Dimension de la base verticale du fractale, utilisée pour la construction récursive de
	 * celle-ci.
	 */
	protected final int	baseHeight;

	/**
	 * Constructeur simple d'un DiscreteFractal. Tout DiscreteFractal doit spécifier des dim
	 * 
	 * @param baseWidth
	 * @param baseHeight
	 */
	public DiscreteFractal(int baseWidth, int baseHeight)
	{
		this.baseWidth = validateBase(baseWidth);
		this.baseHeight = validateBase(baseHeight);
		this.commandableNode = new SimpleCommandable("discrete fractal", "fractal");
	}

	/**
	 * Assure que l'argument passé est plus grand ou égal à 1.
	 * 
	 * @param base l'entier à vérifier.
	 * 
	 * @return base, l'entier vérifié s'il est valide et lance une RuntimeException sinon.
	 */
	private static int validateBase(int base)
	{
		if (base < 1)
			throw new RuntimeException("Dimensions de base invalide (plus petit que 1)");

		return base;
	}

	/**
	 * Vérifie si un point quelconque fait partie du fractale.
	 * 
	 * On conceptualise le fractal comme étant une figure infinie dans le premier cadran du plan des
	 * entiers. Il débute à (0,0) et s'étend vers le bas et la droite (les x et y positifs).
	 * 
	 * @param x Coordonnée x dans le plan entier.
	 * @param y Coordonnée y dans le plan entier.
	 * 
	 * @return true si le point (x, y) est dans le fractal, faux sinon.
	 */
	public abstract boolean contains(int x, int y);

	/**
	 * Vérifie si un point quelconque fait partie du fractale.
	 * 
	 * On conceptualise le fractal comme étant une figure infinie dans le premier cadran du plan des
	 * entiers. Il débute à (0,0) et s'étend vers le bas et la droite (les x et y positifs).
	 * 
	 * On considère de plus avec width et height la partie du plan entier dans laquelle les valeurs
	 * de x et y devraient se retrouver. Cela peut être utile pour des conceptualisations quelque
	 * peu différentes du fractale.
	 * 
	 * @param x Coordonnée x dans la fractale.
	 * @param y Coordonnée y dans la fractale.
	 * @param width Largeur de la partie du plan entier considéré.
	 * @param height Hauteur de la partie du plan entier considéré.
	 * @return true si le point (x, y) est dans la fractale, faux sinon.
	 */
	public abstract boolean contains(int x, int y, int width, int height);

	/**
	 * Getter de la dimension horizontale de la base.
	 * 
	 * @return baseWidth la dimension horizontale de la base.
	 */
	public abstract int getBaseWidth();

	/**
	 * Getter de la dimension verticale de la base.
	 * 
	 * @return baseWidth la dimension verticale de la base.
	 */
	public abstract int getBaseHeight();

	@Override
	public abstract DiscreteFractal clone();

	@Override
	public Commandable getCommandableInstance() 
	{
		return this.commandableNode;
	}
}
