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
