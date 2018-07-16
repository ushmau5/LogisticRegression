import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Read from CSV file and setup training and testing data.
 */
public class CSVReader {

    private BufferedReader br;
    private boolean fileHasBeenRead = false;
    private List<DataRow> trainingData = null;
    private List<DataRow> testingData = null;

    /**
     * Read CSV File and assign training and testing data based on a percentage split
     * @param filePath
     * @param delimiter
     * @param trainingTestingSplit
     * @param attributeHeaders
     * @param outcomeHeader
     */
    public void readCSV(String filePath, String delimiter, double trainingTestingSplit, String[] attributeHeaders, String outcomeHeader) {
        List<DataRow> csvData = new ArrayList<>();
        String row;

        try {
            br = new BufferedReader(new FileReader(filePath));

            while ((row = br.readLine()) != null) {
                String [] csvLine = row.split(delimiter);

                // Make a map of attribute header and its corresponding value //
                HashMap<String, Double> attributeData = new HashMap<>();
                for (int i = 0; i < csvLine.length - 1; i ++) {
                    // Loop to csvLine.length - 1 as the last value on the line is the outcome. //
                    attributeData.put(attributeHeaders[i], Double.parseDouble(csvLine[i]));
                }

                String outcome = csvLine[csvLine.length - 1]; // Outcome is the last value on the line.
                DataRow dataRow = new DataRow(attributeData, outcome);

                csvData.add(dataRow);
            }
        }

        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error reading CSV file!");
        }

        setTrainingTestingData(csvData, trainingTestingSplit);
        fileHasBeenRead = true;
        System.out.println(
                "\n"+
                "DataSet of size: " +csvData.size()+
                "\nTraining Data of size: " +trainingData.size()+
                "\nTesting Data of size: " +testingData.size()
        );
    }

    /**
     *  Split data into separate list of training and testing data based on trainingTestingSplit value
     * @param csvData
     * @param trainingTestingSplit
     */
    private void setTrainingTestingData(List<DataRow> csvData, double trainingTestingSplit) {
        int splitValue;

        // Calculate index that the array of data should be split at //
        if (trainingTestingSplit >= 0.5 && trainingTestingSplit <= 1) {
            splitValue = (int) Math.round(csvData.size() * trainingTestingSplit);
        } else {
            System.out.println("Split not between 0.5 to 1, using 0.66 instead..");
            splitValue = (int) Math.round(csvData.size() * 0.66);
        }

        // Shuffle data set and set training/testing lists based on split value //
        Collections.shuffle(csvData);
        trainingData = csvData.subList(0, splitValue);
        testingData = csvData.subList(splitValue, csvData.size());
    }

    /**
     * Return the Training Data List
     * @return
     */
    public List<DataRow> getTrainingData() {
        if (!fileHasBeenRead) {
            System.out.println("Caution! - TrainingData is empty as you have not read a CSV file!");
        }
        return trainingData;
    }

    /**
     * Return the Testing Data List
     * @return
     */
    public List<DataRow> getTestingData() {
        if (!fileHasBeenRead) {
            System.out.println("Caution! - TestingData is empty as you have not read a CSV file!");
        }
        return testingData;
    }
}
