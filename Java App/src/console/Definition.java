package console;

public interface Definition extends Comparable<Definition> 
{

	/**
	 * Le type de la définition, utilisé pour réduire le « name clash » en la catégorisant.
	 */
	public static enum Type 
	{
		COMMAND_TYPE, TARGET, FIELD, ACTION
	};

	/**
	 * @return le nom de la définition. Peut être une expression régulière.
	 */
	public String getName();

	public Type getType();

	/**
	 * @return l'expréssion régulière associée à cette définition.
	 */
	public String getSynonymRegex();

	/**
	 * 
	 * @param synonym le nom de la définition potentiellement synonyme.
	 * @return true si synonym matches getSynonymRegex(), false sinon.
	 */
	public boolean isSynonymTo(String synonym);

	/**
	 * Compares this to an other Definition.
	 * 
	 * Should return true if and only if both Definitions have the same name and type. Strings
	 * should be compared without considering case.
	 * 
	 * @param obj the Definition to compare to.
	 * @return true if the obj Definition has the same name as this.
	 */
	public boolean equals(Object obj);

	public int hashCode();

	public int compareTo(Definition def);

}
