package console.implementations;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe pour tester les expressions régulières. Le code provient grossièrement des tutoriels
 * d'Oracle sur les expressions régulières.
 */
public class RegexTestHarness {

	public static void main(String[] args) {

		Scanner scan = new Scanner(System.in);

		while (true) {
			System.out.println("Enter your regex: ");
			Pattern pattern = Pattern.compile(scan.nextLine());

			System.out.println("Enter input string to search: ");
			Matcher matcher =
				pattern.matcher(scan.nextLine());

			boolean found = false;
			while (matcher.find()) {
				System.out.println("I found the text " +
					matcher.group() + " starting at " +
					"index " + matcher.start() + " and ending at index" + matcher.end());
				found = true;
			}
			if (!found) {
				System.out.println("No match found.");
			}
		}
	}
}
