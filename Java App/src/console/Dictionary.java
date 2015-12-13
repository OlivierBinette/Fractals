package console;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Dictionary 
{

	private final Set<Definition>	content;

	public Dictionary()
	{
		this(new Definition[] {});
	}

	public Dictionary(Collection<Definition> definitions)
	{
		this.content = new HashSet<Definition>();
		this.content.addAll(definitions);
	}

	public Dictionary(Definition[] definitions)
	{
		this.content = new HashSet<Definition>();
		this.content.addAll(Arrays.asList(definitions));
	}

	public boolean add(Definition entry)
	{
		return this.content.add(entry);
	}

	public boolean addAll(Definition... entries)
	{
		return Collections.addAll(this.content, entries);
	}

	public boolean addAll(Collection<Definition> entries)
	{
		return this.content.addAll(entries);
	}

	/**
	 * Ajoute à ce Dictionary le contenu du Dicionary passé en paramètre.
	 * 
	 * @param d le Dictionary à ajouter à ce Dictionary.
	 */
	public void combineWidth(Dictionary d)
	{
		this.content.addAll(d.getContent());
	}

	public Collection<Definition> getContent()
	{
		return Collections.unmodifiableCollection(this.content);
	}

	/**
	 * @param defName le nom de la Definition à chercher.
	 * @param type le Type de la Definition à chercher.
	 * @return la Definition nommée defName.
	 */
	public Definition getDefFromName(String defName, Definition.Type type) 
	{
		for (Definition d : this.content)
			if (d.getName().equalsIgnoreCase(defName) && d.getType() == type)
				return d;
		return null;
	}

	/**
	 * Retourne la première Définition trouvée qui matche le synonym spécifié.
	 * 
	 * @param defSynonym le synonyme, qui peut être une expréssion régulière.
	 * @param type le Type de la Definition à chercher.
	 * @return la première Définition trouvée qui matche le synonym spécifié.
	 */
	public Definition getFirstDefFromSynonym(String defSynonym, Definition.Type type) 
	{
		for (Definition d : this.content)
			if (d.getType() == type && defSynonym.matches(d.getSynonymRegex()))
				return d;

		return null;
	}

	/**
	 * Retourne une collection des Définition trouvées qui matches le synonym spécifié.
	 * 
	 * @param defSynonym le synonyme, qui peut être une expréssion régulière.
	 * @param type le Type de la Definition à chercher.
	 * @return une collection des Définition trouvées qui matches le synonym spécifié.
	 */
	public Collection<Definition> getDefsFromSynonym(String defSynonym, Definition.Type type) 
	{
		Collection<Definition> synonyms = new HashSet<Definition>();
		for (Definition d : this.content)
			if (d.getType() == type && defSynonym.matches(d.getSynonymRegex()))
				synonyms.add(d);

		return synonyms;
	}

}
