package modele.generator;

import java.util.concurrent.Executors;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import modele.fractal.DiscreteFractal;
import modele.navigator.DiscreteFracNav;
import modele.utils.DoublePoint;
import modele.utils.ThreadKiller;

public class DiscreteGenerator extends Generator {

	public DiscreteGenerator(DiscreteFracNav nav, int screenSizeX, int screenSizeY) 
	{
		super(nav, screenSizeX, screenSizeY, 1, 0);
		this.newExecutor();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void render(final int imgWidth, final int imgHeight, final int sample, final int renderginStep, final ThreadKiller threadKiller) 
	{

		final DiscreteFracNav nav = getNav().clone();

		final Task<WritableImage> task = new Task<WritableImage>()
		{
			@Override
			protected WritableImage call() throws Exception
			{
				final WritableImage img = new WritableImage(getComputedImgWidth(), getComputedImgHeight());

				final DiscreteFractal fractal = nav.getFractal();
				final DoublePoint P0 = nav.getP0();

				final double fractalWidth = imgWidth / nav.getWidth();
				final double fractalHeight = imgHeight / nav.getHeight();

				final int nx = (int) Math.ceil(Math.log(fractalWidth) / Math.log(fractal.getBaseWidth()));
				final int ny = (int) Math.ceil(Math.log(fractalHeight) / Math.log(fractal.getBaseHeight()));

				final int n = Math.max(nx, ny);

				final int newFractalWidth = (int) Math.pow(fractal.getBaseWidth(), n);
				final int newFractalHeight = (int) Math.pow(fractal.getBaseHeight(), n);

				final double scalex = newFractalWidth / fractalWidth;
				final double scaley = newFractalHeight / fractalHeight;
				final PixelWriter writer = img.getPixelWriter();
				final int dx = (int) (P0.getX() * newFractalWidth);
				final int dy = (int) (P0.getY() * newFractalHeight);

				for (int j = dy; j < dy + imgHeight * scaley; j += 1)
				{
					for (int i = dx; i < dx + imgWidth * scalex; i += 1)
					{
						if (fractal.contains(i, j, newFractalWidth, newFractalHeight))
						{
							writer.setColor((int) ((i - dx) / scalex), (int) ((j - dy) / scaley), Color.BLUEVIOLET);
						}
					}
					if (threadKiller.isKilled())
						return null;
				}
				return img;
			}
		};

		task.setOnSucceeded(event ->
		{
			setImage(task.getValue());
			setFirstStepDone(true);
			setFinished(true);
		});

		exec.submit(task);
	}

	@Override
	protected void computeImgSizes() 
	{
		final int basex = getNav().getFractal().getBaseWidth();
		final int basey = getNav().getFractal().getBaseHeight();

		final int base = Math.max(basex, basey);

		final double n = Math.log(Math.min(this.screenSizeX, this.screenSizeY)) / Math.log(base);

		this.computedImgWidth = (int) Math.pow(basex, n);
		this.computedImgHeight = (int) Math.pow(basey, n);
	}

	@Override
	public DiscreteFracNav getNav() 
	{
		ensureOnFXThread();
		return (DiscreteFracNav) this.fracNav;
	}

	private static void ensureOnFXThread()
	{
		if (!Platform.isFxApplicationThread())
			throw new RuntimeException("Generator: Pas sur le thread JavaFX!");
	}

	@Override
	public void newExecutor() 
	{
		this.exec = Executors.newCachedThreadPool();
	}

	@Override
	public Generator newGeneratorInstance(int maxWidth, int maxHeight) 
	{
		Generator gen = new DiscreteGenerator(this.getNav().clone(), maxWidth, maxHeight);
		gen.new Updater().sample(3).renderingStep(0).update();
		return gen;
	}

}
