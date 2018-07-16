import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        List<DataRow> trainingData = new ArrayList<>();
        List<DataRow> testingData = new ArrayList<>();
        double testAccuracy = 0.00;

        // Setup //
        String inputFilePath =  "data.csv"; // using relative path locations
        String outputFilePath = "output.txt";
        String delimiter = ",";
        double trainingTestingSplit = 0.66; // Must be between 0.5 and 1
        String [] attributeHeaders = new String[] {"body-length", "wing-length", "body-width", "wing-width"};
        String [] uniqueOutcomes = new String[] {"BarnOwl", "SnowyOwl", "LongEaredOwl"};
        String outcomeHeader = "type";
        int crossFoldValidation = 10;
        FileWriter fileWriter = new FileWriter(outputFilePath, true);

        // Perform Algorithm Steps ... //
        for (int i = 0; i < crossFoldValidation; i ++) {
            // Read CSV File and get Training and Testing data sets //
            fileWriter.write("Test Number: "+(i+1)+System.getProperty("line.separator"));
            CSVReader csvReader = new CSVReader();
            csvReader.readCSV(inputFilePath, delimiter, trainingTestingSplit, attributeHeaders, outcomeHeader);
            trainingData = csvReader.getTrainingData();
            testingData = csvReader.getTestingData();

            // LogisticRegression //
            LogisticRegression logisticRegression = new LogisticRegression(
                    outputFilePath, trainingData, testingData, attributeHeaders, uniqueOutcomes);
            logisticRegression.setFileWriter(fileWriter);
            double[][] trainedWeightMatrix = logisticRegression.beginTraining();
            logisticRegression.beginTesting(trainedWeightMatrix);
            testAccuracy += logisticRegression.getTestAccuracy();
        }

        // Overall Average Test Accuracy //
        String averageAccuracy = ("**** Average Test Accuracy: " +testAccuracy/crossFoldValidation +" ****");
        System.out.println("\n\n"+averageAccuracy);
        fileWriter.write(averageAccuracy);
        fileWriter.close();
    }
}
