import java.util.List;

/**
 * Created by rishi on 2016-02-20.
 */
public class HeapSortOperation<T extends Comparable<T>> implements Operation<List<T>>, Variant<List<T>> {
    private static final String VARIANT_NAME = "Heap sort primary variant";
    private final List<T> data;

    public HeapSortOperation(List<T> data) {
        this.data = data;
    }

    @Override
    public String getVariantName() {
        return VARIANT_NAME;
    }

    @Override
    public List<T> execute() {
        buildHeap();
        for (int i = data.size() - 1; i > 0; --i) {
            swap(0, i);
            heapify(0, i - 1);
        }
        return data;
    }

    private void buildHeap() {
        for (int i = data.size()/2 - 1; i >= 0; --i) {
            heapify(i, data.size() - 1);
        }
    }

    private void heapify(int root, int end) {
        int numberOfChildren = numberOfChildrenOfNode(root, end);
        if (numberOfChildren == 0) {
            // nothing to do
            return;
        }

        int maxChildIndex;

        switch (numberOfChildren) {
            case 1:
                maxChildIndex = leftChildIndex(root);
                break;
            case 2:
                maxChildIndex = compare(leftChildIndex(root), rightChildIndex(root)) > 0 ? leftChildIndex(root) :
                                                                                      rightChildIndex(root);
                break;
            default:
                throw new RuntimeException("Developer note: Unexpected number of children. Bug in the code.");
        }

        if (compare(maxChildIndex, root) > 0) {
            swap(maxChildIndex, root);
            heapify(maxChildIndex, end);
        }
    }

    private int numberOfChildrenOfNode(int nodeIndex, int endIndex) {
        if (rightChildIndex(nodeIndex) <= endIndex) {
            return 2;
        } else if (leftChildIndex(nodeIndex) <= endIndex) {
            return 1;
        } else {
            return 0;
        }
    }

    private int leftChildIndex(int parentIndex) {
        return 2*parentIndex + 1;
    }

    private int rightChildIndex(int parentIndex) {
        return 2*parentIndex + 2;
    }

    private void swap(int i, int j) {
        T temp = data.set(i, data.get(j));
        data.set(j, temp);
    }

    private int compare(int firstIndex, int secondIndex) {
        return data.get(firstIndex).compareTo(data.get(secondIndex));
    }
}
