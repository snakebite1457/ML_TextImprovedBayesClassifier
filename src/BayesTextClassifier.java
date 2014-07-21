import java.util.*;

/**
 * The Naive Bayes classifier was improved by implementing
 * features from the paper "Tackling the Poor Assumptions
 * of Naive Bayes Classifiers" authored by Rennie et. al.
 *
 * The following transformations were performed:
 * - Term Frequency transform, see §4.1 Transforming Term Frequency
 * - Inverse Document Frequency, see §4.2 Transforming by Document Frequency
 * - Length Normalization, see §4.3 Transforming Based on Length
 * - using Complement Naive Bayes, see §3.1 Skewed Data Bias
 * - and weight normalization, see §3.2 Weight Magnitude Errors
 *
 * With these transformations, the accuracy was ~0.9, which is a improvement of ~0.1.
 *
 * We also experimented with removing numbers and small words
 * and came up with a small improvement of ~.005
 *
 * @author Dennis Meyer, Sebastian Brodehl
 * machine learning summer term 2014
 */
public class BayesTextClassifier {
    // Stores for each (label,word) pair the theta value
    private Map<String, Map<String, Double>> theta; // HashMap<label,HashMap<word, theta>>
    // Stores the weight for each (label,word) pair
    private Map<String, Map<String, Double>> weight; // HashMap<label,HashMap<word, weight>>
    // Stores all existent labels
    private Set<String> label;
    // Stores all words
    private Set<String> words;
    // Stores all words with counts
    private  Map<String, Double> wordsWithCounts;
    // usage in % of words
    double wordThreshold = .1;

    /**
     * Constructs a new TWCNB with the given training documents
     * @param trainingDocuments the documents the classifier will train with
     */
    public BayesTextClassifier(ArrayList<Document> trainingDocuments) {
        this.theta = new HashMap<>();
        this.label = new HashSet<>();
        this.words = new HashSet<>();
        this.weight = new HashMap<>();
        this.wordsWithCounts = new HashMap<>();

        for (Document document : trainingDocuments) {
            this.label.add(document.getLabel());
            this.words.addAll(document.getWords());
            for (String word : document.getWords()) {
                if (this.wordsWithCounts.containsKey(word)) {
                    double count = this.wordsWithCounts.get(word) + document.getImprovedWordCount(word);
                    this.wordsWithCounts.put(word, count);
                } else {
                    this.wordsWithCounts.put(word, document.getImprovedWordCount(word));
                }
            }
        }

        int prevWordSize = this.words.size();
        LinkedHashMap<String, Double> sortedMap = (LinkedHashMap<String, Double>) sortByValues(this.wordsWithCounts);
        double maxValue = 0.;
        double threshold = 0.;
        for (Map.Entry<String, Double> entry : sortedMap.entrySet()) {
            if (entry.getValue() > maxValue) {
                maxValue = entry.getValue();
                threshold = maxValue * wordThreshold;
            }
            if (entry.getValue() > threshold) {
                this.words.remove(entry.getKey());
            } else {
                break;
            }
        }

        System.out.println("Removed (most frequent) words: " + (prevWordSize - this.words.size()));


        // do the transformations here



        System.out.println("Starting step 4");
        /**
         * Step 4 of Rennie et. al. (page 7)
         * Computes theta, see § 3.1 Skewed Data Bias
         */
        // over all c
        for (String label : this.label) {
            System.out.println("Working on label:" + label);
            this.theta.put(label, new HashMap<String, Double>());

            // smoothing factors alpha
            int alphai = 1;
            int sumOfAlphas = this.words.size() * alphai;

            double denumerator = 0;
            // over all j
            for (Document document : trainingDocuments) {
                // if Yj != c
                if (document.getLabel().trim().equals(label.trim())) {
                    continue;
                }
                for (String docWord : document.getWords()) {
                    // sum up word counts
                    denumerator += document.getImprovedWordCount(docWord);
                }
            }
            // add smoothing factor
            denumerator += sumOfAlphas;

            // over all i
            for (String word : this.words) {
                double numerator = 0;
                // over all j
                for (Document document : trainingDocuments) {
                    // if Yj != c
                    if (document.getLabel().trim().equals(label.trim())) {
                        continue;
                    }
                    // sum up word counts
                    numerator += document.getImprovedWordCount(word);
                }
                // add smooting factor
                numerator += alphai;

                double theta = 1.0*numerator / 1.0*denumerator;
                this.theta.get(label).put(word, theta);
            }
        }

        HashMap<String, Double> sumOfWeights = new HashMap<>();
        System.out.println("Starting step 5");
        /**
         * Step 5 of Rennie et. al.
         * computes log of the weights
         * and also saves the sum of the logs for step 6
         */
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
        /**
         * Step 6 of Rennie et. al.
         * weight normalization $ 3.2 Weight Magnitude Errors
         */
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

    /**
     * This method performs the labeling of an unclassified
     * document according to Step 8 of Rennie et. al. (page 7)
     *
     * @param document the document which will be classified
     * @return the label of the document
     */
    public String classify(Document document) {
        String label = "";
        HashMap<String, Double> sums = new HashMap<>();
        // sums up all weights of the words in the document
        for (String c : this.label) {
            double weights = 0;
            for (String word : document.getWords()) {
                if (this.weight.get(c).containsKey(word)) {
                    weights += document.getWordCount(word) * this.weight.get(c).get(word);
                }
            }
            sums.put(c, weights);
        }
        // gets the arg min of the classes
        double minimum = Double.MAX_VALUE;
        for (String c : sums.keySet()) {
            if (sums.get(c) < minimum) {
                minimum = sums.get(c);
                label = c;
            }
        }
        return label;
    }


    public static <K extends Comparable,V extends Comparable> Map<K,V> sortByValues(Map<K,V> map){
        List<Map.Entry<K,V>> entries = new LinkedList<Map.Entry<K,V>>(map.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<K,V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return -1 * o1.getValue().compareTo(o2.getValue());
            }
        });
        //LinkedHashMap will keep the keys in the order they are inserted
        //which is currently sorted on natural ordering
        Map<K,V> sortedMap = new LinkedHashMap<K,V>();
        for(Map.Entry<K,V> entry: entries){
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
}
