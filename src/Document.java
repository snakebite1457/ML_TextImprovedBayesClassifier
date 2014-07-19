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
    private HashMap<String, Double> wordsWithImprovedCounts;
    private String _label;

    public String getLabel() {
        return this._label;
    }

    public java.util.Set<String> getWords() {
        return wordsWithCounts.keySet();
    }

    /**
     * Gets the improved word count parameter. Returns always
     * 0 before calculateImprovedCounts isn't called.
     *
     * @param word
     * @return
     */
    public double getWordCount(String word){
        if (!this.wordsWithImprovedCounts.containsKey(word.trim())) {
            return 0.0;
        }
        return this.wordsWithImprovedCounts.get(word);
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
        this.wordsWithCounts = new HashMap<>();
        this.wordsWithImprovedCounts = new HashMap<>();
        this._label = label;
        this.transformTextIntoWordsWithCounts(text);
    }

    private void transformTextIntoWordsWithCounts(String text) {
        String[] seperatedText = text.split(" ");
        for(String s : seperatedText) {
            String item = s.trim();
            if (wordsWithCounts.containsKey(item)) {
                int count = wordsWithCounts.get(item);
                wordsWithCounts.put(item, ++count);
            } else {
                wordsWithCounts.put(item, 1);
            }
        }
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
            double improvedCount;
            // First improvement for TWCNB, compare §4.1 TF tranform
            improvedCount = Math.log(entry.getValue() + 1);
            // Second improvement for TWCNB, compare §4.2 IDF transform
            improvedCount *= Math.log(allDocuments.size() / idfTransformHelper(allDocuments, entry.getKey()));
            this.wordsWithImprovedCounts.put(entry.getKey(), improvedCount);
        }

        for(Map.Entry<String, Integer> entry : this.wordsWithCounts.entrySet()) {
            double improvedCount = this.wordsWithImprovedCounts.get(entry.getKey());
            // Third improvement for TWCNB, compare §4.3 length norm
            improvedCount /= Math.sqrt(this.lengthNormHelper());
            this.wordsWithImprovedCounts.put(entry.getKey(), improvedCount);
        }
    }

    private double idfTransformHelper(ArrayList<Document> allDocuments, String word) {
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
        for (Map.Entry<String, Double> entry : this.wordsWithImprovedCounts.entrySet()) {
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
