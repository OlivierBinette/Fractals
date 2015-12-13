package dynamic;

public class CalculatorUtil {
	private CalculatorUtil() {};

	public static final Calculator	DEFAULT_CALC_RE		= new Calculator() {
		public double calculate(double zRe, double zImg)
		{
			return zRe * zRe - zImg * zImg;
		}
	};

	public static final Calculator	DEFAULT_CALC_IMG	= new Calculator() {
		public double calculate(double zRe, double zImg)
		{
			return 2 * zRe * zImg;
		}
	};

}
