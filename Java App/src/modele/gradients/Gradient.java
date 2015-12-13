package modele.gradients;

import console.CommandableNode;

public interface Gradient extends CommandableNode 
{

	public int interpolate(double pos);

	public void setMaxPosition(double max);

	public Gradient newInstance();
}
