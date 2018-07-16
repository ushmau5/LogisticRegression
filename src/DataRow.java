import java.util.HashMap;

/**
 * Holds information related to a row of CSV file data.
 */
public class DataRow {

    private HashMap<String, Double> attributes;
    private String outcome;

    /**
     * Constructor
     * @param attributes
     * @param outcome
     */
    public DataRow(HashMap<String, Double> attributes, String outcome) {
        this.attributes = attributes;
        this.outcome = outcome;
    }

    public HashMap<String, Double> getAttributes() {
        return attributes;
    }

    public void setAttributes(HashMap<String, Double> attributes) {
        this.attributes = attributes;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

}
