package vue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Application;
import javafx.beans.Observable;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import modele.fractal.continuous.Mandelbrot;
import modele.fractal.continuous.Mandelbrot.Field;
import modele.generator.ContinuousGenerator;
import modele.generator.Generator;
import modele.gradients.AbstractGradient.InterpolationType;
import modele.gradients.ColorGradient;
import modele.navigator.ContFracNav;
import console.Command;
import console.Commandable;
import console.CommandableNode;
import console.Definition;
import console.Definition.Type;
import console.Feedback;
import console.NP;
import console.Parser;
import console.implementations.AbstractCommandable;
import console.implementations.SimpleCommander;
import console.implementations.SimpleDefinition;
import console.implementations.SimpleParser;

public class App extends Application implements CommandableNode {

	private Commandable	commandableNode;

	private Generator	generator;

	private Stage		stage;
	private StackPane	root;
	private ImageView	imgView;
	private Overlay		overlay;

	private double		mouseX;
	private double		mouseY;

	@Override
	public void start(final Stage primaryStage) throws Exception {
		this.stage = primaryStage;
		primaryStage.setMinWidth(300);
		primaryStage.setMinHeight(300);

		imgView = new ImageView();
		overlay = new Overlay();

		root = new StackPane();
		root.getChildren().add(imgView);
		root.getChildren().add(overlay);

		primaryStage.setScene(new Scene(root));
		primaryStage.setFullScreen(true);

		initGenerator();

		primaryStage.show();

		this.initListeners();
	}

	private void initGenerator()
	{
		if (stage.isFullScreen())
		{
			generator = new ContinuousGenerator(
				new ContFracNav(
					new Mandelbrot.Builder().maxItr(500).colorGradient(
						new ColorGradient.Builder(500).beautifulGradient().interpolType(InterpolationType.CYCLIC).build()
					).build()
				),
				(int) Screen.getPrimary().getBounds().getWidth(),
				(int) Screen.getPrimary().getBounds().getHeight()
				);
		}
		else
		{
			generator = new ContinuousGenerator(
				new ContFracNav(
					new Mandelbrot.Builder().maxItr(500).colorGradient(
						new ColorGradient.Builder(500).beautifulGradient().interpolType(InterpolationType.CYCLIC).build()
					).build()
				),
				(int) stage.getWidth(),
				(int) stage.getHeight()
				);
		}
		generator.new Updater().renderingStep(4).sample(3).update();

		this.imgView.setImage(generator.getImage());
		generator.setOnImageChange(() -> imgView.setImage(generator.getImage()));

		generator.start();

		initCommander();
	}

	private void initCommander()
	{
		this.commandableNode = this.new Commander();

		final SimpleCommander com = new SimpleCommander();
		com.add(this.getCommandableInstance());

		final Parser parser = new SimpleParser(com.getDictionary());

		overlay.setOnReturnKeyPressed(() -> {
			if (com.send(parser.parse(overlay.getText())))
				generator.restart();
		});
	}

	private void initListeners()
	{
		root.setOnScroll((ScrollEvent e) -> {
			double posX = generator.getNav().getP0().getX() + ((e.getX() / (double) generator.getComputedImgWidth()) * generator.getNav().getWidth());
			double posY = generator.getNav().getP0().getY() + ((e.getY() / (double) generator.getComputedImgHeight()) * generator.getNav().getHeight());
			// Zoom in
			if (e.getDeltaY() > 0)
			{
				if (generator.firstStepDone())
				{
					generator.getNav().zoom(scrollNorm(-0.005 * e.getDeltaY()), posX, posY);
					generator.restart();
				}
			}
			// Zoom out
			else if (generator.firstStepDone())
			{
				generator.getNav().zoom(scrollNorm(-0.005 * e.getDeltaY()), posX, posY);
				generator.restart();
			}
		});

		root.setOnMouseDragged(e -> {
			final double x = e.getX();
			final double y = e.getY();

			final double x1 = ((mouseX - x) / (double) generator.getComputedImgWidth()) * generator.getNav().getWidth();
			final double y1 = ((mouseY - y) / (double) generator.getComputedImgHeight()) * generator.getNav().getHeight();

			generator.getNav().translate(x1, y1);

			if (generator.firstStepDone())
				generator.restart();

			mouseX = x;
			mouseY = y;
		});

		root.setOnMouseMoved((e) -> {
			mouseX = e.getX();
			mouseY = e.getY();
		});

		stage.fullScreenProperty().addListener((a, b, newValue) -> {
			if (newValue.booleanValue() == true)
			{
				generator.new Updater().screenSize(
					(int) Screen.getPrimary().getBounds().getWidth(),
					(int) Screen.getPrimary().getBounds().getHeight()
					).update();
				generator.restart();
			}
		});

		stage.widthProperty().addListener((Observable o) -> {
			generator.new Updater().screenSize((int) stage.getWidth(), (int) stage.getHeight()).update();
			generator.restart();
		});

		root.heightProperty().addListener((Observable o) -> {
			generator.new Updater().screenSize((int) stage.getWidth(), (int) stage.getHeight()).update();
			generator.restart();
		});
	}

	@Override
	public void stop()
	{
		this.generator.stop();
	}

	private void export(final int dimX, final int dimY)
	{
		FileChooser dc = new FileChooser();
		dc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
		File f = dc.showSaveDialog(this.stage);
		if (f != null)
			generator.generateAsPNG(f, dimX, dimY).addListener((a, b, c) -> {
			});
	}

	private static double scrollNorm(double x)
	{
		return (2 * Math.atan(x)) / Math.PI;
	}

	public static class Action extends SimpleDefinition
	{
		public static final Action	EXPORT	= new Action("export", "save");
		public static final Action	RESET	= new Action("reset");

		private Action(String name, String... synonyms)
		{
			super(Definition.Type.ACTION, name, synonyms);
		}

		public static Definition[] getValues()
		{
			return new Definition[] { EXPORT, RESET };
		}
	}

	public static final Definition	THIS_TARGET_DEF	= new SimpleDefinition(Type.TARGET, "app", "application");

	public class Commander extends AbstractCommandable
	{
		public Commander() {
			super(THIS_TARGET_DEF);
			this.dictionary.addAll(Action.getValues());
			Objects.requireNonNull(generator);
			this.add(generator.getCommandableInstance());
		}

		@Override
		protected Feedback tryToExecuteLocally(Command command, Feedback onFail) {
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

			p = Pattern.compile(NP.IMAGE_SIZES);
			for (Definition def : actions)
			{
				if (def == Action.EXPORT)
				{
					m = p.matcher(c.getInstruction());
					if (m.find())
					{
						export(NP.getAsInt(m.group("c1")), NP.getAsInt(m.group("c2")));
						return true;
					}
					return false;
				}
				else if (def == Action.RESET)
				{
					initGenerator();
				}
			}

			return false;
		}
	}

	@Override
	public Commandable getCommandableInstance() {
		return this.commandableNode;
	}

	public static void main(String[] args) {
		launch(args);
	}

}
