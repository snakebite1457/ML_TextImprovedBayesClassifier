import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Dennis Meyer, Sebastian Brodehl
 *
 */
public class BayesTextClassifier {
    // Stores for each (label,word) pair the theta value
    private HashMap<String, HashMap<String, Double>> theta; // HashMap<label,HashMap<word, theta>>
    // Stores the weight for each (label,word) pair
    private HashMap<String, HashMap<String, Double>> weight; // HashMap<label,HashMap<word, weight>>
    // Stores all existent labels
    private ArrayList<String> label;
    // Stores all words
    private Set<String> words;
    // Stores all documents
    private ArrayList<Document> documents;

    public BayesTextClassifier(ArrayList<Document> documents) {
        this.theta = new HashMap<>();
        this.label = new ArrayList<>();
        this.words = new HashSet<>();
        this.documents = documents;

        for (Document document : this.documents) {
            this.label.add(document.getLabel());
            this.words.addAll(document.getWords());
        }

        System.out.println("Starting step 4");
        // Step 4
        for (String label : this.label) {
            this.theta.put(label, new HashMap<String, Double>());
            for (String word : this.words) {
                int alphai = 1;
                int sumOfAlphas = 0;

                long numerator = 0;
                for (Document document : this.documents) {
                    if (document.getLabel().trim().equals(label.trim())) {
                        continue;
                    }
                    numerator += document.getWordCount(word) + alphai;
                    sumOfAlphas += alphai;
                }

                long denumerator = 0;
                for (Document document : this.documents) {
                    if (document.getLabel().trim().equals(label.trim())) {
                        continue;
                    }
                    for (String docWord : document.getWords()) {
                        denumerator += document.getWordCount(docWord) + sumOfAlphas;
                    }
                }

                double theta = numerator / denumerator;
                this.theta.get(label).put(word, theta);
            }
        }

        HashMap<String, Double> sumOfWeights = new HashMap<>();

        System.out.println("Starting step 5");
        // Step 5
        for (String label : this.label) {
            this.weight.put(label, new HashMap<String, Double>());
            double sum = 0;
            for (String word : this.words) {
                double theta = this.theta.get(label).get(word);
                double log = Math.log(theta);
                this.weight.get(label).put(word, log);
                sum += log;
            }
            sumOfWeights.put(label, sum);
        }

        System.out.println("Starting step 6");
        // Step 6
        for (String label : this.label) {
            for (String word : this.words) {
                double weight = this.weight.get(label).get(word);
                double normalized = weight / sumOfWeights.get(label);
                this.weight.get(label).put(word, normalized);
            }
        }

        // just a little garbage collection
        this.theta = null;
    }

    public String classify(Document document) {
        String label = "";
        HashMap<String, Double> sums = new HashMap<>();
        for (String c : this.label) {
            double weights = 0;
            for (String word : document.getWords()) {
                weights += document.getWordCount(word) * this.weight.get(c).get(word);
            }
            sums.put(c, weights);
        }
        double minimum = Double.MAX_VALUE;
        for (String c : sums.keySet()) {
            if (sums.get(c) < minimum) {
                minimum = sums.get(c);
                label = c;
            }
        }
        return label;
    }


}
