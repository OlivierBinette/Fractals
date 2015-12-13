package modele.fractal.continuous;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import modele.fractal.ContinuousFractal;
import modele.gradients.BezierGradient;
import modele.gradients.Gradient;
import modele.utils.DoublePoint;
import console.Command;
import console.Commandable;
import console.Definition;
import console.Definition.Type;
import console.Feedback;
import console.NP;
import console.implementations.AbstractCommandable;
import console.implementations.SimpleDefinition;
import dynamic.Calculator;
import dynamic.CalculatorUtil;

public class Mandelbrot extends ContinuousFractal 
{

	private Commandable	commandableNode;

	/**
	 * Le gradient de couleur utilisé pour colorer la fractale.
	 */
	private Gradient	colorGrad;

	/**
	 * Le nombre d'itérations maximale utilisé pour déterminer si un point est dans l'ensemble de
	 * Mandelbrot ou non.
	 */
	private int			maxIteration;

	/**
	 * La distance à laquelle un point doit se retrouver pour être considéré hors de l'ensemble de
	 * Mandelbrot et arrêter d'itérer la formule.
	 */
	private double		escapeRadius;

	private Calculator	calculZRe;

	private Calculator	calculZImg;

	private Mandelbrot(Gradient colorGrad, int maxItr, double radius, DoublePoint upLeftBound, DoublePoint downRightBound, Calculator Zre, Calculator Zimg)
	{
		super(upLeftBound, downRightBound);
		this.colorGrad = colorGrad;
		this.maxIteration = maxItr;
		this.escapeRadius = radius;
		this.commandableNode = this.new Commander();
		this.calculZRe = Zre;
		this.calculZImg = Zimg;
	}

	public boolean inCardioid(final double x, final double y)
	{
		double t = Math.atan((x - 0.25) / y) - Math.PI / 2.0;

		double rn = Math.sqrt((x - 0.25) * (x - 0.25) + y * y) * (y > 0 ? 1 : -1);
		double r1 = -(1 + Math.cos(t)) / 2.0;
		double r2 = (1 - Math.cos(t)) / 2.0;

		if (rn >= r1 && rn <= r2)
		{
			return true;
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getColor(final double x, final double y)
	{
		if (inCardioid(x, y))
			return this.colorGrad.interpolate(this.maxIteration);

		double iteration = 0;

		double zRe = 0, zImg = 0, temp;

		while (((zRe * zRe + zImg * zImg) < escapeRadius) && (iteration < this.maxIteration))
		{
			temp = calculZRe.calculate(zRe, zImg) + x;
			zImg = calculZImg.calculate(zRe, zImg) + y;
			zRe = temp;

			iteration++;
		}

		// On ajuste la couleur pour un gradient continu. Formule de wikipédia.
		if (iteration < this.maxIteration)
		{
			double z = (zRe * zRe) + (zImg * zImg);
			double n = Math.log((0.5 * Math.log(z)) / Math.log(2)) / Math.log(2);
			iteration = (iteration + 1) - n;
		}

		return this.colorGrad.interpolate(iteration);
	}

	@Override
	public ContinuousFractal clone() 
	{
		return new Mandelbrot.Builder(this).build();
	}

	public Gradient getColorGradient()
	{
		return this.colorGrad;
	}

	public int getMaxIterations()
	{
		return this.maxIteration;
	}

	public double getEscapeRadius()
	{
		return this.escapeRadius;
	}

	public Calculator getcalculZre()
	{
		return calculZRe;
	}

	public Calculator getcalculZimg()
	{
		return calculZImg;
	}

	@Override
	public double getBaseWidth() 
	{
		return this.getUpLeftBound().horizontalDistanceTo(this.getDownRightBound());
	}

	@Override
	public double getBaseHeight() 
	{
		return this.getUpLeftBound().verticalDistanceTo(this.getDownRightBound());
	}

	private void setMaxItr(int max)
	{
		this.maxIteration = max;
		this.colorGrad.setMaxPosition(max);
	}

	/**
	 * Classe constructrice de Mandelbrot.
	 */
	public static class Builder
	{
		public static DoublePoint		DEF_UP_LEFT_BOUND;
		public static DoublePoint		DEF_DOWN_RIGHT_BOUND;
		public static int				DEF_MAX_ITR;
		public static long				DEF_RADIUS;
		public static Gradient			DEF_COLOR_GRADIENT;
		public static final Calculator	DEF_CALC_RE		= CalculatorUtil.DEFAULT_CALC_RE;
		public static final Calculator	DEF_CALC_IMG	= CalculatorUtil.DEFAULT_CALC_IMG;

		static
		{
			DEF_UP_LEFT_BOUND = new DoublePoint(-2.2, -1.2);
			DEF_DOWN_RIGHT_BOUND = new DoublePoint(1.0, 1.2);
			DEF_MAX_ITR = 2000;
			DEF_RADIUS = 1 << 30;
			DEF_COLOR_GRADIENT = new BezierGradient.Builder(DEF_MAX_ITR).beautifulGradient().build();
		}

		private DoublePoint				upLeftBound;
		private DoublePoint				downRightBound;
		private Gradient				colorGrad;
		private int						maxIteration;
		private double					escapeRadius;
		private Calculator				ZRe;
		private Calculator				ZImg;

		public Builder()
		{
			this.upLeftBound = Builder.DEF_UP_LEFT_BOUND;
			this.downRightBound = Builder.DEF_DOWN_RIGHT_BOUND;
			this.maxIteration = Builder.DEF_MAX_ITR;
			this.escapeRadius = Builder.DEF_RADIUS;
			this.colorGrad = Builder.DEF_COLOR_GRADIENT;
			this.ZRe = Builder.DEF_CALC_RE;
			this.ZImg = Builder.DEF_CALC_IMG;
		}

		public Builder(Mandelbrot mandel)
		{
			this.upLeftBound = mandel.getUpLeftBound();
			this.downRightBound = mandel.getDownRightBound();
			this.maxIteration = mandel.getMaxIterations();
			this.escapeRadius = mandel.getEscapeRadius();
			this.colorGrad = mandel.getColorGradient().newInstance();
			this.ZRe = mandel.getcalculZre();
			this.ZImg = mandel.getcalculZimg();
		}

		public Builder upLeftBound(DoublePoint bound)
		{
			this.upLeftBound = bound;
			return this;
		}

		public Builder downRightBound(DoublePoint bound)
		{
			this.downRightBound = bound;
			return this;
		}

		public Builder maxItr(int max)
		{
			this.maxIteration = max;
			return this;
		}

		public Builder escapeRadius(double radius)
		{
			this.escapeRadius = radius;
			return this;
		}

		public Builder calcRe(Calculator re)
		{
			this.ZRe = re;
			return this;
		}

		public Builder calcImg(Calculator img)
		{
			this.ZImg = img;
			return this;
		}

		public Builder colorGradient(Gradient colors)
		{
			this.colorGrad = colors;
			return this;
		}

		public Mandelbrot build()
		{
			return new Mandelbrot(this.colorGrad, this.maxIteration, this.escapeRadius,
				this.upLeftBound, this.downRightBound, this.ZRe, this.ZImg);
		}
	}

	/* ****************************************************************
	 * Fonctionnalité Commandable *************************************************************** */

	public static class Action extends SimpleDefinition
	{
		public static final Action	SET			= new Action("set", "change");
		public static final Action	INCREASE	= new Action("increase", "incre(a)?ment");
		public static final Action	DECREASE	= new Action("decrease", "decre(a)?ment");

		private Action(String name, String... synonyms)
		{
			super(Definition.Type.ACTION, name, synonyms);
		}

		public static Definition[] getValues()
		{
			return new Definition[] { SET, INCREASE, DECREASE };
		}

	}

	public static class Field extends SimpleDefinition
	{
		public static final Field	MAX_ITERATION		= new Field("max iteration", "max((imal)|(imum))? iteration(s)?", "iteration(s)?");
		public static final Field	ESCAPE_RADIUS		= new Field("escape radius", "radi((i)|(us))?");
		public static final Field	UP_LEFT_BOUND		= new Field("up left bound", "up bound", "left bound");
		public static final Field	DOWN_RIGHT_BOUND	= new Field("down right bound", "down bound", "right bound");

		private Field(String name, String... synonyms)
		{
			super(Definition.Type.FIELD, name, synonyms);
		}

		public static Definition[] getValues()
		{
			return new Definition[] { MAX_ITERATION, ESCAPE_RADIUS, UP_LEFT_BOUND, DOWN_RIGHT_BOUND };
		}

	}

	public static final Definition	THIS_TARGET_DEF	= new SimpleDefinition(Type.TARGET, "[Mm]andelbrot", "fractal(s)?");

	public class Commander extends AbstractCommandable
	{

		public Commander() {
			super(THIS_TARGET_DEF);
			this.add(Objects.requireNonNull(colorGrad.getCommandableInstance()));
		}

		@Override
		protected Feedback tryToExecuteLocally(Command command, Feedback onFail) {
			boolean executed = this.executeAsDoer(command);

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
					return setField(c, fields);
				else if (def == Action.INCREASE)
					return this.increaseField(c, fields);
				else if (def == Action.DECREASE)
					return this.decreaseField(c, fields);
			}

			return setField(c, fields);
		}

		private boolean setField(Command c, List<Definition> fields)
		{
			for (Definition def : fields)
			{
				if (def == Field.MAX_ITERATION)
					return this.setMaxIteration(c.getInstruction());
				else if (def == Field.ESCAPE_RADIUS)
					return this.setEscapeRadius(c.getInstruction());
				else if (def == Field.UP_LEFT_BOUND)
					return this.setUpLeftBound(c.getInstruction());
				else if (def == Field.DOWN_RIGHT_BOUND)
					return this.setDownRightBound(c.getInstruction());
			}

			return false;
		}

		private boolean increaseField(Command c, List<Definition> fields)
		{
			for (Definition def : fields)
			{
				if (def == Field.MAX_ITERATION)
					return this.increaseIterations(c.getInstruction());
				else if (def == Field.ESCAPE_RADIUS)
					return this.increaseEscapeRadius(c.getInstruction());
			}

			return false;
		}

		private boolean decreaseField(Command c, List<Definition> fields)
		{
			for (Definition def : fields)
			{
				if (def == Field.MAX_ITERATION)
					return this.decreaseMaxIteration(c.getInstruction());
				else if (def == Field.ESCAPE_RADIUS)
					return this.decreaseEscapeRadius(c.getInstruction());
			}

			return false;
		}

		private boolean setMaxIteration(String s)
		{
			Pattern posIntRegex = Pattern.compile(NP.POS_INTEGER);
			Matcher m = posIntRegex.matcher(s);
			if (m.find())
			{
				setMaxItr(NP.getAsInt(m.group()));
				return true;
			}

			return false;
		}

		private boolean setEscapeRadius(String s)
		{
			Pattern posIntRegex = Pattern.compile(NP.POS_DOUBLE);
			Matcher m = posIntRegex.matcher(s);
			if (m.find())
			{
				escapeRadius = NP.getAsDouble(m.group());
				return true;
			}

			return false;
		}

		private boolean setUpLeftBound(String s)
		{
			Pattern bound = Pattern.compile(NP.COORDINATES);
			Matcher m = bound.matcher(s);
			if (m.find())
			{
				upLeftBound = new DoublePoint(NP.getAsDouble(m.group("c1")), -NP.getAsDouble(m.group("c2")));
				return true;
			}

			return false;
		}

		private boolean setDownRightBound(String s)
		{
			Pattern bound = Pattern.compile(NP.COORDINATES);
			Matcher m = bound.matcher(s);
			if (m.find())
			{
				downRightBound = new DoublePoint(NP.getAsDouble(m.group("c1")), -NP.getAsDouble(m.group("c2")));
				return true;
			}

			return false;
		}

		private boolean increaseIterations(String s)
		{
			Pattern posIntRegex = Pattern.compile(NP.POS_INTEGER);
			Pattern percentageRegex = Pattern.compile(NP.PERCENTAGE);
			double percent;

			Matcher m = percentageRegex.matcher(s);
			if (m.find())
			{
				percent = NP.getAsDouble(m.group("c1"));
				setMaxItr(maxIteration + (int) ((maxIteration * percent) / 100));
				return true;
			}

			m = posIntRegex.matcher(s);
			if (m.find())
			{
				setMaxItr(maxIteration + NP.getAsInt(m.group()));
				return true;
			}

			return false;
		}

		private boolean decreaseMaxIteration(String s)
		{
			Pattern posIntRegex = Pattern.compile(NP.POS_INTEGER);
			Pattern percentageRegex = Pattern.compile(NP.PERCENTAGE);
			double percent;
			int val;

			Matcher m = percentageRegex.matcher(s);
			if (m.find())
			{
				percent = NP.getAsDouble(m.group("c1"));
				val = maxIteration - (int) ((maxIteration * percent) / 100);
				setMaxItr(val >= 1 ? val : 1);
				return true;
			}

			m = posIntRegex.matcher(s);
			if (m.find())
			{
				val = maxIteration - NP.getAsInt(m.group());
				setMaxItr(val >= 1 ? val : 1);
				return true;
			}

			return false;
		}

		private boolean increaseEscapeRadius(String s)
		{
			Pattern posIntRegex = Pattern.compile(NP.POS_DOUBLE);
			Pattern percentageRegex = Pattern.compile(NP.PERCENTAGE);
			double percent;

			Matcher m = percentageRegex.matcher(s);
			if (m.find())
			{
				percent = NP.getAsDouble(m.group("c1"));
				escapeRadius = (escapeRadius + (int) ((escapeRadius * percent) / 100));
				return true;
			}

			m = posIntRegex.matcher(s);
			if (m.find())
			{
				escapeRadius = escapeRadius + NP.getAsDouble(m.group());
				return true;
			}

			return false;
		}

		private boolean decreaseEscapeRadius(String s)
		{
			Pattern posIntRegex = Pattern.compile(NP.POS_DOUBLE);
			Pattern percentageRegex = Pattern.compile(NP.PERCENTAGE);
			double percent;
			double val;

			Matcher m = percentageRegex.matcher(s);
			if (m.find())
			{
				percent = NP.getAsDouble(m.group("c1"));
				val = escapeRadius - ((escapeRadius * percent) / 100);
				escapeRadius = (val >= 1 ? val : 1);
				return true;
			}

			m = posIntRegex.matcher(s);
			if (m.find())
			{
				val = escapeRadius - NP.getAsDouble(m.group());
				escapeRadius = (val >= 1 ? val : 1);
				return true;
			}

			return false;
		}

	}

	@Override
	public Commandable getCommandableInstance() {
		return this.commandableNode;
	}
}
