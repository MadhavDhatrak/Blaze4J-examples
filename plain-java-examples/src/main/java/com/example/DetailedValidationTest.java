package com.example;

import com.github.madhavdhatrak.blaze4j.BlazeValidator;
import com.github.madhavdhatrak.blaze4j.CompiledSchema;
import com.github.madhavdhatrak.blaze4j.SchemaCompiler;
import com.github.madhavdhatrak.blaze4j.ValidationResult;

public class DetailedValidationTest {
    public static void main(String[] args) {
        String enumSchemaJson = """
            {
                "$schema": "https://json-schema.org/draft/2020-12/schema",
                "enum": ["red", "green", "blue"]
            }
            """;

        String invalidEnumInstance = "\"yellow\"";

        SchemaCompiler compiler = new SchemaCompiler();
        try (CompiledSchema compiledSchema = compiler.compile(enumSchemaJson)) {
            BlazeValidator validator = new BlazeValidator();
            ValidationResult result = validator.validateWithDetails(compiledSchema, invalidEnumInstance);

            System.out.println("Is valid: " + result.isValid());

            if (!result.isValid()) {
                System.out.println("Validation errors:");
                result.getErrors().forEach(System.out::println);
            }
        }
    }
}

// use this command to run this test : mvn compile exec:java -Dexec.mainClass=com.example.DetailedValidationTest
