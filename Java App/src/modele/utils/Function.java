package modele.utils;

/**
 * Interface fonctionnelle (de
 * 
 * <pre>
 * {} --> {}
 * </pre>
 * 
 * ) utilisée un peu partout au travers du projet. (Je suis au courant que java 8 introduit son
 * propre lot d'utilités dans java.util.Function, mais je suis aussi du genre late-adopter).
 */
public interface Function 
{
	public void execute();

	static Function doNothing()
	{
		return () -> {};
	}

}
