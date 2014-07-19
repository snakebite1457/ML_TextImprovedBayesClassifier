import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Run {
    private static ArrayList<Document> trainDocuments = new ArrayList<>();
    private static ArrayList<Document> testDocuments = new ArrayList<>();

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
            System.out.println(label + "/" + document.getLabel());
            if (label.equals(document.getLabel())) {
                countCorrect++;
            }
        }
        System.out.print("done!\n");
        endTime = System.currentTimeMillis();
        System.out.println((endTime - startTime)/1000 + "s");
        System.out.println(countCorrect*1.0 / testDocuments.size() + " correct labeled documents.");
    }

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
                if (Math.random() < 0.75) {
                    trainDocuments.add(document);
                } else {
                    testDocuments.add(document);
                }
                line = br.readLine();
            }
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
