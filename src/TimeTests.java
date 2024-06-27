import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class TimeTests {
    public static void main(String[] args) {
        // Path to the CSV file
        String inputFilePath = "data/experiments/Testing - Project 1-2 - App Runtime.csv";
        String outputFilePath = "data/experiments/Testing - Project 1-2 - App Runtime Updated.csv";

        try {
            // Read the CSV file
            CsvReader csvReader = new CsvReader(new FileReader(inputFilePath));
            List<String[]> records = new ArrayList<>();
            csvReader.readHeaders();
            String[] headers = csvReader.getHeaders();
            boolean hasRuntimeColumn = false;

            // Check if Runtime(ms) column exists
            for (String header : headers) {
                if (header.equals("Runtime(ms)")) {
                    hasRuntimeColumn = true;
                    break;
                }
            }

            // Read all records and add the runtime column if it doesn't exist
            while (csvReader.readRecord()) {
                String[] record = csvReader.getValues();
                if (!hasRuntimeColumn) {
                    String[] newRecord = new String[record.length + 1];
                    System.arraycopy(record, 0, newRecord, 0, record.length);
                    newRecord[record.length] = ""; // Initialize new column with empty value
                    records.add(newRecord);
                } else {
                    records.add(record);
                }
            }
            csvReader.close();

            // Write the updated records to a new CSV file with the runtime column
            CsvWriter csvWriter = new CsvWriter(new FileWriter(outputFilePath), ',');
            if (!hasRuntimeColumn) {
                String[] newHeaders = new String[headers.length + 1];
                System.arraycopy(headers, 0, newHeaders, 0, headers.length);
                newHeaders[headers.length] = "Runtime(ms)";
                csvWriter.writeRecord(newHeaders);
            } else {
                csvWriter.writeRecord(headers);
            }

            // Default start time for tests
            String defaultStartTime = "08:00:00";

            // Process each record, run the application, and record the runtime
            for (String[] record : records) {
                if (record.length < 1) {
                    System.err.println("Invalid test record: " + String.join(", ", record));
                    continue;
                }

                String startPostalCode = record[0];
                String endPostalCode = record[1];  // Default end postal code

                long startTimeMs = System.currentTimeMillis();
                try {
                    RoutingApplication.findBestRoute(startPostalCode, endPostalCode, defaultStartTime); // Assuming the method signature is findBestRoute(String startPostalCode, String endPostalCode, String startTime)
                } catch (Exception e) {
                    System.err.println("Error running RoutingApplication: " + e.getMessage());
                }
                long endTimeMs = System.currentTimeMillis();
                long runtime = endTimeMs - startTimeMs;

                System.out.println("Runtime: " + runtime);

                // Add the runtime to the record
                record[record.length - 1] = String.valueOf(runtime);

                csvWriter.writeRecord(record);
            }

            csvWriter.close();
            System.out.println("Updated CSV with runtime information saved to " + outputFilePath);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}