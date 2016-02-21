import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
            numbers = readFromFile(args.inputFile, new Converter<String, Integer>() {
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

        RandomlyFailingList<Integer> randomlyFailingNumbers = new RandomlyFailingList<>(numbers,
                                                                        args.primaryFailureProbability);
        Variant<List<Integer>> primaryVariant = new HeapSortOperation<>(randomlyFailingNumbers);
        Variant<List<Integer>> backupVariant = new NativeInsertionSortOperation(numbers,
                                                                                args.backupFailureProbability);

        // Needed because of possible unsafe use of varargs. Safe here
        @SuppressWarnings({"unchecked"})
        RecoveryBlocksExecutor<List<Integer>> executive
                = new RecoveryBlocksExecutor<>(args.timeLimitMilliseconds, null, primaryVariant, backupVariant);

        List<Integer> sorted;
        try {
            sorted = executive.execute();
        } catch (RecoveryBlocksExecutor.RecoveryBlocksSystemFailedException e) {
            System.err.println(e.getMessage());
            return;
        }

        try {
            writeToFile(args.outputFile, sorted, new Converter<Integer, CharSequence>() {
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

    private static <T> List<T> readFromFile(String filePath, Converter<String, T> converter) throws IOException {
        Path file = Paths.get(filePath);
        List<String> lines = Files.readAllLines(file, Charset.forName("UTF-8"));
        return convertList(lines, converter);
    }

    private static <T> void writeToFile(String outputFilePath, List<T> data, Converter<T, ? extends
                                        CharSequence> converter) throws IOException {

        Path file = Paths.get(outputFilePath);
        List<? extends CharSequence> dataToWrite = convertList(data, converter);
        Files.write(file, dataToWrite, Charset.forName("UTF-8"));
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

    private interface Converter<T1, T2> {
        T2 convert(T1 source);
        boolean isNullEquivalent(T1 source);
    }

    private static <T1, T2> List<T2> convertList(List<T1> src, Converter<T1, T2> converter) {
        ArrayList<T2> output = new ArrayList<>();
        for (T1 i : src) {
            if (!converter.isNullEquivalent(i)) {
                output.add(converter.convert(i));
            }
        }
        return output;
    }
}
