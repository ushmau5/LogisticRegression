import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LogisticRegression {
    private FileWriter fileWriter;
    private String lineSeparator = System.getProperty("line.separator");
    private List<DataRow> trainingData = new ArrayList<>();
    private List<DataRow> testingData = new ArrayList<>();
    private String[] attributeHeaders;
    private String[] uniqueOutcomes;
    private double[][] attributeWeightMatrix;
    private double testAccuracy = 0.00;

    /**
     * Constructor
     */
    public LogisticRegression(String outputFileLocation,
                              List<DataRow> trainingData,
                              List<DataRow> testingData,
                              String[] attributeHeaders,
                              String[] uniqueOutcomes)
    {
        this.trainingData = trainingData;
        this.testingData = testingData;
        this.attributeHeaders = attributeHeaders;
        this.uniqueOutcomes = uniqueOutcomes;
    }

    /**
     * Begin Training and return a matrix with fitted attribute weights.
     * @return
     */
    public double[][] beginTraining() {
        attributeWeightMatrix = initialiseAttributeWeightMatrix(uniqueOutcomes.length, attributeHeaders.length);
        double weightVariance = 0.00;
        double n = trainingData.size();
        double convergeWeightIterations = 2500; // Repeat adjustment of weights so they converge to their most accurate value
        int trainingIterations = 30;
        double learningRate = 1;

        // This top level loop was added in to increase accuracy of training
        for (int currentIteration = 0; currentIteration < trainingIterations; currentIteration++) {
            // For each row in the data set
            for (int rowIndex = 0; rowIndex < trainingData.size(); rowIndex++) {
                DataRow selectedRow = trainingData.get(rowIndex);
                double y = 0.00;

                // For each unique outcome possible
                for (int uniqueOutcomesIndex = 0; uniqueOutcomesIndex < uniqueOutcomes.length; uniqueOutcomesIndex++) {
                    y = 0.00; // Reset y to 0 for next training outcome.
                    double correctPrediction = 0.00; // 1.0 if prediction is correct, 0 if incorrect.

                    // For each attribute on the selected row (aka for each attribute column)
                    for (int attributeHeaderIndex = -1; attributeHeaderIndex < attributeHeaders.length; attributeHeaderIndex++) {
                        // First weight (b0) is not multiplied by any attribute
                        if (attributeHeaderIndex < 0) {
                            y = y + attributeWeightMatrix[uniqueOutcomesIndex][attributeHeaderIndex + 1];
                        } else {
                            // Every other weight eg b1*x1
                            y = y + (attributeWeightMatrix[uniqueOutcomesIndex][attributeHeaderIndex + 1] * //WeightMatrix contains 1 more column than number of attributes.
                                    selectedRow.getAttributes().get(attributeHeaders[attributeHeaderIndex]));
                        }
                    }
                    // At this point we have a value for y = b0 + b1*x1 + b2*x2 ...
                    double probability = sigmoidFunction(y);

                    // Does the predicted outcome match the actual outcome on the row? //
                    if (uniqueOutcomes[uniqueOutcomesIndex].equalsIgnoreCase(selectedRow.getOutcome())) {
                        correctPrediction = 1.00;
                    } else {
                        correctPrediction = 0.00;
                    }

                    for (int i = 0; i < convergeWeightIterations; i++) {
                        // Update weight value for each attribute on the row //
                        for (int attributeHeaderIndex = -1; attributeHeaderIndex < attributeHeaders.length; attributeHeaderIndex++) {
                            // First weight (b0)
                            if (attributeHeaderIndex < 0) {
                                weightVariance = (1.0 / n) * (correctPrediction - probability);
                            } else {
                                // Every other weight eg b1, b2, b3
                                weightVariance = (1.0 / n) * (selectedRow.getAttributes().get(attributeHeaders[attributeHeaderIndex]) * (correctPrediction - probability));
                            }

                            // Update stored attribute weight value  //
                            // Make changes less significant every iteration like a convergence formula would
                            attributeWeightMatrix[uniqueOutcomesIndex][attributeHeaderIndex + 1] += (learningRate / (convergeWeightIterations + 1.00)) * weightVariance;
                        }
                    }
                }
            }
    }
        printAttributeWeightMatrix(attributeWeightMatrix);
        return attributeWeightMatrix;
    }

    /**
     * Begin testing using the trained weight matrix
     * @param trainedWeightMatrix
     */
    public void beginTesting(double[][] trainedWeightMatrix) {
        double y = 0.00;
        double correctPredictions = 0.00;

        // For each row in the data set
        for (int rowIndex = 0; rowIndex < testingData.size(); rowIndex ++) {
            DataRow selectedRow = testingData.get(rowIndex);
            double highestProbability = 0.00; // keep track of the highestProbability.
            HashMap<Double,String> probabilities = new HashMap<>();  // key=probability or sigmoid(y) and value=String outcome (owlType)

            // For each unique outcome possible
            for (int uniqueOutcomesIndex = 0; uniqueOutcomesIndex < uniqueOutcomes.length; uniqueOutcomesIndex ++) {
                y = 0.00; // Reset y to 0 for next testing outcome.

                // For each attribute on the selected row
                for (int attributeHeaderIndex = -1; attributeHeaderIndex < attributeHeaders.length; attributeHeaderIndex ++) {
                    // First weight (b0) is not multiplied by any attribute
                    if (attributeHeaderIndex < 0) {
                        y = y + trainedWeightMatrix[uniqueOutcomesIndex][attributeHeaderIndex+1];
                    } else {
                        // Every other weight eg b1*x1
                        y = y + ( trainedWeightMatrix[uniqueOutcomesIndex][attributeHeaderIndex+1] * //WeightMatrix contains 1 more column than number of attributes.
                                selectedRow.getAttributes().get(attributeHeaders[attributeHeaderIndex]) );
                    }
                }
                // At this point we have a value for y = b0 + b1*x1 + b2*x2 ...
                double probability = sigmoidFunction(y);
                // Put outcome in a map with the probability as the key //
                probabilities.put(probability, uniqueOutcomes[uniqueOutcomesIndex]);
                if (probability > highestProbability) {
                    highestProbability = probability; // keep track of which outcome has highest probability of occurring.
                }
            }
            // Does the most likely outcome match the actual outcome? //
            if (probabilities.get(highestProbability).equalsIgnoreCase(selectedRow.getOutcome())) {
                correctPredictions ++;
                String text = "[Y] - predicted: " +probabilities.get(highestProbability)+ "\tactual: "+selectedRow.getOutcome();
                System.out.println(text);
                writeToFile(text+ lineSeparator);
            } else {
                String text = "[N] - predicted: " +probabilities.get(highestProbability)+ "\tactual: "+selectedRow.getOutcome();
                System.out.println(text);
                writeToFile(text+ lineSeparator);
            }
        }
        testAccuracy = ( correctPredictions/(double) testingData.size() ) * 100;
        String accuracyText = ("Testing Accuracy: " +testAccuracy+ "%")+lineSeparator+lineSeparator;
        writeToFile(accuracyText);
        System.out.println("\nTesting Accuracy: " +testAccuracy+ "%");
    }


    /**
     * Initialise a matrix of equals weights used in y = b0 + b1*x1 + b2*x2 ...
     * x = attribute
     * b = weight
     * Note: always 1 more weight than number of attributes.
     * @param numOutcomes - Number of unique outcomes in the data.
     * @param numAttributes - number of unique attributes in the data.
     * @return - weightMatrix[#rows][#columns]
     */
    private double[][] initialiseAttributeWeightMatrix(int numOutcomes, int numAttributes) {
        double[][] weightMatrix = new double[numOutcomes][numAttributes+1];

        for (int i = 0; i < numOutcomes; i++) {
            for (int j = 0; j < numAttributes+1; j ++) {
                weightMatrix[i][j] = 0.5; // All attributes initially have equal weight
            }
        }
        return weightMatrix;
    }

    /**
     * Sigmoid Function
     * @param y
     * @return
     */
    private double sigmoidFunction(double y) {
        return 1.0/( 1.0+(Math.exp(-y)) );
    }

    /**
     * Print the weight matrix to the console
     * @param attributeWeightMatrix
     */
    private void printAttributeWeightMatrix(double[][] attributeWeightMatrix) {
        System.out.println("\n\n-- Coefficient Weight Matrix --");
        for (int row = 0; row < uniqueOutcomes.length; row ++) {
            System.out.println("\nOutcome: " +uniqueOutcomes[row]);
            for (int column = 0; column < attributeHeaders.length; column ++) {
                System.out.print("\t"+attributeWeightMatrix[row][column]);
            }
        }
        System.out.println("\n"); // print spacing line
    }

    /**
     * Return the accuracy of the latest test run
     * @return
     */
    public double getTestAccuracy() {
        return testAccuracy;
    }

    /**
     * Write a String to the output file.
     * @param text
     */
    public void writeToFile(String text) {
        try {
            fileWriter.write(text);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not write to file...");
        }
    }

    /**
     * Overwrite the default FileWriter
     * @param fileWriter
     */
    public void setFileWriter(FileWriter fileWriter) {
        this.fileWriter = fileWriter;
    }

}
