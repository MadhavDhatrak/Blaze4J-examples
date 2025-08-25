package com.example;

import com.github.madhavdhatrak.blaze4j.BlazeValidator;
import com.github.madhavdhatrak.blaze4j.CompiledSchema;
import com.github.madhavdhatrak.blaze4j.SchemaCompiler;

public class Test {
    public static void main(String[] args) {
        String schemaJson = """
            {
                "$schema": "https://json-schema.org/draft/2020-12/schema",
                "type": "string"
            }
            """;

        SchemaCompiler compiler = new SchemaCompiler();
        try (CompiledSchema schema = compiler.compile(schemaJson)) {
             BlazeValidator validator = new BlazeValidator();
            
            // Test valid string input
            boolean validStringResult = validator.validate(schema, "\"hello\"");
            System.out.println("Validation result for \"hello\": " + validStringResult);

            // Test invalid number input
            boolean invalidNumberResult = validator.validate(schema, "42");
            System.out.println("Validation result for 42: " + invalidNumberResult);
        }
    }
}

// use this command to run this test : mvn compile exec:java -Dexec.mainClass=com.example.Test






