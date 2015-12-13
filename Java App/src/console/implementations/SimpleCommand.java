package console.implementations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import console.Command;

public class SimpleCommand implements Command 
{

	private List<String>	targetChain;
	private String			instruction;

	public SimpleCommand(Command c)
	{
		this.targetChain = new ArrayList<String>();
		Collections.copy(this.targetChain, targetChain);
		this.instruction = c.getInstruction();
	}

	public SimpleCommand(List<String> targetChain, String instruction)
	{
		this.targetChain = targetChain;
		this.instruction = instruction;
	}

	public List<String> getTargetChain() 
	{
		return this.targetChain;
	}

	public String getInstruction() 
	{
		return this.instruction;
	}

	public String toString()
	{
		String s;
		s = Arrays.deepToString(this.targetChain.toArray());
		s = s + " Instruction : " + this.instruction;
		return s;
	}
}
