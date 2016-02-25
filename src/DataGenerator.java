import java.io.IOException;
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

        List<Integer> randomNumbers = getRandomNumbers(numValues);
        try {
            FileIOUtils.writeToFile(outputFilePath, randomNumbers, FileIOUtils.INTEGER_TO_STRING_CONVERTER);
        } catch (IOException e) {
            System.err.println(String.format("IOException encountered when writing to file: %s", e.getMessage()));
        }
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
