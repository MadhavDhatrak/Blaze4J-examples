package com.example;
import com.github.madhavdhatrak.blaze4j.Blaze;
import com.github.madhavdhatrak.blaze4j.CompiledSchema;
import com.github.madhavdhatrak.blaze4j.Blaze4jLogger;
import java.util.logging.Level;

public class ClasspathTest {
    public static void main(String[] args) {

        Blaze4jLogger.configureLogging(Level.FINE);
        // You can use this Blaze4jLogger to configure the logging level for Blaze4j.
        // use can use the Levels like  FINE , INFO , WARNING , SEVERE , ALL

        String schemaJson = """
            {
                "$schema": "https://json-schema.org/draft/2020-12/schema",
                "$ref": "classpath://test-schema.json" 
            }
            """;

        try (CompiledSchema schema = Blaze.compile(schemaJson)) {
            String validJson = "{ \"name\": \"test\", \"value\": 42 }";
            boolean validResult = Blaze.validate(schema, validJson);
            System.out.println("Validation result for validJson: " + validResult);


            String minimalJson = "{ \"name\": \"test\" }";
            boolean minimalResult = Blaze.validate(schema, minimalJson);
            System.out.println("Validation result for minimalJson: " + minimalResult);


            String invalidJson = "{ \"value\": 42 }";
            boolean invalidResult = Blaze.validate(schema, invalidJson);
            System.out.println("Validation result for invalidJson: " + invalidResult);

            String wrongTypeJson = "{ \"name\": \"test\", \"value\": \"not-a-number\" }";
            boolean wrongTypeResult = Blaze.validate(schema, wrongTypeJson);
            System.out.println("Validation result for wrongTypeJson: " + wrongTypeResult);

        }   
    }
}

// use this command to run this test : mvn compile exec:java -Dexec.mainClass=com.example.ClasspathTest
