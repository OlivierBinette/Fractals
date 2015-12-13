package console;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * Le système d'«analyse sémantique»
 * </p>
 * <p>
 * L'idée générale est d'exploiter la structure interne du programme - sa hiérarchie et son
 * polymorphisme - afin de pouvoir inférer les informations qui seraient sous-entendues dans
 * l'instruction exprimée en langage courant. Chaque objet « Commandable » peut potentiellement
 * exécuter une Command, ou bien la relayer à ses « enfants ». On exploite cette hiérarchie en
 * tentant d'exécuter la Command en la transmettant aux plus haux niveaux en premier, puis aux
 * niveaux inférieurs. Si certaines informations de contexte sont spécifiées dans la Command, cette
 * méthode de relais par niveau démmarre au plus haut niveau où le contexte spécifié correspond.
 * </p>

 * <p>
 * Chaque objet Commandable possède son propre lexique et intègre lui-même leur sémantique. Quand il
 * recoit une Command, il tente de la traduire avec ses propres outils, ce qui lui permet de
 * conclure si la Command est exécutable à son niveau, ou bien si elle doit être relayée. On peut
 * donc avoir un même mot qui signifie des choses différentes pour deux objets différents, sans que
 * cela cause problème. Autrement dit, ce système permet de gérer l'ambiguité du langage.
 * </p>
 */

public interface Commander 
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
	 * @return la combinaison des dictionnaires de tous les enfants de ce Commander.
	 */
	public Dictionary getDictionary();

	/**
	 * Lance la commande dans l'arbre des Commandable.
	 * 
	 * @param command la commande à exécuter.
	 * @return true si la commande a été exécutée, false sinon.
	 */
	public boolean send(Command command);

}
