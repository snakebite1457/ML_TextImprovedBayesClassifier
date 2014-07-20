import java.io.*;
import java.util.ArrayList;

/**
 * @author Dennis Meyer, Sebastian Brodehl
 * machine learning summer term 2014
 */
public class Run {
    private final static boolean DEBUG = false;
    private static ArrayList<Document> trainDocuments = new ArrayList<>();
    private static ArrayList<Document> testDocuments = new ArrayList<>();
    private static ArrayList<Document> classifyDocuments = new ArrayList<>();

    /**
     * This method only handles the file reading/writing
     * constructs a new classifier and tests him
     * also classifies unclassified documents
     *
     * @param argv some args
     */
    public static void main(String[] argv) {
        System.out.print("Reading and creating documents...");
        createDocuments("data/trg.txt");
        System.out.print("done!\n");

        System.out.println("We got " + trainDocuments.size() + " training sets and " + testDocuments.size() + " test sets.");

        long startTime = System.currentTimeMillis();
        System.out.print("Calculating improved counts for documents...");
        for (Document document : trainDocuments) {
            document.calculateImprovedCounts(trainDocuments);
        }
        System.out.print("done!\n");
        long endTime = System.currentTimeMillis();
        System.out.println((endTime - startTime)/1000 + "s");

        BayesTextClassifier bayesTextClassifier = new BayesTextClassifier(trainDocuments);

        int countCorrect = 0;
        startTime = System.currentTimeMillis();
        System.out.println("Testing documents...");
        for (Document document : testDocuments) {
            String label = bayesTextClassifier.classify(document);
            if (label.equals(document.getLabel())) {
                countCorrect++;
            }
        }
        System.out.print("done!\n");
        endTime = System.currentTimeMillis();
        System.out.println((endTime - startTime)/1000 + "s");
        System.out.println(countCorrect*1.0 / testDocuments.size() + " correct labeled documents.");

        System.out.println("Classifying unknown data...");
        createDocumentsForClassification("data/tst.txt");
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("data/tst_classification_" + System.currentTimeMillis()/1000 + ".txt"));
            for (Document document : classifyDocuments) {
                String label = bayesTextClassifier.classify(document);
                bw.write(label);
                bw.newLine();
            }
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("done!");
    }

    /**
     * Reads a file and extracts classified documents
     * @param filePath the file with the documents
     */
    public static void createDocuments(String filePath) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line = br.readLine();

            while (line != null) {
                String[] split = line.split("\t");
                if (split.length != 2)
                    return;
                String label = split[0];
                String text = split[1].replace('"', ' ').trim();
                Document document = new Document(text, label);
                if (DEBUG) {
                    if (trainDocuments.size() < 3000)
                        trainDocuments.add(document);
                    else
                        testDocuments.add(document);
                } else {
                    if (Math.random() < 0.86) {
                        trainDocuments.add(document);
                    } else {
                        testDocuments.add(document);
                    }
                }
                line = br.readLine();
            }
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads a file and extracts unclassified documents
     * @param filePath the file with the documents
     */
    public static void createDocumentsForClassification(String filePath) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line = br.readLine();
            while (line != null) {
                String text = line.replace('"', ' ').trim();
                Document document = new Document(text, null);
                classifyDocuments.add(document);
                line = br.readLine();
            }
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
