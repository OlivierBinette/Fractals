package modele.utils;

public class Controller {

	private boolean		finished;

	private Function	onFinish	= () -> {};

	private int			count;
	private final int	maxCount;

	public Controller(int maxCount)
	{
		this.maxCount = maxCount;
		finished = false;
		this.count = 0;
	}

	public synchronized void finished()
	{
		count++;
		this.checkIfCountReached();
	}

	private void checkIfCountReached()
	{
		if (this.count >= this.maxCount)
		{
			finished = true;
			this.onFinish.execute();
		}
	}

	public synchronized int getCount()
	{
		return this.count;
	}

	public synchronized boolean allFinished()
	{
		return this.finished;
	}

	public void setOnFinish(Function func)
	{
		this.onFinish = func;
	}
}
