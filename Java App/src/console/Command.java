package console;

import java.util.List;

/**
 * <p>
 * Représente une commande à être exécutée. Celle-ci est très souple et ne demande pas d'avoir une
 * syntaxe ou bien des informations exactes, mais doit respecter le contrat spécifié ci-dessous.
 * </p>
 * 
 * <p>
 * Il faut que son type soit exact.
 * </p>
 * 
 * <p>
 * Pour Type.SET, il lui faut au moins un Field et une Value. Pour Type.GET, il lui faut au moins un
 * Field. Pour Type.DO, il lui faut au moins une Action.
 * </p>
 */
public interface Command 
{

	/**
	 * <p>
	 * Retourne la liste des noms des « targets ».
	 * </p>
	 * <p>
	 * Un target représente un élément Commandable. Lorsque plusieurs targets sont spécifiés, on
	 * suppose qu'ils composent une hiérarchie référant au sujet de la Command. Par exemple,
	 * {"mandelbrot", "generator"} réfère à l'ensemble de Mandelbrot contenu dans le generator.
	 * </p>
	 * <p>
	 * Si la liste des targets est incomplète (ou vide), on tente alors d'utiliser au maximum les
	 * informations qu'elle contient avant de conclure que n'importe quel objet peut se réclamer
	 * comme cible de la commande.
	 * </p>
	 * 
	 * @return la liste représentant la cible et sa hérarchie.
	 */
	public List<String> getTargetChain();

	/**
	 * Retourne la partie « instruction » de la Command, qui doit contenir le message et peut
	 * contenir des informations superflues.
	 */
	public String getInstruction();
}
