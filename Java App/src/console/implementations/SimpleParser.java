package console.implementations;

import java.util.ArrayList;

import console.Command;
import console.Definition;
import console.Definition.Type;
import console.Dictionary;
import console.Parser;

/**
 * <p>
 * Parser utilisant une méthode d'extraction de données pour transformer une instruction en langage
 * courant en une Command. Toute la gestion de l'ambiguité et de l'inférence de cible (si on peut
 * dire) est faite du côté des Command et des Commandable.
 * </p>
 * 
 * <p>
 * Si on souhaiterait avoir un système plus capable, sans tomber dans les longues listes de
 * possibilités, il faudrait utiliser un système d'analyse du sens des phrases et là on tombe dans
 * un autre ordre de complexité (quoi que certaines librairies existes pour nous simplifier la
 * tâche, comme le Natural Language Toolkit avec Python).
 * </p>
 */
public class SimpleParser implements Parser 
{

	public static final Definition	GET, DO;
	public static final Dictionary	COMMAND_TYPES;

	static
	{
		GET = new SimpleDefinition(Type.COMMAND_TYPE, "get", "print", "return");
		DO = new SimpleDefinition(Type.COMMAND_TYPE, "do", "execute");
		COMMAND_TYPES = new Dictionary();
		COMMAND_TYPES.addAll(GET, DO);
	}

	private Dictionary				reference;

	public SimpleParser(Dictionary ref)
	{
		this.reference = ref;
	}

	public Command parse(String s) 
	{
		s = s.toLowerCase();

		ArrayList<String> targetChain = new ArrayList<String>();
		String instruction = s;

		for (Definition def : this.reference.getContent())
		{
			if (s.matches(def.getSynonymRegex()))
			{
				if (def.getType() == Definition.Type.TARGET)
				{
					targetChain.add(def.getName());
				}
			}
		}

		return new SimpleCommand(targetChain, instruction);
	}

}
