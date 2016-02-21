import java.util.ArrayList;
import java.util.List;

/**
 * Created by rishi on 2016-02-20.
 */
public class NativeInsertionSortDriver implements Operation<List<Integer>>, Variant<List<Integer>> {
    private static final String VARIANT_NAME = "Insertion sort (native) backup variant";
    private final List<Integer> data;
    private final double failureProbability;

    public NativeInsertionSortDriver(List<Integer> data, double failureProbability) {
        if (failureProbability < 0.0 || failureProbability > 1.0) {
            throw new IllegalArgumentException("Failure probability needs to be between 0 and 1");
        }
        this.failureProbability = failureProbability;
        this.data = data;
    }
    @Override
    public String getVariantName() {
        return VARIANT_NAME;
    }

    @Override
    public List<Integer> execute() {
        NativeInsertionSortLib nativeInsertionSort = new NativeInsertionSortLib();
        System.loadLibrary("insertion_sort");

        int[] dataArray = toPrimitiveArray(data);
        nativeInsertionSort.insertion_sort(dataArray, failureProbability);

        return toJavaIntegerList(dataArray);
    }

    private static ArrayList<Integer> toJavaIntegerList(int[] dataArray) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i : dataArray) {
            list.add(i);
        }
        return list;
    }

    private static int[] toPrimitiveArray(List<Integer> data) {
        int[] dataArray = new int[data.size()];
        for (int i = 0; i < data.size(); ++i) {
            dataArray[i] = data.get(i);
        }

        return dataArray;
    }
}
