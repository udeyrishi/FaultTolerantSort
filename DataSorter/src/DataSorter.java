import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataSorter {

    public static void main(String[] _args) {
        DataSorterArgs args;
        try {
            args = new DataSorterArgs(_args);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return;
        }

        List<Integer> numbers;
        try {
            numbers = FileIOUtils.readFromFile(args.inputFile, new FileIOUtils.Converter<String, Integer>() {
                @Override
                public Integer convert(String source) {
                    return Integer.parseInt(source);
                }

                @Override
                public boolean isNullEquivalent(String source) {
                    return source == null || source.trim().isEmpty();
                }
            });
        } catch (IOException e) {
            System.err.println(String.format("IOException encountered when reading from file: %s", e.getMessage()));
            return;
        }

        RandomlyFailingList<Integer> randomlyFailingNumbers;
        try {
            randomlyFailingNumbers = new RandomlyFailingList<>(numbers, args.primaryFailureProbability);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return;
        }

        Variant<List<Integer>> primaryVariant = new HeapSortOperation<>(randomlyFailingNumbers);

        Variant<List<Integer>> backupVariant;
        try {
            backupVariant = new NativeInsertionSortOperation(numbers, args.backupFailureProbability);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return;
        }

        RecoveryBlocksExecutor<List<Integer>> executive;

        try {
            // Annotation needed because of possible unsafe use of varargs. Safe here
            // Need to use this temp because annotations can only be used with variable declaration, not initialization
            @SuppressWarnings({"unchecked"})
            RecoveryBlocksExecutor<List<Integer>> temp = new RecoveryBlocksExecutor<>(args.timeLimitMilliseconds, null,
                    primaryVariant, backupVariant);
            executive = temp;
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return;
        }

        List<Integer> sorted;
        try {
            // Create a new list, because if the primary passed, sorted will be a RandomlyFailingList
            // We don't want any more failures now
            sorted = new ArrayList<>(executive.execute());
        } catch (RecoveryBlocksExecutor.RecoveryBlocksSystemFailedException e) {
            System.err.println(e.getMessage());
            return;
        }

        try {
            FileIOUtils.writeToFile(args.outputFile, sorted, new FileIOUtils.Converter<Integer, CharSequence>() {
                @Override
                public CharSequence convert(Integer source) {
                    return source.toString();
                }

                @Override
                public boolean isNullEquivalent(Integer source) {
                    return source == null;
                }
            });
        } catch (IOException e) {
            System.err.println(String.format("IOException encountered when writing to file: %s", e.getMessage()));
        }
    }

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
