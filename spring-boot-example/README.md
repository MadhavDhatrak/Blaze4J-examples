# Blaze4j Spring Boot Example

This Spring Boot application demonstrates a realistic use case for JSON Schema validation: validating employee data during a batch import process. It benchmarks Blaze4j's performance with different JSON Schema draft versions.

## Features

- Employee JSON Schema (Draft-2020-12) with nested objects, arrays, and various validations
- File upload endpoint for validating JSON files
- Benchmark endpoint for performance testing
- Separate timing for parsing vs. validation phases

## Running the Application

```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="--enable-native-access=ALL-UNNAMED"
```

The application starts on port 8080.

## API Endpoints

### 1. Upload and Validate JSON File

```bash
curl -F "file=@employees-sample.json" http://localhost:8080/api/upload
```

Response:
```json
{
  "blaze4j": { "valid": true, "timeMs": 5 },
  "parseTimeMs": 2
}
```

### 2. Run Benchmark

```bash
# Default 10,000 records
curl http://localhost:8080/api/benchmark

# Custom size (e.g., 100,000 records)
curl "http://localhost:8080/api/benchmark?size=100000"
```

## GitHub Actions Benchmarks

This project includes GitHub Actions workflows to benchmark Blaze4j across different:
- Operating systems (Windows, Linux, macOS)
- Java versions (17, 21, 22)

To run the benchmarks:
1. Fork this repository
2. Go to Actions tab
3. Select "Blaze4j Benchmarks" workflow
4. Click "Run workflow"

## Understanding the Results

The response includes two timing measurements:

- `parseTimeMs`: Time to parse the JSON string into objects
- `blaze4j.timeMs`: Time for Blaze4j to validate the schema

For accurate performance comparison:

1. Look at the validation times, not the parsing time
2. Run multiple requests to warm up the JVM
3. Average the results of several runs

## Sample Files

- `employees-sample.json`: Small sample file with 5 valid employee records
- The benchmark endpoint can generate datasets of any size on the fly