import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The main class for the Data Sorter application.
 */
public class DataSorter {

    public static void main(String[] _args) {
        try {
            DataSorterArgs args = new DataSorterArgs(_args);
            List<Integer> numbers = FileIOUtils.readFromFile(args.inputFile, FileIOUtils.STRING_TO_INTEGER_CONVERTER);
            RandomlyFailingList<Integer> randomlyFailingNumbers = new RandomlyFailingList<>(numbers,
                    args.primaryFailureProbability);
            Variant<List<Integer>> primaryVariant = new HeapSortOperation<>(randomlyFailingNumbers);
            Variant<List<Integer>> backupVariant = new NativeInsertionSortOperation(numbers,
                    args.backupFailureProbability);

            // Annotation needed because of possible unsafe use of varargs. Safe here
            @SuppressWarnings({"unchecked"})
            RecoveryBlocksExecutor<List<Integer>> executive = new RecoveryBlocksExecutor<>(
                    args.timeLimitMilliseconds,
                    DataSorter.<Integer>createIncreasingSortAcceptanceTest(),
                    primaryVariant,
                    backupVariant);
            // Create a new list, because if the primary passed, sorted will be a RandomlyFailingList
            // We don't want any more failures now
            List<Integer> sorted = new ArrayList<>(executive.execute());
            FileIOUtils.writeToFile(args.outputFile, sorted, FileIOUtils.INTEGER_TO_STRING_CONVERTER);
        }
        catch (IOException e) {
            System.err.println(String.format("IOException encountered when reading/writing from/to file: %s",
                    e.getMessage()));
        }
        catch (IllegalArgumentException | RecoveryBlocksExecutor.RecoveryBlocksSystemFailedException e) {
            System.err.println(e.getMessage());
        }
    }

    private static <E extends Comparable<E>> AcceptanceTest<List<E>> createIncreasingSortAcceptanceTest() {
        return new AcceptanceTest<List<E>>() {
            @Override
            public boolean testResult(List<E> result) {
                E previous = null;

                for (E i : result) {
                    if (previous == null || i.compareTo(previous) >= 0) {
                        previous = i;
                    } else {
                        return false;
                    }
                }

                return true;
            }
        };
    }

    /**
     * A small data encapsulating class for the args to the program.
     */
    private static class DataSorterArgs {

        public final String inputFile;
        public final String outputFile;
        public final double primaryFailureProbability;
        public final double backupFailureProbability;
        public final long timeLimitMilliseconds;

        public DataSorterArgs(String[] args) {
            if (args.length < 5) {
                throw new IllegalArgumentException("Usage: java <program name> <input file> <output file> " +
                        "<primary variant failure probability> <backup variant failure probability> <time limit" +
                        " in ms>");
            }
            inputFile = args[0];
            outputFile = args[1];
            primaryFailureProbability = Double.parseDouble(args[2]);
            backupFailureProbability = Double.parseDouble(args[3]);
            timeLimitMilliseconds = Long.parseLong(args[4]);
        }
    }
}
