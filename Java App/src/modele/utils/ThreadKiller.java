package modele.utils;

public class ThreadKiller 
{
	private boolean	killed;

	public ThreadKiller()
	{
		killed = false;
	}

	public ThreadKiller(boolean value)
	{
		killed = value;
	}

	public synchronized void killAll()
	{
		this.killed = true;
	}

	public synchronized boolean isKilled()
	{
		return this.killed;
	}
}
