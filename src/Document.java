import java.util.*;
import java.util.regex.Pattern;

/**
 * This class is in charge of a document.
 * It calculates the counts for the words and stores
 * them into a data structure. If this is a training
 * document, the label is already set to a class.
 * Otherwise one can use the BayesTextClassifier
 * to classify this document.
 *
 * This class is designed for the BayesTextClassifier.
 *
 * @author Dennis Meyer, Sebastian Brodehl
 * machine learning summer term 2014
 */
public class Document {
    private HashMap<String, Integer> wordsWithCounts;
    private HashMap<String, Double> wordsWithImprovedCounts;
    private String _label;

    private Set<String> stopwords = new HashSet<>(Arrays.asList(new String[]{
            "a", "about", "above", "above", "across", "after", "afterwards", "again", "against", "all", "almost", "alone", "along", "already", "also","although","always","am","among", "amongst", "amoungst", "amount",  "an", "and", "another", "any","anyhow","anyone","anything","anyway", "anywhere", "are", "around", "as",  "at", "back","be","became", "because","become","becomes", "becoming", "been", "before", "beforehand", "behind", "being", "below", "beside", "besides", "between", "beyond", "bill", "both", "bottom","but", "by", "call", "can", "cannot", "cant", "co", "con", "could", "couldnt", "cry", "de", "describe", "detail", "do", "done", "down", "due", "during", "each", "eg", "eight", "either", "eleven","else", "elsewhere", "empty", "enough", "etc", "even", "ever", "every", "everyone", "everything", "everywhere", "except", "few", "fifteen", "fify", "fill", "find", "fire", "first", "five", "for", "former", "formerly", "forty", "found", "four", "from", "front", "full", "further", "get", "give", "go", "had", "has", "hasnt", "have", "he", "hence", "her", "here", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "him", "himself", "his", "how", "however", "hundred", "ie", "if", "in", "inc", "indeed", "interest", "into", "is", "it", "its", "itself", "keep", "last", "latter", "latterly", "least", "less", "ltd", "made", "many", "may", "me", "meanwhile", "might", "mill", "mine", "more", "moreover", "most", "mostly", "move", "much", "must", "my", "myself", "name", "namely", "neither", "never", "nevertheless", "next", "nine", "no", "nobody", "none", "noone", "nor", "not", "nothing", "now", "nowhere", "of", "off", "often", "on", "once", "one", "only", "onto", "or", "other", "others", "otherwise", "our", "ours", "ourselves", "out", "over", "own","part", "per", "perhaps", "please", "put", "rather", "re", "same", "see", "seem", "seemed", "seeming", "seems", "serious", "several", "she", "should", "show", "side", "since", "sincere", "six", "sixty", "so", "some", "somehow", "someone", "something", "sometime", "sometimes", "somewhere", "still", "such", "system", "take", "ten", "than", "that", "the", "their", "them", "themselves", "then", "thence", "there", "thereafter", "thereby", "therefore", "therein", "thereupon", "these", "they", "thickv", "thin", "third", "this", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "top", "toward", "towards", "twelve", "twenty", "two", "un", "under", "until", "up", "upon", "us", "very", "via", "was", "we", "well", "were", "what", "whatever", "when", "whence", "whenever", "where", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whoever", "whole", "whom", "whose", "why", "will", "with", "within", "without", "would", "yet", "you", "your", "yours", "yourself", "yourselves", "the"
    }));


    private boolean TF = true; // §4.1 TF transform
    private boolean IDF = true; // §4.2 IDF transform
    private boolean LN = true; // §4.3 length norm

    public Document(String text, String label) {
        this.wordsWithCounts = new HashMap<>();
        this.wordsWithImprovedCounts = new HashMap<>();
        this._label = label;
        this.transformTextIntoWordsWithCounts(text);
    }

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
     * @param word the word the count is returned for
     * @return double value 'improved count'
     */
    public double getImprovedWordCount(String word){
        if (!this.wordsWithImprovedCounts.containsKey(word.trim())) {
            return 0.0;
        }
        return this.wordsWithImprovedCounts.get(word);
    }

    /**
     * Gets the word count for a given word.
     * Returns zero if the word isn't in the text.
     *
     * @param word the word the count is returned for
     * @return double value 'simple count'
     */
    public int getWordCount(String word) {
        if (!this.wordsWithCounts.containsKey(word.trim())) {
            return 0;
        }
        return this.wordsWithCounts.get(word);
    }

    /**
     * Checks of the given word occurs in this document
     *
     * @param word The word which will be checked
     * @return True if it occurs, false otherwise
     */
    public boolean containsWord(String word) {
        return this.wordsWithCounts.containsKey(word);
    }

    /**
     * Splits the text of the document to single words
     * by white spaces and strips the first and the
     * last double quotes.
     *
     * @Improvement Didn't insert words if the length is
     * smaller than 4.
     *
     * @param text The text which will be splitted
     */
    private void transformTextIntoWordsWithCounts(String text) {
        Pattern numberRE = Pattern.compile("[0-9]+");
        String[] seperatedText = text.split(" ");
        for(String s : seperatedText) {
            String item = s.trim();
            if (item.length() < 1 || numberRE.matcher(item).matches()  || stopwords.contains(item)) {
                continue;
            }
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
            double improvedCount = entry.getValue();
            // First improvement for TWCNB, compare §4.1 TF trasnform
            if (TF) {
                improvedCount = Math.log(entry.getValue() + 1);
            }
            // Second improvement for TWCNB, compare §4.2 IDF transform
            if (IDF) {
                improvedCount = improvedCount * (Math.log(allDocuments.size() / idfTransformHelper(allDocuments, entry.getKey())));
            }
            this.wordsWithImprovedCounts.put(entry.getKey(), improvedCount);
        }
        if (LN) {
            double lenghtNorm = this.lengthNormHelper();
            for(Map.Entry<String, Integer> entry : this.wordsWithCounts.entrySet()) {
                double improvedCount = this.wordsWithImprovedCounts.get(entry.getKey());
                // Third improvement for TWCNB, compare §4.3 length norm
                improvedCount = 1.0*improvedCount / Math.sqrt(lenghtNorm);
                this.wordsWithImprovedCounts.put(entry.getKey(), improvedCount);
            }
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
            returnValue += (entry.getValue() * entry.getValue());
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
