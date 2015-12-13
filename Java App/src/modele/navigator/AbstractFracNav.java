package modele.navigator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import modele.fractal.Fractal;
import modele.utils.DoublePoint;
import console.Command;
import console.Commandable;
import console.CommandableNode;
import console.Definition;
import console.Definition.Type;
import console.Feedback;
import console.NP;
import console.implementations.AbstractCommandable;
import console.implementations.SimpleDefinition;

/**
 * AbstractFracNav devrait contenir tout ce dont Renderer a besoin et tout ce qui est commun à ses
 * sous-classes, sans plus.
 */
public abstract class AbstractFracNav implements CommandableNode 
{

	protected DoublePoint	focalPoint	= null;

	private Commandable		commandableNode;

	/**
	 * Le Fractal contenu dans le AbstractFracNav.
	 */
	protected final Fractal	fractal;

	/**
	 * Le point représentant le coin supérieur gauche de l'écran de navigation dans la fractale.
	 */
	protected DoublePoint	P0;

	/**
	 * Le point représentant le coin inférieur droit de l'écran de navigation dans la fractale.
	 */
	protected DoublePoint	P1;

	/**
	 * Constructeur simple d'AbstractFracNav. Doit nécéssairement recevoir une fractale et les deux
	 * points de l'écran de navigation.
	 * 
	 * @param fractal La fractale.
	 * @param P0 Le point du coin supérieur gauche de l'écran de navigation.
	 * @param P1 Le point du coin inférieur droit de l'écran de navigation.
	 */
	public AbstractFracNav(Fractal fractal, DoublePoint P0, DoublePoint P1) 
	{
		this.fractal = Objects.requireNonNull(fractal);
		this.P0 = Objects.requireNonNull(P0);
		this.P1 = Objects.requireNonNull(P1);
		this.commandableNode = this.new Commander();
	}

	/**
	 * Déplace l'écran de navigation (P0 et P1), de la distance verticale et horinzontale spécifiée.
	 * Cette implémentation n'intègre aucune limite sur les déplacements.
	 * 
	 * @param x Le déplacement horizontal de l'écran de navigation (P0 et P1).
	 * @param y Le déplacement vertical de l'écran de navigatio (P0 et P1).
	 */
	public void translate(double x, double y)
	{
		this.P0 = this.P0.translate(x, y);
		this.P1 = this.P1.translate(x, y);
	}

	/**
	 * Calcule la largeur de l'écran de navigation (la distance horizontale entre P0 et P1).
	 * 
	 * @return La largueur de l'écran de navigation.
	 */
	public double getWidth()
	{
		return (P0.horizontalDistanceTo(P1));
	}

	/**
	 * Calcule la hauteur de l'écran de navigation (la distance verticale entre P0 et P1).
	 * 
	 * @return La hauteur de l'écran de navigation.
	 */
	public double getHeight()
	{
		return (P0.verticalDistanceTo(P1));
	}

	/**
	 * Effectue un « zoom » dans la fractale. Un « zoom in » est un rapetissement de l'écran de
	 * navigation, alors qu'un « zoom out » est son agrandissement.
	 * 
	 * Le module du zoom est la valeur absolue du paramètre scale spécifié.
	 * 
	 * Un paramètre scale positif est un « zoom out » alors qu'un scale négatif est un « zoom in ».
	 * Un paramètre de scale de 0 donne un « zoom » nul.
	 * 
	 * La valeur de scale devrait se situer entre -0.1 et 0.1.
	 * 
	 * On spécifie un point focal dont la position n'est pas modifiée par le zoom.
	 * 
	 * @param scale Le paramètre spécifiant la grandeur et direction du zoom. Positif pour un « zoom
	 *            out » et négatif pour un « zoom in ».
	 * @param pzx La coordonnée x du point focal du zoom.
	 * @param pzy La coordonnée y du point focal du zoom.
	 */
	public abstract void zoom(double scale, double pzx, double pzy);

	/**
	 * Getter du Fractal. Un cast devrait être fait au bon type dans l'implémentation de
	 * l'AbstractFracNav.
	 * 
	 * @return Le Fractal dans le bon type.
	 */
	public abstract Fractal getFractal();

	/**
	 * Getter simple de P0, le point représentant le coin supérieur gauche de l'écran de navigation.
	 * 
	 * @return Le point du coin supérieur gauche de l'écran de navigation.
	 */
	public DoublePoint getP0() 
	{
		return this.P0;
	}

	/**
	 * Getter simple de P1, le point représentant le coin inférieur droit de l'écran de navigation.
	 * 
	 * @return Le point du coin inférieur droit de l'écran de navigation.
	 */
	public DoublePoint getP1() 
	{
		return this.P1;
	}

	/**
	 * Setter simple de P0, le point du coin supérieur gauche de l'écran de navigation. Aucune
	 * vérification n'est fait pour s'assurer que ses coordonnées soient valides.
	 * 
	 * @param p0 le nouveau DoublePoint de P0.
	 */
	public void setP0(DoublePoint p0) 
	{
		this.P0 = p0;
	}

	/**
	 * Setter simple de P1, le point du coin inférieur droit de l'écran de navigation. Aucune
	 * vérification n'est fait pour s'assurer que ses coordonnées soient valides.
	 * 
	 * @param p1 le nouveau DoublePoint de P1.
	 */
	public void setP1(DoublePoint p1) 
	{
		this.P1 = p1;
	}

	public void removeFocalPoint()
	{
		this.focalPoint = null;
	}

	@Override
	public abstract AbstractFracNav clone();

	public static class Action extends SimpleDefinition
	{
		public static final Action	SET		= new Action("set", "change");
		public static final Action	REMOVE	= new Action("remove", "reset");

		private Action(String name, String... synonyms)
		{
			super(Definition.Type.ACTION, name, synonyms);
		}

		public static Definition[] getValues()
		{
			return new Definition[] { SET, REMOVE };
		}

	}

	public static class Field extends SimpleDefinition
	{
		public static final Field	FOCUS	= new Field("focus", "focal point", "zoom point");
		public static final Field	P0		= new Field("P0", "[pP]0", "point zero", "point 0");
		public static final Field	P1		= new Field("P1", "[pP]1", "point one", "point 1");

		private Field(String name, String... synonyms)
		{
			super(Definition.Type.FIELD, name, synonyms);
		}

		public static Definition[] getValues()
		{
			return new Definition[] { FOCUS, P0, P1 };
		}

	}

	public static final Definition	THIS_TARGET_DEF	= new SimpleDefinition(Type.TARGET, "navigator");

	/**
	 * On utilise une classe interne pour, d'une certaine façon, mimer l'héritage multiple. On
	 * aurait pu aussi implémenter Commandable directement sur Generator, mais alors on aurait du
	 * aussi tout recopier l'implémentation plutot que d'utiliser ce qui est déjà fait dans
	 * AbstractCommandable. (On n'extend pas une implémentation de Commandable puisque c'est une
	 * fonctionnalité secondaire qu'on veut pouvoir ajouter trop changer quoi que ce soit.)
	 */
	public class Commander extends AbstractCommandable
	{

		public Commander() 
		{
			super(THIS_TARGET_DEF);
			this.add(getFractal().getCommandableInstance());
		}

		@Override
		protected Feedback tryToExecuteLocally(Command command, Feedback onFail) 
		{
			boolean executed = executeAsDoer(command);

			return executed ? Feedback.EXECUTED : onFail;
		}

		private boolean executeAsDoer(Command c)
		{
			Pattern p;
			Matcher m;

			List<Definition> actions = new ArrayList<Definition>();
			for (Definition def : Action.getValues())
			{
				p = Pattern.compile(def.getSynonymRegex());
				m = p.matcher(c.getInstruction());
				if (m.find())
					actions.add(def);
			}

			List<Definition> fields = new ArrayList<Definition>();
			for (Definition def : Field.getValues())
			{
				p = Pattern.compile(def.getSynonymRegex());
				m = p.matcher(c.getInstruction());
				if (m.find())
					fields.add(def);
			}

			for (Definition def : actions)
			{
				if (def == Action.SET)
				{
					return setField(c, fields);
				}

				if (def == Action.REMOVE)
				{
					if (fields.stream().anyMatch((Definition defin) -> {
						return defin != null && defin == Field.FOCUS;
					}))
					{
						focalPoint = null;
						return true;
					}

				}
			}

			return false;
		}

		private boolean setField(Command c, List<Definition> fields)
		{
			for (Definition def : fields)
			{
				if (def == Field.FOCUS)
					return this.setFocus(c.getInstruction());
				else if (def == Field.P0)
					return this.setP0(c.getInstruction());
				else if (def == Field.P1)
					return this.setP1(c.getInstruction());
			}

			return false;
		}

		private boolean setP0(String s)
		{
			Pattern coord = Pattern.compile(NP.COORDINATES);
			Matcher m = coord.matcher(s);
			if (m.find())
			{
				P0 = new DoublePoint(NP.getAsDouble(m.group("c1")), NP.getAsDouble(m.group("c2")));
				return true;
			}

			return false;
		}

		private boolean setP1(String s)
		{
			Pattern coord = Pattern.compile(NP.COORDINATES);
			Matcher m = coord.matcher(s);
			if (m.find())
			{
				P1 = new DoublePoint(NP.getAsDouble(m.group("c1")), NP.getAsDouble(m.group("c2")));
				return true;
			}

			return false;
		}

		private boolean setFocus(String s)
		{
			Pattern p = Pattern.compile("here|this position");
			Matcher m = p.matcher(s);
			if (m.find())
			{
				focalPoint = P0.midPoint(P1);
				return true;
			}

			Pattern coordRegex = Pattern.compile(NP.COORDINATES);
			m = coordRegex.matcher(s);
			if (m.find())
			{
				focalPoint = new DoublePoint(NP.getAsDouble(m.group("c1")), NP.getAsDouble(m.group("c2")));
				return true;
			}

			return false;
		}
	}

	public Commandable getCommandableInstance() 
	{
		return this.commandableNode;
	}

}
