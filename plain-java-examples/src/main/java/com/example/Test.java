package com.example;

import com.github.madhavdhatrak.blaze4j.Blaze;
import com.github.madhavdhatrak.blaze4j.CompiledSchema;
public class Test {
    public static void main(String[] args) {
        String schemaJson = """
            {
                "$schema": "https://json-schema.org/draft/2020-12/schema",
                "type": "string"
            }
            """;

        try (CompiledSchema schema = Blaze.compile(schemaJson)) {
            // Test valid string input
            boolean validStringResult = Blaze.validate(schema, "\"hello\"");
            System.out.println("Validation result for \"hello\": " + validStringResult);

            // Test invalid number input
            boolean invalidNumberResult = Blaze.validate(schema, "42");
            System.out.println("Validation result for 42: " + invalidNumberResult);
        }
    }
}

// use this command to run this test : mvn compile exec:java -Dexec.mainClass=com.example.Test






