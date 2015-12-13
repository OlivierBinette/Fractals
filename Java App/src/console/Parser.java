package console;

/**
 * Permet de transformer une instruction en langage courant en Command.
 */
public interface Parser 
{

	/**
	 * Transforme une instruction exprimée en langage courant (anglais) en une Command.
	 * 
	 * @param instruction l'instruction, exprimée en langage courant, à transformer en Command.
	 * @return la Command représentatn l'instruction.
	 */
	public Command parse(String instruction);
}
