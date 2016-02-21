/**
 * Created by rishi on 2016-02-20.
 */
public class OperationThread<T> extends Thread {
    private final Operation<T> operation;
    private T result = null;

    public OperationThread(Operation<T> operation) {
        this.operation = operation;
    }

    @Override
    public void run() {
        result = operation.execute();
    }

    public T getResult() {
        return result;
    }
}
