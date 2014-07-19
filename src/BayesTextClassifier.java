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
    private Set<String> label;
    // Stores all words
    private Set<String> words;

    public BayesTextClassifier(ArrayList<Document> documents) {
        this.theta = new HashMap<>();
        this.label = new HashSet<>();
        this.words = new HashSet<>();
        this.weight = new HashMap<>();

        for (Document document : documents) {
            this.label.add(document.getLabel());
            this.words.addAll(document.getWords());
        }

        System.out.println("Starting step 4");
        // Step 4
        double maxTheta = 0;
        // c
        for (String label : this.label) {
            System.out.println("Working on label:" + label);
            this.theta.put(label, new HashMap<String, Double>());

            int alphai = 1;
            int sumOfAlphas = this.words.size() * alphai;

            long denumerator = 0;
            // j
            for (Document document : documents) {
                // Yj != c
                if (document.getLabel().trim().equals(label.trim())) {
                    continue;
                }
                for (String docWord : document.getWords()) {
                    denumerator += document.getWordCount(docWord);
                }
            }
            denumerator += sumOfAlphas;

            // i
            for (String word : this.words) {
                long numerator = 0;
                // j
                for (Document document : documents) {
                    // Yj != c
                    if (document.getLabel().trim().equals(label.trim())) {
                        continue;
                    }
                    numerator += document.getWordCount(word);
                }
                numerator += alphai;

                double theta = 1.0*numerator / 1.0*denumerator;
                this.theta.get(label).put(word, theta);

                if (theta > maxTheta)
                    maxTheta = theta;
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
        this.words = null;
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
