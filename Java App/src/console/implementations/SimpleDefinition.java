package console.implementations;

import console.Definition;

public class SimpleDefinition implements Definition {

	protected final String			name;
	protected final String			synonymRegex;
	protected final Definition.Type	type;

	public SimpleDefinition(Definition.Type type, String name, String... synonyms)
	{
		this.name = name;
		String s = "(" + name + ")";
		for (String synonym : synonyms)
			s += "|(" + synonym + ")";

		this.synonymRegex = s;
		this.type = type;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() 
	{
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 */
	public Type getType() 
	{
		return this.type;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getSynonymRegex() 
	{
		return this.synonymRegex;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isSynonymTo(String synonym) 
	{
		return synonym.matches(this.synonymRegex);
	}

	public boolean equals(Object obj)
	{
		if (!(obj instanceof Definition))
			return false;
		Definition def = (Definition) obj;

		return this.type == def.getType() && this.name.equalsIgnoreCase(def.getName());
	}

	public int hashCode()
	{
		int r = 17;
		r = 31 * r + this.type.hashCode();
		r = 31 * r + this.name.hashCode();
		return r;
	}

	public int compareTo(Definition def)
	{
		int c = this.getType().compareTo(def.getType());

		if (c == 0)
			c = this.getName().compareTo(def.getName());

		return c;
	}

	public String toString()
	{
		return "Définition: " + this.type.toString() + " " + this.name + " ";
	}

}
