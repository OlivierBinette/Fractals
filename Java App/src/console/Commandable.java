package console;

import java.util.Collection;
import java.util.List;

/**
 * Représente un objet possédant son propre dictionnaire et capable d'exécuter des Commandes, ou
 * bien de les relayer à ses enfants.
 */
public interface Commandable 
{

	/**
	 * 
	 * @return la liste des enfants immédiats à cet objet.
	 */
	public List<Commandable> getChildren();

	/**
	 * Retourne une liste comprenant tous les enfants Commandable se rattachant à cet objet.
	 * 
	 * La liste est organisée par niveau. La première liste imbriquée contient les enfants immédiats
	 * à cet objet, la deuxième liste imbriquée contient les enfants de ces enfants, et ainsi de
	 * suite.
	 * 
	 * @return la liste de tous les enfants rattachés à cet objet.
	 */
	public List<List<Commandable>> getFlatChildrenTree();

	public void add(Commandable child);

	public void addAll(Collection<? extends Commandable> child);

	/**
	 * Retourne la Definition représentant le nom de ce Commandable et ses synonymes.
	 * 
	 * On utilise cette Definition pour cibler cet objet dans une Command.
	 * 
	 * @return la Definition représentant ce Commandable.
	 */
	public Definition getThisTargetDefinition();

	/**
	 * @return le dictionnaire immédiat de ce Commandable.
	 */
	public Dictionary getDictionary();

	/**
	 * 
	 * @return le dictionnaire immédiat de ce Commandable, combiné aux dictionnaires de tous les
	 *         enfants Commandable s'y rattachant.
	 */
	public Dictionary getThisAndChildrenDictionary();

	/**
	 * Relaye la Command à ce Commandable, qui l'éxécute ou bien la soumet à ses enfants.
	 * 
	 * @param command la commande à exécuter.
	 * @return si on doit continuer l'exécution par niveau, si la commande a été exécutée, ou bien
	 *         si la commande ne peut être exécutée.
	 */
	public Feedback execute(Command command);

}
