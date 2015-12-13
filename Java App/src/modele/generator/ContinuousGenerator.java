package modele.generator;

import java.nio.IntBuffer;
import java.util.concurrent.Executors;

import javafx.application.Platform;
import javafx.scene.image.PixelFormat;
import modele.fractal.ContinuousFractal;
import modele.navigator.ContFracNav;
import modele.utils.ColorSampler;
import modele.utils.Controller;
import modele.utils.DoublePoint;
import modele.utils.Function;
import modele.utils.ThreadKiller;

public class ContinuousGenerator extends Generator 
{

	/**
	 * Le nombre de processeurs utilisés.
	 */
	private int		nbrProcessors;

	private double	varx;
	private double	vary;
	private double	oldWidth;
	private double	oldHeight;

	public ContinuousGenerator(ContFracNav nav, int screenSizeX, int screenSizeY) 
	{
		super(nav, screenSizeX, screenSizeY, 2, 4);
		this.findNbrProcessors();
		this.newExecutor();
	}

	@Override
	protected void constructorInit()
	{
		varx = 0;
		vary = 0;
		oldWidth = 1.0;
		oldHeight = 1.0;
	}

	@Override
	protected void render(final int imgWidth, final int imgHeight, final int sample, final int renderingStep, final ThreadKiller killer)
	{
		final int div = this.nbrProcessors;

		this.firstStepController = new Controller(div);
		this.firstStepController.setOnFinish(new Function() 
		{
			public void execute() 
			{
				setFirstStepDone(true);
			}
		});

		this.finishedController = new Controller(div);
		this.finishedController.setOnFinish(new Function() 
		{
			public void execute() 
			{
				setFinished(true);
			}
		});

		final ContFracNav nav = getNav().clone();

		for (int x = 0; x < div; x++)
		{

			final Runnable task = createTask(x, div, nav, imgWidth, imgHeight, sample, renderingStep, killer, this.firstStepController, this.finishedController);

			exec.submit(task);
		}
	}

	private Runnable createTask(
		final int pos, // Note : l'utilisation de final n'est plus nécéssaire avec Java 8 (puisque
						// effectivement final), mais bon...
		final int div,
		final ContFracNav nav,
		final int imgWidth,
		final int imgHeight,
		final int sample,
		final int renderingStep,
		final ThreadKiller killer,
		final Controller firstRendering,
		final Controller finishedRendering)
	{
		Runnable task = new Runnable()
		{
			public void run()
			{
				try 
				{
					final int samplex = sample;
					final int sampley = sample;

					final double deltax = nav.getWidth() / (double) imgWidth;
					final double deltay = nav.getHeight() / (double) imgHeight;
					final double p0x = nav.getP0().getX();
					final double p0y = nav.getP0().getY();
					final double fracWidth = nav.getWidth() / (double) div;
					final int remainder;

					if (pos == div - 1)
						remainder = imgWidth % div;
					else
						remainder = 0;

					final int width = imgWidth / div + remainder;

					final int[] buffer = new int[width * imgHeight];
					int c;
					final ColorSampler ave = new ColorSampler(samplex * sampley, 0.05);
					double x, y;

					boolean renderedOnce = false;
					int index;
					final int step = (int) Math.pow(2, renderingStep);
					// Étape de génération.
					long time = System.nanoTime();
					long times = 0;
					for (int var = step; var > 0; var /= 2)
					{
						// Sauts en x et y selon l'étape de génération.
						for (int ky = 0; ky < step; ky += var)
						{
							for (int kx = 0; kx < step; kx += var)
							{
								// On s'assure de ne jamais repasser sur un point déjà calculé.
								// On est sur que ça fonctionne puisque chaque pair (kx, ky) est
								// atteinte (on le voit à l'affichage) et on rentre autant de fois
								// ici qu'il y a de pixels à
								// dessiner dans un des carrés (testé).
								// (kx & ~ (-2*var)) != 0 <=> kx % (2*var) != 0.
								if (kx % (2 * var) != 0 || ky % (2 * var) != 0 || var == step)
								{
									// Boucle principale.
									for (int j = ky; j < imgHeight; j += step)
									{
										for (int i = kx; i < width; i += step)
										{
											x = i * deltax + pos * fracWidth + p0x;
											y = j * deltay + p0y;

											// Oversampling.

											ave.add(nav.getFractal().getColor(x, y));
											if (sample > 1 && ave.addOverThreshold(nav.getFractal().getColor(x + deltax * (samplex - 1) / (double) samplex,
												y + deltay * (sampley - 1) / (double) sampley)))
											{
												for (int p = 1; p < samplex * sampley - 1; p++)
												{
													ave.add(nav.getFractal().getColor(x + deltax * (p % samplex) / (double) samplex, y + deltay * (p / samplex) / (double) sampley));
												}
											}
											c = ave.getAverage();
											ave.reset();

											// Dessin des carrés.
											for (int py = j; py < j + var && py < imgHeight; py++)
											{
												for (int px = i; px < i + var && px < width; px++)
												{
													index = width * py + px;
													buffer[index] = c;
												}
											}
										}
										if (killer.isKilled())
											return;
									}
								}
							}
						}
						if (!renderedOnce)
						{
							firstRendering.finished();
							renderedOnce = true;
						}
						writeImgBuffer(killer, pos, imgWidth / div, width, imgHeight, imgWidth, buffer);
					}
					System.out.println(times);
					finishedRendering.finished();
					System.out.println(Thread.currentThread().getName() + " : " + (System.nanoTime() - time) / (double) 1000000 + " ms");
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		};

		return task;
	}

	/**
	 * Dessine l'array byte[] passé en paramètre sur l'Image, à partir du Thread JavaFx.
	 */
	private void writeImgBuffer(ThreadKiller killer, int pos, int smallWidth, int realWidth, int imgHeight, int imgWidth, int[] buffer)
	{
		Platform.runLater(() ->
		{
			if (!killer.isKilled())
			{
				PixelFormat<IntBuffer> format = PixelFormat.getIntArgbPreInstance();
				getImage().getPixelWriter().setPixels(pos * smallWidth, 0, realWidth, imgHeight, format, buffer, 0, realWidth);
			}
		});
	}

	@Override
	protected void computeImgSizes()
	{
		double width = getNav().getWidth();
		double height = getNav().getHeight();

		getNav().setP0(getNav().getP0().translate(width * varx / oldWidth, 0));
		getNav().setP1(getNav().getP1().translate(-width * varx / oldWidth, 0));

		getNav().setP0(getNav().getP0().translate(0, height * vary / oldHeight));
		getNav().setP1(getNav().getP1().translate(0, -height * vary / oldHeight));

		width = getNav().getWidth();
		height = getNav().getHeight();

		if (this.screenSizeX * height < this.screenSizeY * width)
		{
			this.computedImgWidth = this.screenSizeX;
			this.computedImgHeight = (int) ((this.computedImgWidth * height) / width);
		}
		else
		{
			this.computedImgHeight = this.screenSizeY;
			this.computedImgWidth = (int) ((this.computedImgHeight * width) / height);
		}

		varx = width * (this.screenSizeX - this.computedImgWidth) / (2.0 * this.computedImgWidth);
		getNav().setP0(getNav().getP0().translate(-varx, 0));
		getNav().setP1(getNav().getP1().translate(varx, 0));

		vary = height * (this.screenSizeY - this.computedImgHeight) / (2.0 * this.computedImgHeight);
		getNav().setP0(getNav().getP0().translate(0, -vary));
		getNav().setP1(getNav().getP1().translate(0, vary));

		oldWidth = getNav().getWidth();
		oldHeight = getNav().getHeight();

		this.computedImgWidth = this.screenSizeX;
		this.computedImgHeight = this.screenSizeY;
	}

	/* Si on utilise des limites pour le zoom out, on doit les ajuster pour fitter le nouveau ratio
	 * d'écran. */
	private void updateBoundsToScreenRatio()
	{
		double var;
		double width = ContinuousFractal.DEF_DOWN_RIGHT_BOUND.getX() - ContinuousFractal.DEF_UP_LEFT_BOUND.getX();
		double height = ContinuousFractal.DEF_DOWN_RIGHT_BOUND.getY() - ContinuousFractal.DEF_UP_LEFT_BOUND.getY();

		// Pour que l'élargissement des bounds se fasse d'après le centre visuel du fractal plutôt
		// que du point (0,0).
		double fractalCenter = 0.5 * (ContinuousFractal.DEF_DOWN_RIGHT_BOUND.getX() + ContinuousFractal.DEF_UP_LEFT_BOUND.getX());

		if (this.screenSizeX / width < this.screenSizeY / height)
		{
			var = (width) * this.getNav().getHeight() / (2.0 * this.getNav().getWidth());
			getNav().getFractal().setUpLeftBound(
				new DoublePoint(ContinuousFractal.DEF_UP_LEFT_BOUND.getX(), -var)
			);
			getNav().getFractal().setDownRightBound(
				new DoublePoint(ContinuousFractal.DEF_DOWN_RIGHT_BOUND.getX(), var)
			);
		}
		else
		{
			var = (height) * this.getNav().getWidth() / (2.0 * this.getNav().getHeight());
			getNav().getFractal().setUpLeftBound(
				new DoublePoint(-var + fractalCenter, ContinuousFractal.DEF_UP_LEFT_BOUND.getY())
			);
			getNav().getFractal().setDownRightBound(
				new DoublePoint(var + fractalCenter, ContinuousFractal.DEF_DOWN_RIGHT_BOUND.getY())
			);
		}
	}

	@Override
	public ContFracNav getNav() 
	{
		ensureOnFXThread();
		return (ContFracNav) this.fracNav;
	}

	private static void ensureOnFXThread()
	{
		if (!Platform.isFxApplicationThread())
			throw new RuntimeException("Generator: Pas sur le thread JavaFX!");
	}

	/**
	 * Défini nbrProcessors sur le nombre de processeurs à utiliser.
	 */
	private void findNbrProcessors()
	{
		int n = Runtime.getRuntime().availableProcessors();
		nbrProcessors = n > 1 ? n - 1 : n;
	}

	/**
	 * Crée un nouvel ExecutorService pour le Generator.
	 */
	@Override
	public void newExecutor() 
	{
		findNbrProcessors();
		exec = Executors.newWorkStealingPool(nbrProcessors);
	}

	@Override
	public ContinuousGenerator newGeneratorInstance(int maxWidth, int maxHeight) 
	{
		ContinuousGenerator gen = new ContinuousGenerator(this.getNav().clone(), maxWidth, maxHeight);
		gen.new Updater().sample(getSample()).renderingStep(0).update();
		return gen;
	}

}
