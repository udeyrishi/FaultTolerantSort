/**
 Copyright 2016 Udey Rishi
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The main class for the Data Sorter application.
 */
public class DataSorter {
    private static final AcceptanceTest<List<Integer>> SORT_ACCEPTANCE_TEST
        = new AcceptanceTest<List<Integer>>() {
            @Override
            public boolean testResult(List<Integer> result) {
                Integer previous = null;

                for (Integer i : result) {
                    if (previous == null || i.compareTo(previous) >= 0) {
                        previous = i;
                    } else {
                        return false;
                    }
                }

                return true;
            }
        };

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
                    SORT_ACCEPTANCE_TEST,
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
