import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by dennismeyer on 17.07.14.
 */
public class Run {

    private static ArrayList<Document> trainDocuments = new ArrayList<Document>();
    private static ArrayList<Document> testDocuments = new ArrayList<Document>();

    public static void main(String[] argv) {
        createDocuments("data/trg.txt");
        for (Document document : trainDocuments) {
            document.calculateImprovedCounts(trainDocuments);
        }
    }

    public static void createDocuments(String filePath) {

        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));

            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {

                Document document = new Document(line.substring(3), line.substring(0, 1));
                if (Math.random() < 0.75) {
                    trainDocuments.add(document);
                }
                else {
                    testDocuments.add(document);
                }

                line = br.readLine();
            }
            String everything = sb.toString();



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
