package console.implementations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import console.Command;
import console.Commandable;
import console.Commander;
import console.Dictionary;
import console.Feedback;

public class SimpleCommander extends Object implements Commander 
{

	private List<List<Commandable>>	flatChildrenTree;
	private List<Commandable>		children;
	
	public SimpleCommander()
	{
		this.children = new ArrayList<Commandable>();
		this.flatChildrenTree = new ArrayList<List<Commandable>>();
		this.makeFlatChildrenTree();
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Commandable> getChildren() 
	{
		return Collections.unmodifiableList(this.children);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<List<Commandable>> getFlatChildrenTree() 
	{
		return Collections.unmodifiableList(this.flatChildrenTree);
	}

	/**
	 * {@inheritDoc}
	 */
	public void add(Commandable child) 
	{
		this.children.add(child);
		this.makeFlatChildrenTree();
	}

	public void addAll(Collection<? extends Commandable> children) 
	{
		this.children.addAll(children);
		this.makeFlatChildrenTree();
	}

	private void makeFlatChildrenTree()
	{
		List<List<Commandable>> childList;
		List<Commandable> temp;

		this.flatChildrenTree.clear();
		this.flatChildrenTree.add(this.children);

		for (int c = 0; c < this.children.size(); c++)
		{
			childList = this.children.get(c).getFlatChildrenTree();
			for (int i = 0; i < childList.size(); i++)
			{
				if (i + 1 >= this.flatChildrenTree.size())
				{
					this.flatChildrenTree.add(childList.get(i));
				}
				else
				{
					temp = this.flatChildrenTree.get(i + 1);
					temp.addAll(childList.get(i));
					this.flatChildrenTree.set(i + 1, temp);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean send(Command command) 
	{
		// We iterate through the flatChildrenTree, level by level, while the
		// execution returns CONTINUE.
		Feedback feed = Feedback.CONTINUE;

		execution:
		for (List<Commandable> level : this.flatChildrenTree)
		{
			for (Commandable child : level)
			{
				feed = child.execute(command);
				if (feed != Feedback.CONTINUE)
					break execution;
			}
		}

		return feed == Feedback.EXECUTED;
	}

	/**
	 * {@inheritDoc}
	 */
	public Dictionary getDictionary() 
	{
		Dictionary main = new Dictionary();
		for (Commandable c : this.children)
			main.combineWidth(c.getThisAndChildrenDictionary());

		return main;
	}
}
