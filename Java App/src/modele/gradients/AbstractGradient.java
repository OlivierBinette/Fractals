package modele.gradients;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import modele.utils.IntColor;
import console.Command;
import console.Commandable;
import console.Definition;
import console.Definition.Type;
import console.Feedback;
import console.NP;
import console.implementations.AbstractCommandable;
import console.implementations.SimpleDefinition;

public abstract class AbstractGradient implements Gradient 
{

	private Commandable	commandableNode;

	public enum ColorType 
	{
		RGB, HSB
	}

	public enum InterpolationType 
	{
		CYCLIC, LINEAR
	}

	protected InterpolationType	interpolationType;
	protected double			param;
	protected double			offset;
	protected double			maximumPosition;
	protected int				deadColor;

	protected AbstractGradient(InterpolationType interpolationType, double maxPosition, double var, double offset, int deadColor)
	{
		this.commandableNode = this.new Commander();

		this.interpolationType = interpolationType;
		this.maximumPosition = maxPosition;
		this.param = var;
		this.deadColor = deadColor;
		this.offset = offset;
	}

	public synchronized void setOffset(double offset)
	{
		this.offset = (offset % this.maximumPosition);
	}

	public synchronized void setParam(double param)
	{
		if (param <= 0)
		{
			this.param = 100;
			System.out.println("Le paramètre d'étalement de couleur doit être plus grand que 0.");
		}
		else
			this.param = param;
	}

	@Override
	public synchronized void setMaxPosition(double max)
	{
		this.maximumPosition = max;
	}

	/* ********************************************************************************
	 * Fonctionnalité Commandable
	 * ******************************************************************************* */
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
		public static final Field	PARAM				= new Field("param", "parameter", "bleeding", "[ée]talement");
		public static final Field	OFFSET				= new Field("offset", "offset(s)?");
		public static final Field	COLOR_TYPE			= new Field("color type");
		public static final Field	INTERPOLATION_TYPE	= new Field("interpolation type", "interpolation");
		public static final Field	DEAD_COLOR			= new Field("dead color", "end color");

		private Field(String name, String... synonyms)
		{
			super(Definition.Type.FIELD, name, synonyms);
		}

		public static Definition[] getValues()
		{
			return new Definition[] { PARAM, OFFSET, COLOR_TYPE, INTERPOLATION_TYPE, DEAD_COLOR };
		}
	}

	public static final Definition	THIS_TARGET_DEF	= new SimpleDefinition(Type.TARGET, "mandelbrot", "fractal(?:s)?");

	public class Commander extends AbstractCommandable
	{
		public Commander() 
		{
			super(THIS_TARGET_DEF);
		}

		@Override
		protected Feedback tryToExecuteLocally(Command command, Feedback onFail) 
		{
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
				{
					return this.setField(c, fields);
				}
				if (def == Action.INCREASE)
				{
					return this.increaseField(c, fields);
				}
				if (def == Action.DECREASE)
				{
					return this.decreaseField(c, fields);
				}
			}

			return this.setField(c, fields);
		}

		private boolean setField(Command c, List<Definition> fields)
		{
			for (Definition def : fields)
			{
				if (def == Field.PARAM)
				{
					return setParam(c.getInstruction());
				}
				else if (def == Field.OFFSET)
				{
					return setOffset(c.getInstruction());
				}
				else if (def == Field.INTERPOLATION_TYPE)
				{
					return setInterpolationType(c.getInstruction());
				}
				else if (def == Field.DEAD_COLOR)
				{
					return setDeadColor(c.getInstruction());
				}
			}

			return false;
		}

		private boolean increaseField(Command c, List<Definition> fields)
		{
			for (Definition def : fields)
			{
				if (def == Field.OFFSET)
				{
					return this.increaseOffset(c.getInstruction());
				}
				else if (def == Field.PARAM)
				{
					return this.increaseParam(c.getInstruction());
				}
			}

			return false;
		}

		private boolean decreaseField(Command c, List<Definition> fields)
		{
			for (Definition def : fields)
			{
				if (def == Field.OFFSET)
				{
					return this.decreaseOffset(c.getInstruction());
				}
				else if (def == Field.PARAM)
				{
					return this.decreaseParam(c.getInstruction());
				}
			}

			return false;
		}

		private boolean increaseOffset(String s)
		{
			Pattern percentageRegex = Pattern.compile(NP.PERCENTAGE);
			Pattern doubleRegex = Pattern.compile(NP.DOUBLE);
			Matcher m = percentageRegex.matcher(s);
			if (m.find())
			{
				offset += offset * NP.getAsDouble(m.group("c1")) / 100.0;
				return true;
			}

			m = doubleRegex.matcher(s);
			if (m.find())
			{
				offset += NP.getAsDouble(m.group());
				return true;
			}

			return false;
		}

		private boolean increaseParam(String s)
		{
			Pattern percentageRegex = Pattern.compile(NP.PERCENTAGE);
			Pattern posDoubleRegex = Pattern.compile(NP.POS_DOUBLE);

			Matcher m = percentageRegex.matcher(s);
			if (m.find())
			{
				AbstractGradient.this.setParam(param + param * NP.getAsDouble(m.group()) / 100.0);
				return true;
			}

			m = posDoubleRegex.matcher(s);
			if (m.find())
			{
				AbstractGradient.this.setParam(param + NP.getAsDouble(m.group()));
				return true;
			}

			return false;
		}

		private boolean decreaseOffset(String s)
		{
			Pattern percentageRegex = Pattern.compile(NP.PERCENTAGE);
			Pattern doubleRegex = Pattern.compile(NP.DOUBLE);
			Matcher m;

			m = percentageRegex.matcher(s);
			if (m.find())
			{
				offset -= offset * NP.getAsDouble(m.group("c1")) / 100.0;
				return true;
			}

			m = doubleRegex.matcher(s);
			if (m.find())
			{
				offset -= NP.getAsDouble(m.group());
				return true;
			}

			return false;

		}

		private boolean decreaseParam(String s)
		{
			Pattern percentageRegex = Pattern.compile(NP.PERCENTAGE);
			double percent;
			double val;
			Pattern posDoubleRegex = Pattern.compile(NP.POS_DOUBLE);
			Matcher m;

			m = percentageRegex.matcher(s);
			if (m.find())
			{
				percent = NP.getAsDouble(m.group("c1"));
				val = param - (param * percent / 100.0);
				AbstractGradient.this.setParam(val >= 1 ? val : 1);
				return true;
			}

			m = posDoubleRegex.matcher(s);
			if (m.find())
			{
				val = param - NP.getAsDouble(m.group());
				AbstractGradient.this.setParam(val >= 1 ? val : 1);
				return true;
			}

			return false;
		}

		private boolean setParam(String s)
		{
			Pattern posDoubleRegex = Pattern.compile(NP.DOUBLE);
			Matcher m = posDoubleRegex.matcher(s);
			if (m.find())
			{
				AbstractGradient.this.setParam(NP.getAsDouble(m.group()));
				return true;
			}

			return false;
		}

		private boolean setOffset(String s)
		{
			Pattern doubleRegex = Pattern.compile(NP.DOUBLE);
			Matcher m = doubleRegex.matcher(s);
			if (m.find())
			{
				offset = NP.getAsDouble(m.group());
				return true;
			}

			return false;
		}

		private boolean setInterpolationType(String s)
		{
			Pattern interCyclicRegex = Pattern.compile("\\b(?:cyclic)\\b");
			Pattern interLinearRegex = Pattern.compile("\\b(?:linear)\\b");

			Matcher m = interCyclicRegex.matcher(s);
			if (m.find())
			{
				interpolationType = InterpolationType.CYCLIC;
				return true;
			}

			m = interLinearRegex.matcher(s);
			if (m.find())
			{
				interpolationType = InterpolationType.LINEAR;
				return true;
			}

			return false;
		}

		private boolean setDeadColor(String s)
		{
			Pattern RGBColor = Pattern.compile(NP.COLOR_VAL_RGB);
			Pattern HBSColor = Pattern.compile(NP.COLOR_VAL_HBS);
			Matcher m = RGBColor.matcher(s);
			if (m.find())
			{
				deadColor = IntColor.rgb(NP.getAsInt(m.group("c1")), NP.getAsInt(m.group("c2")), NP.getAsInt(m.group("c3")));
				return true;
			}

			m = HBSColor.matcher(s);
			if (m.find())
			{
				deadColor = IntColor.HSBtoColor(NP.getAsInt(m.group("c1")), NP.getAsInt(m.group("c2")), NP.getAsInt(m.group("c3")));
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
