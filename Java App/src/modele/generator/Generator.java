package modele.generator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;

import modele.navigator.AbstractFracNav;
import modele.utils.Controller;
import modele.utils.Function;
import modele.utils.ThreadKiller;
import console.Command;
import console.Commandable;
import console.CommandableNode;
import console.Definition;
import console.Definition.Type;
import console.Feedback;
import console.NP;
import console.implementations.AbstractCommandable;
import console.implementations.SimpleDefinition;

public abstract class Generator implements CommandableNode 
{

	/**
	 * Instance de Commandable associée à cet objet.
	 */
	private Commandable			commandableInstance;

	/**
	 * Éxécuteur des tâches de génération.
	 */
	protected ExecutorService	exec;

	/**
	 * Référence à l'image dessinée par le Generator.
	 */
	private WritableImage		image;
	/**
	 * Représente la réalisation de la première étape de génération.
	 */
	private boolean				firstStepDone;
	/**
	 * Représente l'état de la génération.
	 */
	private boolean				finished;
	/**
	 * Passe à vrai lorsque les dimensions de l'image ont été changées par setScreenSizes(...), et
	 * retombe à faux lorsque l'attribut {@code image} a été modifié pour les prendre en compte.
	 */
	private boolean				imageSizesChanged;

	/**
	 * Executée lorsque l'attribut image est réassignée.
	 */
	private Function			onImageChange	= () -> {};

	/**
	 * Executée lorsque l'attribut firstStepDone passe à vrai. Toutes les implémentations intègrent
	 * cette fonctionnalité, mais dans les cas les plus simple, elle concorde avec onFinished.
	 */
	private Function			onFirstStepDone	= () -> {};

	/**
	 * Executée lorsque l'attribut finished passe à vrai. Toutes les implémentations intègrent cette
	 * fonctionnalité.
	 */
	private Function			onFinished		= () -> {};

	/**
	 * Executée lorsque les paramètres sample, renderingStep, maxImgSize ou fracNav sont changés.
	 */
	private Function			onChange		= () -> {};

	/**
	 * Le navigateur de fractale, contenant la fractale, qui est utilisé pour la génération.
	 */
	protected AbstractFracNav	fracNav;

	/**
	 * La largeur du « frame » entourant l'image.
	 */
	protected int				screenSizeX;
	/**
	 * La hauteur du « frame » entourant l'image.
	 */
	protected int				screenSizeY;

	/**
	 * La largeur de l'image, calculée selon la limité spécifiée par screenSizeX et les proportions
	 * de l'image.
	 */
	protected int				computedImgWidth;
	/**
	 * La hauteur de l'image, calculée selon la limité spécifiée par screenSizeY et les proportions
	 * de l'image.
	 */
	protected int				computedImgHeight;

	/**
	 * Le nombre de points à calculer par pixel.
	 */
	private int					sample			= 1;
	/**
	 * Le nombre d'étapes de génération supplémentaires à la génération de base.
	 */
	private int					renderingStep;

	/**
	 * Classe particulière utilisée exclusivement pour arrêter des Threads pouvant rouler en
	 * parallèle.
	 * 
	 * On passe aux Threads des copies de la référence {@code threadKiller}, lesquelles ne seront
	 * pas réassignées même si {@code threadKiller} est ici réassigné. Ça permet de s'assurer
	 */
	protected ThreadKiller		threadKiller;

	/**
	 * Permet de déterminer si tous les threads en parallèle ont terminés leur première étape de
	 * génération.
	 */
	protected Controller		firstStepController;
	/**
	 * Permet de déterminer si tous les threads en parallèle ont terminés leur partie de génération.
	 */
	protected Controller		finishedController;

	/**
	 * Constructeur de Generator.
	 * 
	 * @param nav le navigateur de fractal à utiliser pour la génération.
	 * @param screenSizeX la largeur du « frame » circonscrivant la dimension de l'image.
	 * @param screenSizeY la hauteur du « frame » circonscrivant la dimension de l'image.
	 * @param sample le nombre de points à calculer par pixel.
	 * @param steps le nombre d'étapes de générations supplémentaires à faire, en plus de l'étape de
	 *            base.
	 */
	public Generator(AbstractFracNav nav, int screenSizeX, int screenSizeY, int sample, int steps)
	{
		constructorInit();
		this.setFinished(false);
		this.setFirstStepDone(false);
		this.fracNav = nav;
		this.setScreeSizes(screenSizeX, screenSizeY);
		this.setImage(new WritableImage(this.computedImgWidth, this.computedImgHeight));
		this.setSample(sample);
		this.renderingStep = steps;
		this.commandableInstance = this.new Commander();
		this.imageSizesChanged = false;
	}

	protected void constructorInit() {};

	/**
	 * Crée une copie de ce generator, en changeant les dimensions du « frame » circonscrivant les
	 * dimensions de l'image.
	 * 
	 * @param maxWidth la largeur du « frame » circonscrivant les dimensions de l'image.
	 * @param maxHeight la hauteur du « frame » circonscrivant les dimensions de l'image.
	 * @return le nouveau Generator.
	 */
	public abstract Generator newGeneratorInstance(int maxWidth, int maxHeight);

	/**
	 * Lance la routine de génération dans l'implémentation du Generator.
	 * 
	 * @param imgWidth la largeur de l'image à générer.
	 * @param imgHeight la hauteur de l'image à générer.
	 * @param sample le nombre de points à calculer par pixel.
	 * @param renderginStep le nombre d'étapes de générations supplémentaires à la génération de
	 *            base à effectuer.
	 * @param threadKiller le ThreadKiller passé aux Threads.
	 */
	protected abstract void render(final int imgWidth, final int imgHeight, final int sample, final int renderginStep, final ThreadKiller threadKiller);

	/**
	 * Demande à l'implémentation du generator de créer un nouveau Executeur pour les Threads.
	 */
	public abstract void newExecutor();

	/**
	 * Génère une image en réassignant l'attribut {@code image} ou en déssinant sur celle-ci. Si
	 * {@code image} est réassignée, la Func spécifiée par setOnImageChange est exécutée.
	 */
	public void generate()
	{
		if (this.imageSizesChanged)
			this.setImage(new WritableImage(this.computedImgWidth, this.computedImgHeight));
		this.imageSizesChanged = false;

		threadKiller = new ThreadKiller();
		this.finished = false;
		this.firstStepDone = false;

		this.render(this.computedImgWidth, this.computedImgHeight, this.sample, this.renderingStep, this.threadKiller);
	}

	/**
	 * Démarre la génération d'une image png basée sur le Generator actuel. Un BooleanProperty est
	 * crée, permettant de savoir quand la génération termine.
	 * 
	 * @param f Le fichier dans lequel écrire l'image.
	 * @param maxWidth la largeur maximale de l'image.
	 * @param maxHeight la hauteur maximale de l'image.
	 * @return un BooleanProperty qui prend la valeur true lorsque la génération termine.
	 */
	public BooleanProperty generateAsPNG(final File f, int maxWidth, int maxHeight)
	{
		BooleanProperty finishedProperty = new SimpleBooleanProperty(false);
		final Generator gen = this.newGeneratorInstance(maxWidth, maxHeight);
		gen.setOnFinished(() -> {
			try 
			{
				ImageIO.write(SwingFXUtils.fromFXImage(gen.getImage(), null), "png", f);
				gen.stop();
				finishedProperty.set(true);
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				gen.stop();
				finishedProperty.set(true);
			}
		});

		gen.start();

		return finishedProperty;
	}

	/**
	 * Démarre le générateur de fractales.
	 */
	public void start()
	{
		this.generate();
		this.onImageChange.execute();
	}

	/**
	 * Redémarre le générateur de fratales.
	 * 
	 * Si le générateur n'a pas terminé la dernière génération, on tente de l'arrêter puis on lance
	 * une nouvelle génération.
	 */
	public void restart()
	{
		this.cancel();
		this.generate();
	}

	/**
	 * Tente d'arrêter les Threads occupés à la génération.
	 */
	public void cancel()
	{
		threadKiller.killAll();
	}

	/**
	 * Ferme le générateur de fractales.
	 * 
	 * Cette méthode devrait être appellée à la fermeture de l'application.
	 */
	public void stop()
	{
		this.cancel();
		if (this.exec != null)
			exec.shutdown();
	}

	/**
	 * Called when the screenSizes are changed through the protected setter.
	 */
	protected abstract void computeImgSizes();

	/**
	 * Change les dimensions du « frame » circonscrivant les dimensions de l'image. Les nouvelles
	 * dimensions de l'image sont calculées à partir de ces valeurs et l'{@code image} sera modifiée
	 * à la prochaine génération.
	 * 
	 * @param sizeX la largeur
	 * @param sizeY
	 */
	protected void setScreeSizes(int sizeX, int sizeY)
	{
		this.screenSizeX = sizeX;
		this.screenSizeY = sizeY;
		this.computeImgSizes();
		this.imageSizesChanged = true;
	}

	/**
	 * Change {@code image} et exécute la fonction onImageChange.
	 * 
	 * @param img la nouvelle Image.
	 */
	protected void setImage(WritableImage img)
	{
		this.image = img;
		this.onImageChange.execute();
	}

	protected void setFinished(boolean value)
	{
		this.finished = value;
		if (value == true)
			this.onFinished.execute();
	}

	protected void setFirstStepDone(boolean value)
	{
		this.firstStepDone = value;
		if (value == true)
			this.onFirstStepDone.execute();
	}

	public boolean allFinished()
	{
		return this.finished;
	}

	public boolean firstStepDone()
	{
		return this.firstStepDone;
	}

	/**
	 * Spécifie la fonction à exécuter lorsque l'{@code image} accessible par getImage() est
	 * réassignée.
	 * 
	 * @param function le code à exécuter.
	 */
	public void setOnImageChange(Function function)
	{
		this.onImageChange = function;
	}

	/**
	 * Spécifie la fonction à exécuter lorsque le {@code Generator} termine la génératino de
	 * l'image. Toutes les implémentations intègrent cette fonctionnalité.
	 * 
	 * @param function le code à exécuter.
	 */
	public void setOnFinished(Function function)
	{
		this.onFinished = function;
	}

	/**
	 * Spécifie la fonction à exécuter lorsque le {@code Generator} termine la première étape de
	 * génération de l'image.
	 * 
	 * Les implémentations sont libres d'interpréter ce qui constitue cette première étape.
	 * 
	 * Dans les cas les plus simple, cela peut coincider avec onFinished (donc se méfier du
	 * dédoublement de code!).
	 * 
	 * @param function le code à exécuter.
	 */
	public void setOnFirstStepDone(Function function)
	{
		this.onFirstStepDone = function;
	}

	/**
	 * Spécifie la fonction à exécuter lorsque les paramètres de génération sont modifiés. Ceux-ci
	 * sont accessible en utilisant Updater.
	 * 
	 * @param function le code à exécuter.
	 */
	public void setOnChange(Function function)
	{
		this.onChange = function;
	}

	public WritableImage getImage()
	{
		return this.image;
	}

	public abstract AbstractFracNav getNav();

	/**
	 * Retourne la largeur du « frame » circonscrivant les dimensions de l'image.
	 * 
	 * @return la largeur du « frame ».
	 */
	public int getScreenSizeX() 
	{
		return this.screenSizeX;
	}

	/**
	 * Retourne la hauteur du « frame » circonscrivant les dimensions de l'image.
	 * 
	 * @return la hauteur du « frame ».
	 */
	public int getScreenSizeY() 
	{
		return this.screenSizeY;
	}

	/**
	 * Retourne la largeur de l'image, calculée à partir du paramètre de la dimension maximale de
	 * l'image. Ce paramètre est accessible à partir de Updater.
	 * 
	 * @return la largeur de l'image.
	 */
	public int getComputedImgWidth() 
	{
		return computedImgWidth;
	}

	/**
	 * Retourne la hauteur de l'image, calculée à partir du paramètre de la dimension maximale de
	 * l'image. Ce paramètre est accessible à partir de Updater.
	 * 
	 * @return la hauteur de l'image.
	 */
	public int getComputedImgHeight() 
	{
		return computedImgHeight;
	}

	/**
	 * Retourne le paramètre d'échantillonage. Ce paramètre spécifie le nombre de points
	 * d'échantillonages à prendre en x et en y, pour chaque pixel.
	 * 
	 * Une valeur de 2 permet d'avoir une bonne qualité d'image, mais demande 4 fois plus de temps
	 * de génération.
	 * 
	 * La paramètre peut être modifié avec Updater.
	 * 
	 * @return le paramètre d'échantillonage.
	 */
	public int getSample() 
	{
		return sample;
	}

	public void setSample(int val)
	{
		if (val <= 0)
		{
			this.sample = 1;
			System.out.println("Sample doit être plus grand ou égal à 1.");
		}
		else
		{
			this.sample = val;
		}
	}

	/**
	 * Retourne le paramètre du nombre d'étapes de générations. 0 signifie une seule étape.
	 * 
	 * Les implémentations sont libres d'intégrer cette fonctionnalité ou non.
	 * 
	 * Ce paramètre peut être modifié avec Updater.
	 * 
	 * @return le paramètre de génération par étape.
	 */
	public int getRenderingStep() 
	{
		return renderingStep;
	}

	/**
	 * {@inheritDoc}
	 */
	public Commandable getCommandableInstance()
	{
		return this.commandableInstance;
	}

	/**
	 * <p>
	 * On utilise une classe interne pour, d'une certaine façon, atomiser le changement des
	 * attributs de classe. Ça permet aussi d'appeller seulement une fois onChange.execute() lorsque
	 * plusieurs attributs sont changés. Ça permet de plus facilement gérer les problèmes de
	 * conccurence, si par exemple plusieurs Threads souhaiteraient modifier les attributs de cet
	 * objet en même temps (ce n'est pas le cas présentement, mais ça pourrait l'être si Commander
	 * roulait sur un Thread). Finalement, c'est plus facile à utiliser et j'aime beaucoup pouvoir
	 * faire plusieurs opérations sur une même ligne :
	 * </p>
	 * 
	 * <pre>
	 * <code>
	 * gen.new Updater().sample(3).renderingStep(4).maxImgSize(2000).update(); //wow!
	 * </code>
	 * </pre>
	 */
	public class Updater
	{
		private int				newScreenSizeX;
		private int				newScreenSizeY;
		private int				newSample;
		private int				newRenderingStep;
		private AbstractFracNav	newFracNav;

		public Updater()
		{
			this.newScreenSizeX = getScreenSizeX();
			this.newScreenSizeY = getScreenSizeY();
			this.newSample = getSample();
			this.newRenderingStep = getRenderingStep();
			this.newFracNav = getNav();
		}

		public Updater screenSize(int sizeX, int sizeY)
		{
			this.newScreenSizeX = sizeX;
			this.newScreenSizeY = sizeY;
			return this;
		}

		public Updater sample(int val)
		{
			this.newSample = val;
			return this;
		}

		public Updater renderingStep(int step)
		{
			this.newRenderingStep = step;
			return this;
		}

		public Updater fracNav(AbstractFracNav nav)
		{
			this.newFracNav = nav;
			return this;
		}

		public void update()
		{
			setScreeSizes(this.newScreenSizeX, this.newScreenSizeY);
			setSample(this.newSample);
			renderingStep = this.newRenderingStep;
			fracNav = this.newFracNav;
			onChange.execute();
		}
	}

	public static class Action extends SimpleDefinition
	{
		public static final Action	RESTART		= new Action("restart", "refresh");
		public static final Action	SET			= new Action("set", "change");
		public static final Action	INCREASE	= new Action("increase", "incre(a)?ment");
		public static final Action	DECREASE	= new Action("decrease", "decre(a)?ment");

		private Action(String name, String... synonyms)
		{
			super(Definition.Type.ACTION, name, synonyms);
		}

		public static Definition[] getValues()
		{
			return new Definition[] { RESTART, SET, INCREASE, DECREASE };
		}

	}

	public static class Field extends SimpleDefinition
	{
		public static final Field	SAMPLE	= new Field("sample", "(sample)[s]?", "(oversample)[s]?", "(oversampling)");
		public static final Field	STEPS	= new Field("steps", "step(s)?", "(render)(ing)? step(s)?", "render step(s)?");

		private Field(String name, String... synonyms)
		{
			super(Definition.Type.FIELD, name, synonyms);
		}

		public static Definition[] getValues()
		{
			return new Definition[] { SAMPLE, STEPS };
		}
	}

	public static final Definition	THIS_TARGET_DEF	= new SimpleDefinition(Type.TARGET, "generator", "renderer");

	/**
	 * On utilise une classe interne pour, d'une certaine façon, mimer l'héritage multiple. On
	 * aurait pu aussi implémenter Commandable directement sur Generator, mais alors on aurait du
	 * aussi tout recopier l'implémentation plutot que d'utiliser ce qui est déjà fait dans
	 * AbstractCommandable.
	 */
	public class Commander extends AbstractCommandable
	{

		public Commander() {
			super(THIS_TARGET_DEF);
			Objects.requireNonNull(fracNav);
			this.add(fracNav.getCommandableInstance());
		}

		@Override
		protected Feedback tryToExecuteLocally(Command command, Feedback onFail) 
		{
			boolean executed;

			executed = executeAsDoer(command);

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
				if (def == Action.RESTART)
				{
					restart();
					return true;
				}
				else if (def == Action.SET)
				{
					return setField(c, fields);
				}
				else if (def == Action.INCREASE)
				{
					return this.increaseField(c, fields);
				}
				else if (def == Action.DECREASE)
				{
					return this.decreaseField(c, fields);
				}
			}

			return setField(c, fields);
		}

		private boolean setField(Command c, List<Definition> fields)
		{
			for (Definition def : fields)
			{
				if (def == Field.SAMPLE)
				{
					return setSample(c.getInstruction());
				}
				else if (def == Field.STEPS)
				{
					return this.setSteps(c.getInstruction());
				}
			}

			return false;
		}

		private boolean increaseField(Command c, List<Definition> fields)
		{
			for (Definition def : fields)
			{
				if (def == Field.SAMPLE)
				{
					return increaseSample(c.getInstruction());
				}
				else if (def == Field.STEPS)
				{
					return increaseSteps(c.getInstruction());
				}
			}

			return false;
		}

		private boolean decreaseField(Command c, List<Definition> fields)
		{
			for (Definition def : fields)
			{
				if (def == Field.SAMPLE)
				{
					return decreaseSample(c.getInstruction());
				}
				else if (def == Field.STEPS)
				{
					return decreaseSteps(c.getInstruction());
				}
			}

			return false;
		}

		private boolean increaseSample(String s)
		{
			Pattern posIntRegex = Pattern.compile(NP.INTEGER);
			Pattern percentageRegex = Pattern.compile(NP.PERCENTAGE);
			double percent;

			Matcher m = percentageRegex.matcher(s);
			if (m.find())
			{
				percent = NP.getAsDouble(m.group("c1"));
				Generator.this.setSample(sample + (int) (sample * percent / 100));
				return true;
			}

			m = posIntRegex.matcher(s);
			if (m.find())
			{
				Generator.this.setSample(sample + NP.getAsInt(m.group()));
				return true;
			}

			return false;
		}

		private boolean increaseSteps(String s)
		{
			Pattern percentageRegex = Pattern.compile(NP.PERCENTAGE);
			double percent;
			Pattern posIntRegex = Pattern.compile(NP.POS_INTEGER);

			Matcher m = percentageRegex.matcher(s);
			if (m.find())
			{
				percent = NP.getAsDouble(m.group("c1"));
				renderingStep += (int) (renderingStep * percent / 100);
				return true;
			}

			m = posIntRegex.matcher(s);
			if (m.find())
			{
				renderingStep += NP.getAsInt(m.group());
				return true;
			}

			return false;
		}

		private boolean decreaseSample(String s)
		{
			Pattern percentageRegex = Pattern.compile(NP.PERCENTAGE);
			double percent;
			int val;
			Pattern posIntRegex = Pattern.compile(NP.INTEGER);

			Matcher m = percentageRegex.matcher(s);
			if (m.find())
			{
				percent = NP.getAsDouble(m.group("c1"));
				val = sample - (int) (sample * percent / 100);
				Generator.this.setSample(val >= 1 ? val : 1);
				return true;
			}

			m = posIntRegex.matcher(s);
			if (m.find())
			{
				val = sample - NP.getAsInt(m.group());
				Generator.this.setSample(val >= 1 ? val : 1);
				return true;
			}

			return false;
		}

		private boolean decreaseSteps(String s)
		{
			Pattern percentageRegex = Pattern.compile(NP.PERCENTAGE);
			double percent;
			int val;
			Pattern posIntRegex = Pattern.compile(NP.POS_INTEGER);

			Matcher m = percentageRegex.matcher(s);
			if (m.find())
			{
				percent = NP.getAsDouble(m.group("c1"));
				val = renderingStep - (int) (renderingStep * percent / 100);
				renderingStep = (val >= 0 ? val : 0);
				return true;
			}

			m = posIntRegex.matcher(s);
			if (m.find())
			{
				val = renderingStep - NP.getAsInt(m.group());
				renderingStep = val >= 0 ? val : 0;
				return true;
			}

			return false;
		}

		private boolean setSample(String s)
		{
			Pattern posIntRegex = Pattern.compile(NP.INTEGER);

			Matcher m = posIntRegex.matcher(s);
			if (m.find())
			{
				Generator.this.setSample(NP.getAsInt(m.group()));
				return true;
			}

			return false;
		}

		private boolean setSteps(String s)
		{
			Pattern posIntRegex = Pattern.compile(NP.POS_INTEGER);

			Matcher m = posIntRegex.matcher(s);
			if (m.find())
			{
				renderingStep = NP.getAsInt(m.group());
				return true;
			}

			return false;
		}
	}

}
