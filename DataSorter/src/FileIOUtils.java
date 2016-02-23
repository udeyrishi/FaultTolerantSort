import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rishi on 2016-02-21.
 */
public class FileIOUtils {

    public static <T> List<T> readFromFile(String filePath, Converter<String, T> converter) throws IOException {
        Path file = Paths.get(filePath);
        List<String> lines = Files.readAllLines(file, Charset.forName("UTF-8"));
        return convertList(lines, converter);
    }

    public static <T> void writeToFile(String outputFilePath, List<T> data, Converter<T, ? extends
            CharSequence> converter) throws IOException {

        Path file = Paths.get(outputFilePath);
        List<? extends CharSequence> dataToWrite = convertList(data, converter);
        Files.write(file, dataToWrite, Charset.forName("UTF-8"));
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

    public interface Converter<T1, T2> {
        T2 convert(T1 source);
        boolean isNullEquivalent(T1 source);
    }
}
