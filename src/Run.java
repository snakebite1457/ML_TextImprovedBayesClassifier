import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Run {

    private static ArrayList<Document> trainDocuments = new ArrayList<>();
    private static ArrayList<Document> testDocuments = new ArrayList<>();

    public static void main(String[] argv) {
        System.out.println("### Reading and creating documents: ###");
        createDocuments("data/trg.txt");
        System.out.println("### READY! ###");

        System.out.println("We got " + trainDocuments.size() + " training sets and " + testDocuments.size() + " test sets.");

        long startTime = System.currentTimeMillis();
        System.out.println("### Calculating improved counts for documents: ###");
        int allTrainingSets = trainDocuments.size();
        int current = 0;
        for (Document document : trainDocuments) {
            document.calculateImprovedCounts(trainDocuments);
            System.out.println(++current + "/" + allTrainingSets);
        }
        System.out.println("### DONE ###");
        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime + "ms");

        BayesTextClassifier bayesTextClassifier = new BayesTextClassifier(trainDocuments);


        int countCorrect = 0;
        startTime = System.currentTimeMillis();
        System.out.println("### Testing documents: ###");
        for (Document document : testDocuments) {
            String label = bayesTextClassifier.classify(document);
            if (label.equals(document.getLabel())) {
                countCorrect++;
            }
        }
        System.out.println("### DONE ###");
        endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime + "ms");
        System.out.println(countCorrect / testDocuments.size() + " correct labeled documents.");
    }

    public static void createDocuments(String filePath) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line = br.readLine();
            int allDocuments = 0;
            while (line != null) {
                allDocuments++;
                line = br.readLine();
            }
            br = new BufferedReader(new FileReader(filePath));
            line = br.readLine();
            int currentDoc = 0;
            while (line != null) {
                Document document = new Document(line.substring(3), line.substring(0, 1));
                if (Math.random() < 0.75) {
                    trainDocuments.add(document);
                } else {
                    testDocuments.add(document);
                }
                System.out.println(++currentDoc + "/" + allDocuments);
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
