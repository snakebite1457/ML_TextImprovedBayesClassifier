import java.util.HashMap;

/**
 * @author Dennis Meyer, Sebastian Brodehl
 *
 */
public class BayesTextClassifier {

    // Stores all distinct word from all
    // training documents with there weights for each label.
    private HashMap<String, HashMap<String, Integer>> allWordsWithWeightsForEachLabel;


    public BayesTextClassifier() {
        this.allWordsWithWeightsForEachLabel = new HashMap<String, HashMap<String, Integer>>();



        //TODO: Implement step 4 to 6 from paper (WEIGHTS)
        //TODO: Implement a classify method which works like step 8
    }
}
