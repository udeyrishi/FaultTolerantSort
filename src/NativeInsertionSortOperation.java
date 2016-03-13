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

import java.util.ArrayList;
import java.util.List;

/**
 * A Java {@link Variant} wrapper for sorting a list of Integers using the native insertion_sort lib.
 * A "libinsertion_sort.*" dynamic library should be present at runtime in the Java load path.
 * * = platform specific extension
 * Created by rishi on 2016-02-20.
 */
public class NativeInsertionSortOperation implements Operation<List<Integer>>, Variant<List<Integer>> {
    static {
        System.loadLibrary("insertion_sort");
    }

    private static final String VARIANT_NAME = "Insertion sort (native) backup variant";
    private final List<Integer> data;
    private final double failureProbability;

    /**
     * Creates a {@link NativeInsertionSortOperation} object.
     * @param data The list to be sorted.
     * @param failureProbability The failure probability to be passed to the native module.
     * @throws IllegalArgumentException Thrown if the probability is not between 0 and 1.
     */
    public NativeInsertionSortOperation(List<Integer> data, double failureProbability) throws IllegalArgumentException {
        if (failureProbability < 0.0 || failureProbability > 1.0) {
            throw new IllegalArgumentException("Failure probability needs to be between 0 and 1");
        }
        this.failureProbability = failureProbability;
        this.data = data;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return VARIANT_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> execute() {
        int[] dataArray = toPrimitiveArray(data);
        this.insertionSort(dataArray, failureProbability);

        return toJavaIntegerList(dataArray);
    }

    private static ArrayList<Integer> toJavaIntegerList(int[] dataArray) {
        ArrayList<Integer> list = new ArrayList<>();
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

    private native void insertionSort(int[] data, double failureProbability);
}
