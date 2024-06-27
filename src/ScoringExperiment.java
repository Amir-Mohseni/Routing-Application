import com.csvreader.CsvWriter;
import dbTables.AddressScore;

import java.util.ArrayList;
import java.util.List;

public class ScoringExperiment {
    public static void main(String[] args) {
        // Record all the scores for all addresses using AmenitiesCalculator into a CSV file
        List<AddressScore> scores = new ArrayList<>();
        AmenitiesCalculator.calculateAllScores(scores);

        // Write the scores to a CSV file
        CsvWriter csvWriter = new CsvWriter("data/experiments/scores.csv");
        try {
           // Columns "Postal Code", "Score", "Amenity Score", "Shop Score", "Tourism Score", "Accessibility Score"
            csvWriter.write("Postal Code");
            csvWriter.write("Score");
            csvWriter.write("Amenity Score");
            csvWriter.write("Shop Score");
            csvWriter.write("Tourism Score");
            csvWriter.write("Accessibility Score");
            csvWriter.endRecord();

            for (AddressScore score : scores) {
                csvWriter.write(score.getPostalCode());
                csvWriter.write(String.valueOf(score.getScore()));
                csvWriter.write(String.valueOf(score.getAmenityScore()));
                csvWriter.write(String.valueOf(score.getShopScore()));
                csvWriter.write(String.valueOf(score.getTourismScore()));
                csvWriter.write(String.valueOf(score.getAccessibilityScore()));
                csvWriter.endRecord();
            }

            csvWriter.close();
        }
        catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

    }
}
