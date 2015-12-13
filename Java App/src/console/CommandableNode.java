package console;

/**
 * <p>
 * Interface utilitaire permettant de s'assurer qu'un objet possède une instance Commandable
 * associée à lui-même. Cela permet d'implémenter Commandable dans un classe interne à l'objet
 * plutôt que sur l'objet lui-même.
 * </p>
 * 
 * <p>
 * C'est utile pour « singer » l'héritage multiple. Une classe interne peut étendre une
 * implémentation de Commandable, et donc ne pas avoir à redéfinir l'implémentation en entier.
 * </p>
 * 
 */
public interface CommandableNode 
{

	/**
	 * Retourne l'instance Commandable associée à la classe.
	 */
	public Commandable getCommandableInstance();
}
