import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is in charge of a training document.
 * It calculates the counts for the words and stores
 * them into a data structure.
 *
 * This class is designed for the BayesTextClassifier.
 *
 * @author Dennis Meyer, Sebastian Brodehl
 *
 */
public class Document {
    private HashMap<String, Integer> wordsWithCounts;
    private HashMap<String, Double> wordsWithImporvedCounts;
    private String _label;


    public String getLabel() {
        return this._label;
    }

    /**
     * Gets the improved word count parameter. Returns always
     * 0 before calculateImprovedCounts isn't called.
     *
     * @param word
     * @return
     */
    public double getWordCount(String word){
        if (!this.wordsWithImporvedCounts.containsKey(word.trim())) {
            return 0;
        }

        return this.wordsWithImporvedCounts.get(word);
    }

    /**
     * Checks of the given word occur in this document
     *
     * @param word The word which will be checked
     * @return True if it occur otherwise false
     */
    public boolean containsWord(String word) {
        return this.wordsWithCounts.containsKey(word);
    }

    public Document(String text, String label) {
        this.wordsWithCounts = new HashMap<String, Integer>();
        this.wordsWithImporvedCounts = new HashMap<String, Double>();

        this._label = label;
        this.transformTextIntoWordsWithCounts(text);
    }

    private void transformTextIntoWordsWithCounts(String text) {
        //TODO: Implement please
    }

    /**
     * Calculates the improved weights for the words. Compare with
     * Paper "Tackling the Poor Assumptions of Naive Base Text Classifiers"
     *
     * The method must be called after all documents being initialized.
     *
     * @param allDocuments All documents for training
     */
    public void calculateImprovedCounts(ArrayList<Document> allDocuments) {
        for(Map.Entry<String, Integer> entry : this.wordsWithCounts.entrySet()) {
            double improvedCount = entry.getValue();

            // First improvement for TWCNB, compare §4.1 TF tranform
            improvedCount = Math.log(entry.getValue() + 1);

            // Second improvement for TWCNB, compare §4.2 IDF transform
            improvedCount *= Math.log(allDocuments.size() / idfTranformHelper(allDocuments, entry.getKey()));

            this.wordsWithImporvedCounts.put(entry.getKey(), improvedCount);
        }

        for(Map.Entry<String, Integer> entry : this.wordsWithCounts.entrySet()) {
            double improvedCount = this.wordsWithImporvedCounts.get(entry.getKey());

            // Third improvement for TWCNB, compare §4.3 length norm
            improvedCount /= Math.sqrt(this.lengthNormHelper());

            this.wordsWithImporvedCounts.put(entry.getKey(), improvedCount);
        }
    }

    private double idfTranformHelper(ArrayList<Document> allDocuments, String word) {

        double returnValue = 0;
        for(Document document : allDocuments) {
            if (document.containsWord(word)) {
                returnValue++;
            }
        }

        return returnValue;
    }

    private double lengthNormHelper(){
        double returnValue = 0;

        for (Map.Entry<String, Double> entry : this.wordsWithImporvedCounts.entrySet()) {
            returnValue = entry.getValue() * entry.getValue();
        }

        return returnValue;
    }

    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }

        if (obj instanceof Document) {
            Document other = (Document)obj;
            return other.getLabel().equals(this._label);
        }

        return false;
    }
}
