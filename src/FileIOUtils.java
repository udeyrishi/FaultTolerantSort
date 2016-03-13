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
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * A collection of handy static I/O utils functions and objects.
 * Created by rishi on 2016-02-21.
 */
public class FileIOUtils {

    /**
     * Reads the contents from a file line by line, and creates a list containing elements corresponding to each
     * line. The elements in the list are converted from the lines using the {@code converter}.
     * The null equivalent objects are ignored. See: {@link Converter#isNullEquivalent(Object)}.
     * @param filePath The path to the file.
     * @param converter A {@link Converter<String, T>} to be used for converting the line (String) to the result's
     *                  element.
     * @param <T> The type of elements in the output list.
     * @return The generated data list from the file.
     * @throws IOException Thrown if the I/O operations to the file fail.
     */
    public static <T> List<T> readFromFile(String filePath, Converter<String, T> converter) throws IOException {
        Path file = Paths.get(filePath);
        List<String> lines = Files.readAllLines(file, Charset.forName("UTF-8"));
        return convertList(lines, converter);
    }

    /**
     * Writes the {@code data}'s elements to a file, by converting the elements to strings using the
     * {@code converter}.
     * The null equivalent objects are ignored. See: {@link Converter#isNullEquivalent(Object)}.
     * @param outputFilePath The path to the file.
     * @param data The list containing the elements to be written out.
     * @param converter A {@link Converter<T, ? extends CharSequence>} that will be used for converting the
     *                  list elements to a file-writable {@link CharSequence}.
     * @param <T> The data type of the list elements.
     * @throws IOException Thrown if the I/O operations to the file fail.
     */
    public static <T> void writeToFile(String outputFilePath, List<T> data, Converter<T, ? extends
            CharSequence> converter) throws IOException {

        Path file = Paths.get(outputFilePath);
        List<? extends CharSequence> dataToWrite = convertList(data, converter);
        Files.write(file, dataToWrite, Charset.forName("UTF-8"));
    }

    /**
     * Converts the list by mapping each element to another using the converter. Ignores the null equivalent
     * objects.
     * @param src The source list.
     * @param converter The {@link Converter} to be used.
     * @param <T1> The data type of the elements in the source.
     * @param <T2> The data type of the elements in the result.
     * @return The mapped list.
     */
    private static <T1, T2> List<T2> convertList(List<T1> src, Converter<T1, T2> converter) {
        ArrayList<T2> output = new ArrayList<>();
        for (T1 i : src) {
            if (!converter.isNullEquivalent(i)) {
                output.add(converter.convert(i));
            }
        }
        return output;
    }

    /**
     * A {@link Converter<Integer, String>} for parsing {@link String} objects to {@link Integer}.
     */
    public static final FileIOUtils.Converter<Integer, String> INTEGER_TO_STRING_CONVERTER
            = new FileIOUtils.Converter<Integer, String>() {
        @Override
        public String convert(Integer source) {
            return source.toString();
        }

        @Override
        public boolean isNullEquivalent(Integer source) {
            return source == null;
        }
    };

    /**
     * A {@link Converter<String, Integer>} for parsing {@link Integer} objects to {@link String}.
     */
    public static final FileIOUtils.Converter<String, Integer> STRING_TO_INTEGER_CONVERTER
            = new FileIOUtils.Converter<String, Integer>() {
        @Override
        public Integer convert(String source) {
            return Integer.parseInt(source);
        }

        @Override
        public boolean isNullEquivalent(String source) {
            return source == null || source.trim().isEmpty();
        }
    };

    /**
     * An interface for an object that can convert objects from 1 type to another.
     * @param <T1> The source object type.
     * @param <T2> The destination object type.
     */
    public interface Converter<T1, T2> {
        /**
         * Converts an object form {@link T1} to {@link T2}.
         * @param source The source object.
         * @return The converted object.
         */
        T2 convert(T1 source);

        /**
         * Can be used for checking if the object is useless/null for the type {@link T1}. Can be used for checking
         * if the object can be ignored.
         * @param source The object to be verified.
         * @return True if the object is null equivalent, else false.
         */
        boolean isNullEquivalent(T1 source);
    }
}
