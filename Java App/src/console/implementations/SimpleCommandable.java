package console.implementations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import console.Command;
import console.Commandable;
import console.Definition;
import console.Dictionary;
import console.Feedback;

public class SimpleCommandable implements Commandable 
{

	private Definition				thisTargetDefinition;
	private Dictionary				dictionary;

	private List<Commandable>		children;
	private List<List<Commandable>>	flatChildrenTree;

	private String					name;

	public SimpleCommandable(String name, String... synonyms)
	{
		this.name = name;
		this.thisTargetDefinition = new SimpleDefinition(Definition.Type.TARGET, name, synonyms);
		this.dictionary = new Dictionary();
		this.children = new ArrayList<Commandable>();
		this.flatChildrenTree = new ArrayList<List<Commandable>>();
		makeFlatChildrenTree();
	}

	public String getName()
	{
		return this.name;
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
	public Definition getThisTargetDefinition() 
	{
		return this.thisTargetDefinition;
	}

	/**
	 * {@inheritDoc}
	 */
	public Dictionary getDictionary() 
	{
		return this.dictionary;
	}

	/**
	 * {@inheritDoc}
	 */
	public Dictionary getThisAndChildrenDictionary() 
	{
		Dictionary main = new Dictionary(this.dictionary.getContent());
		for (Commandable c : this.children)
			main.combineWidth(c.getThisAndChildrenDictionary());

		return main;
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
	public Feedback execute(Command command) 
	{
		// If this Commandable is in the target chain but not the only target, then we push down the
		// command (after removing the target from the target chain) through the flatChildrenTree
		// and return either STOP or EXECUTED.
		// If the target chain but this target is empty, we try to execute the command and return
		// either EXECUTED or CONTINUE.
		// If the target chain does not contain this target but contains other targets, then we
		// return CONTINUE.

		Command cleanedCommand = new SimpleCommand(command);

		boolean contained = false;
		boolean containsOther = false;
		for (String s : command.getTargetChain())
			if (this.thisTargetDefinition.isSynonymTo(s))
			{
				contained = true;
				cleanedCommand.getTargetChain().remove(s);
			}
			else
				containsOther = true;

		if (!containsOther)
		{
			return this.tryToExecuteLocally(command, Feedback.CONTINUE);
		}
		else if (contained)
		{
			if (tryToPushCommandByLevel(cleanedCommand) != Feedback.EXECUTED)
			{
				return this.tryToExecuteLocally(command, Feedback.STOP);
			}

			return Feedback.EXECUTED;
		}
		
		return Feedback.CONTINUE;
	}

	protected Feedback tryToExecuteLocally(Command command, Feedback onFail)
	{

		return onFail;
	}

	private Feedback tryToPushCommandByLevel(Command command)
	{
		Feedback feed = Feedback.STOP;

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

		return feed;
	}

}
