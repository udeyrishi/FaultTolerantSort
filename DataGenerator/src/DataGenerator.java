import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataGenerator {

    public static void main(String[] args) {
	    if (args.length < 2) {
            System.err.println("Error: Please pass the output filepath and number of integer values to be generated "
                                + "as args.");
            System.err.println("Usage: java <program name> <path to output file> <number of random ints>");
            return;
        }

        String outputFilePath = args[0];
        int numValues = Integer.parseInt(args[1]);

        if (numValues < 0) {
            System.err.println("Error: Can't produce a negative number of random ints.");
            return;
        }

        List<String> randomNumbers = convertToListOfStrings(getRandomNumbers(numValues));
        try {
            writeToFile(outputFilePath, randomNumbers);
        } catch (IOException e) {
            System.err.println(String.format("IOException encountered when writing to file: %s", e.getMessage()));
        }
    }

    private static List<String> convertToListOfStrings(Iterable<?> list) {
        ArrayList<String> stringList = new ArrayList<>();
        for (Object i : list) {
            stringList.add(i.toString());
        }
        return stringList;
    }

    private static void writeToFile(String outputFilePath,
                                    Iterable<? extends CharSequence> randomNumbers) throws IOException {
        Path file = Paths.get(outputFilePath);
        Files.write(file, randomNumbers, Charset.forName("UTF-8"));
    }

    private static List<Integer> getRandomNumbers(int numValues) {
        Random rand = new Random();
        ArrayList<Integer> randomNumbers = new ArrayList<>();

        for (int i = 0; i < numValues; ++i) {
            randomNumbers.add(rand.nextInt());
        }

        return randomNumbers;
    }
}
