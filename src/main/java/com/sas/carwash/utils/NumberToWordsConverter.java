package com.sas.carwash.utils;

import java.math.BigDecimal;

public class NumberToWordsConverter {

	private static final String[] units = { "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
			"Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen",
			"Nineteen" };

	private static final String[] tens = { "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty",
			"Ninety" };

	private static String convertLessThanOneThousand(int n) {
		if (n < 20) {
			return units[n];
		}
		if (n < 100) {
			return tens[n / 10] + (n % 10 != 0 ? " " + units[n % 10] : "");
		}
		return units[n / 100] + " Hundred" + (n % 100 != 0 ? " and " + convertLessThanOneThousand(n % 100) : "");
	}

	public static String convert(int n) {
		if (n == 0) {
			return "Zero";
		}

		StringBuilder result = new StringBuilder();

		if (n / 10000000 > 0) {
			result.append(convertLessThanOneThousand(n / 10000000)).append(" Crore ");
			n %= 10000000;
		}
		if (n / 100000 > 0) {
			result.append(convertLessThanOneThousand(n / 100000)).append(" Lakh ");
			n %= 100000;
		}
		if (n / 1000 > 0) {
			result.append(convertLessThanOneThousand(n / 1000)).append(" Thousand ");
			n %= 1000;
		}
		if (n > 0) {
			result.append(convertLessThanOneThousand(n));
		}

		return result.toString().trim();
	}

	public static String convert(BigDecimal number) {
		if (number.signum() == 0) {
			return "Zero";
		}

		String[] parts = number.toPlainString().split("\\.");
		int integerPart = Integer.parseInt(parts[0]);
		int fractionalPart = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;

		StringBuilder result = new StringBuilder();
		result.append(convert(integerPart));

		if (fractionalPart > 0) {
			result.append(" and ").append(convert(fractionalPart)).append(" Paise");
		}

		result.append(" Only");
		return result.toString();
	}
}
