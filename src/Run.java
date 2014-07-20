import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    /**
     * Just compares multiple classifications
     */
    public static void getFinalClassification() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("data/tst_classification_final.txt"));

            BufferedReader c0 = new BufferedReader(new FileReader("data/tst_classification_1405851034.txt"));
            BufferedReader c1 = new BufferedReader(new FileReader("data/tst_classification_1405851126.txt"));
            BufferedReader c2 = new BufferedReader(new FileReader("data/tst_classification_1405851213.txt"));
            BufferedReader c3 = new BufferedReader(new FileReader("data/tst_classification_1405851293.txt"));
            BufferedReader c4 = new BufferedReader(new FileReader("data/tst_classification_1405851415.txt"));

            String c0Label = c0.readLine();
            String c1Label = c1.readLine();
            String c2Label = c2.readLine();
            String c3Label = c3.readLine();
            String c4Label = c4.readLine();

            HashMap<String, Integer> counts = new HashMap<>();
            while (c0Label != null) {
                counts.clear();
                if (counts.containsKey(c0Label)) {
                    int count = counts.get(c0Label) + 1;
                    counts.put(c0Label, count);
                } else
                    counts.put(c0Label, 1);

                if (counts.containsKey(c1Label)) {
                    int count = counts.get(c1Label) + 1;
                    counts.put(c1Label, count);
                } else
                    counts.put(c1Label, 1);

                if (counts.containsKey(c2Label)) {
                    int count = counts.get(c2Label) + 1;
                    counts.put(c2Label, count);
                } else
                    counts.put(c2Label, 1);

                if (counts.containsKey(c3Label)) {
                    int count = counts.get(c3Label) + 1;
                    counts.put(c3Label, count);
                } else
                    counts.put(c3Label, 1);

                if (counts.containsKey(c4Label)) {
                    int count = counts.get(c4Label) + 1;
                    counts.put(c4Label, count);
                } else
                    counts.put(c4Label, 1);

                int maxCount = -1;
                String label = "";
                for (Map.Entry<String, Integer> entry : counts.entrySet()) {
                    if (entry.getValue() > maxCount) {
                        label = entry.getKey();
                    }
                }
                if (label.equals("")) {
                    throw new Exception();
                }
                bw.write(label);
                bw.newLine();

                c0Label = c0.readLine();
                c1Label = c1.readLine();
                c2Label = c2.readLine();
                c3Label = c3.readLine();
                c4Label = c4.readLine();
            }
            bw.flush();
            bw.close();
            c0.close();
            c1.close();
            c2.close();
            c3.close();
            c4.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
