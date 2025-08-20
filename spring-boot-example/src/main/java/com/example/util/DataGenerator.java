package com.example.util;

import java.time.LocalDate;
import java.util.Random;

public class DataGenerator {

    private static final String[] DEPTS = {"HR", "ENGINEERING", "SALES", "FINANCE", "MARKETING", "OPERATIONS", "LEGAL"};
    private static final String[] CURRENCIES = {"USD", "EUR", "GBP", "JPY", "INR", "AUD", "CAD"};
    private static final Random random = new Random();

    public String generate(int count) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < count; i++) {
            if (i > 0) sb.append(',');
            sb.append(buildEmployee(i));
        }
        sb.append(']');
        return sb.toString();
    }

    private String buildEmployee(int idx) {
        String id = String.format("EMP%05d", idx);
        String fullName = "Employee " + idx;
        String birthDate = LocalDate.now().minusYears(20 + random.nextInt(25)).toString();
        String department = DEPTS[random.nextInt(DEPTS.length)];
        String title = "Title " + (random.nextInt(10) + 1);
        double salary = 30_000 + random.nextInt(80_000);
        String currency = CURRENCIES[random.nextInt(CURRENCIES.length)];
        int houseNo = 100 + random.nextInt(900);
        int postal = 10000 + random.nextInt(80000);

        // Single primary email contact
        String contact = String.format("[{\"type\":\"email\",\"value\":\"emp%d@example.com\",\"primary\":true}]", idx);

        return String.format("{\"id\":\"%s\",\"fullName\":\"%s\",\"birthDate\":\"%s\",\"department\":\"%s\",\"title\":\"%s\",\"salary\":%.2f,\"currency\":\"%s\",\"address\":{\"street\":\"%d Main St\",\"city\":\"Metropolis\",\"postalCode\":\"%d\",\"country\":\"US\"},\"contacts\":%s}",
                id, fullName, birthDate, department, title, salary, currency, houseNo, postal, contact);
    }
}

