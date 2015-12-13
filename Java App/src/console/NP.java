package console;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NP 
{

	private NP() {};

	/**
	 * Nombre (pour couleur HBS) entre 0.0 et 360.0.
	 */
	public static final String	HBS_0_360		= "(\\d){1,3}(\\.)?(\\d)*";
	/**
	 * Nombre (pour couleur HBS) entre 0.0 et 1.1é
	 */
	public static final String	HBS_0_1			= "(0)*(0|1)?(\\.)?(\\d)*";
	/**
	 * Nombre (pour couleur RGB) entre 0 et 255.
	 */
	public static final String	RGB_0_255		= "(0)*(\\d){1,3}";
	/**
	 * Un octect en hexadécimal.
	 */
	public static final String	HEX				= "[0-9a-fA-F]{2}";

	/**
	 * Couleur HSB pour Color.hsb(...)
	 */
	public static final String	COLOR_VAL_HBS	= "\\(?(?<c1>" + HBS_0_360 + "),\\s*(?<c2>" + HBS_0_1 + "),\\s*(?<c3>" + HBS_0_1 + ")\\)?";
	/**
	 * Couleur RGB pour Color.rgb(...)
	 */
	public static final String	COLOR_VAL_RGB	= "\\(?(?<c1>" + RGB_0_255 + "),\\s*(?<c2>" + RGB_0_255 + "),\\s*(?<c3>" + RGB_0_255 + ")\\)?";
	/**
	 * Couleur en format 0xFFFFFF pour Color.web(...)
	 */
	public static final String	COLOR_VAL_WEB	= "((0[xX])|(#))(?<c1>" + HEX + ")\\s*(?<c2>" + HEX + ")\\s*(?<c3>" + HEX + ")";

	/**
	 * Entier positif.
	 */
	public static final String	POS_INTEGER		= "(\\+)?[0-9]+";
	/**
	 * Entier.
	 */
	public static final String	INTEGER			= "[+-]?[0-9]+";

	/**
	 * Nombre en pourcentage.
	 */
	public static final String	PERCENTAGE		= "(?<c1>" + NP.DOUBLE + ")(\\s)*%";

	/**
	 * Coordonnées en 2d.
	 */
	public static final String	COORDINATES		= "\\(?\\s*(?<c1>" + NP.DOUBLE + "),?\\s*(?<c2>" + NP.DOUBLE + ")\\s*\\)?";

	/**
	 * Dimensions.
	 */
	public static final String	IMAGE_SIZES		= "(?<c1>" + NP.POS_INTEGER + ")(\\s)*[xX]+\\s*(?<c2>" + NP.POS_INTEGER + ")";

	/**
	 * Nombre rationnel, tel que défini dans la classe Double.
	 */
	public static final String	DOUBLE			= "[\\x00-\\x20]*" +
													"[+-]?(" +
													"NaN|" +
													"Infinity|" +
													"((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)([eE][+-]?)?)|" +
													"(\\.((\\p{Digit}+))([eE][+-]?)?)|" +
													"((" +
													"(0[xX](\\p{XDigit}+)(\\.)?)|" +
													"(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+))" +
													")[pP][+-]?(\\p{Digit}+)))" +
													"[fFdD]?))" +
													"[\\x00-\\x20]*";

	/**
	 * Nombre rationnel entier.
	 */
	public static final String	POS_DOUBLE		= "[\\x00-\\x20]*" +
													"(" +
													"NaN|" +
													"Infinity|" +
													"((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)([eE][+-]?)?)|" +
													"(\\.((\\p{Digit}+))([eE][+-]?)?)|" +
													"((" +
													"(0[xX](\\p{XDigit}+)(\\.)?)|" +
													"(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+))" +
													")[pP][+-]?(\\p{Digit}+)))" +
													"[fFdD]?))" +
													"[\\x00-\\x20]*";

	public static double getAsDouble(String number)
	{
		Pattern p = Pattern.compile(DOUBLE);
		Matcher m = p.matcher(number);
		if (m.find())
			return Double.parseDouble(m.group());

		throw new IllegalArgumentException();
	}

	public static long getAsLong(String number)
	{
		Pattern p = Pattern.compile(INTEGER);
		Matcher m = p.matcher(number);
		if (m.find())
			return Long.parseLong(m.group());

		throw new IllegalArgumentException();
	}

	public static int getAsInt(String number)
	{
		Pattern p = Pattern.compile(INTEGER);
		Matcher m = p.matcher(number);
		if (m.find())
			return Integer.parseInt(m.group());

		throw new IllegalArgumentException();
	}
}
