package com.example.service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class DataGeneratorService {

	private static final String[] EMPLOYMENT_TYPES = {"FULL_TIME", "PART_TIME", "CONTRACTOR", "INTERN"};
	private static final String[] CURRENCIES = {"USD", "EUR", "GBP", "CAD", "INR"};
	private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final SecureRandom RANDOM = new SecureRandom();

	public String generateEmployeesJson(int count) {
		List<String> employees = new ArrayList<>(count);
		for (int i = 0; i < count; i++) {
			employees.add(generateEmployee());
		}
		return "[" + String.join(",", employees) + "]";
	}

	private String generateEmployee() {
		String employeeId = UUID.randomUUID().toString();
		String fullName = randomName();
		String email = fullName.toLowerCase(Locale.ROOT).replace(" ", ".") + "@example.com";
		String birthDate = LocalDate.now().minusYears(20 + RANDOM.nextInt(25)).toString();
		String hireDate = LocalDate.now().minusDays(RANDOM.nextInt(3650)).toString();
		String employmentType = pick(EMPLOYMENT_TYPES);
		boolean remote = RANDOM.nextBoolean();
		String department = randomDepartment();
		String skills = randomSkills();
		String phones = randomPhones();
		String managerId = RANDOM.nextInt(5) == 0 ? ("\"" + UUID.randomUUID() + "\"") : "null";
		String address = randomAddress();
		double amount = Math.round((30000 + RANDOM.nextDouble() * 120000) * 100.0) / 100.0;
		String currency = pick(CURRENCIES);
		String metadata = "{\"x-note\":\"generated\"}";

		String employeeJson = """
		{
		  \"employeeId\": \"%s\",
		  \"fullName\": \"%s\",
		  \"email\": \"%s\",
		  \"birthDate\": \"%s\",
		  \"hireDate\": \"%s\",
		  \"employmentType\": \"%s\",
		  \"remote\": %s,
		  \"department\": \"%s\",
		  \"skills\": %s,
		  \"phoneNumbers\": %s,
		  \"managerId\": %s,
		  \"address\": %s,
		  \"salary\": {
		    \"amount\": %s,
		    \"currency\": \"%s\"
		  },
		  \"metadata\": %s
		}""".formatted(
		        employeeId, fullName, email, birthDate, hireDate, employmentType, remote,
		        department, skills, phones, managerId, address, amount, currency, metadata);
		return employeeJson;
	}

	private String randomName() {
		String first = randomLetterUpper() + randomLettersLower(4 + RANDOM.nextInt(6));
		String last = randomLetterUpper() + randomLettersLower(5 + RANDOM.nextInt(8));
		return first + " " + last;
	}

	private String randomDepartment() {
		int len = 2 + RANDOM.nextInt(5);
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) sb.append(LETTERS.charAt(RANDOM.nextInt(LETTERS.length())));
		return sb.toString();
	}

	private String randomSkills() {
		int n = RANDOM.nextInt(6);
		List<String> s = new ArrayList<>(n);
		for (int i = 0; i < n; i++) s.add("\"" + randomLettersLower(3 + RANDOM.nextInt(6)) + "\"");
		return "[" + String.join(",", s) + "]";
	}

	private String randomPhones() {
		int n = RANDOM.nextInt(3);
		List<String> s = new ArrayList<>(n);
		for (int i = 0; i < n; i++) s.add("\"+1" + (1000000 + RANDOM.nextInt(9000000)) + "\"");
		return "[" + String.join(",", s) + "]";
	}

	private String randomAddress() {
		String country = RANDOM.nextBoolean() ? "US" : (RANDOM.nextBoolean() ? "CA" : "GB");
		String postal = switch (country) {
			case "US" -> String.valueOf(10000 + RANDOM.nextInt(89999));
			case "CA" -> "A1A 1A1"; 
			default -> "SW1A 1AA"; 
		};
		return new StringBuilder(160)
			.append('{')
			.append("\"street\":\"").append(1 + RANDOM.nextInt(9999)).append(" Main St\",")
			.append("\"city\":\"City").append(RANDOM.nextInt(100)).append("\",")
			.append("\"state\":\"ST\",")
			.append("\"country\":\"").append(country).append("\",")
			.append("\"postalCode\":\"").append(postal).append("\"")
			.append('}')
			.toString();
	}

	private String pick(String[] arr) { return arr[RANDOM.nextInt(arr.length)]; }
	private String randomLetterUpper() { return String.valueOf(LETTERS.charAt(RANDOM.nextInt(LETTERS.length()))); }
	private String randomLettersLower(int n) {
		StringBuilder sb = new StringBuilder(n);
		for (int i = 0; i < n; i++) sb.append((char)('a' + RANDOM.nextInt(26)));
		return sb.toString();
	}
}


