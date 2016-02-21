/**
 * Created by rishi on 2016-02-20.
 */
public class OperationThread<T> extends Thread {
    private final Operation<T> operation;
    private boolean isKilled = false;
    private T result = null;
    private boolean uncaughtExceptionThrown = false;
    private String uncaughtExceptionMessage = null;

    public OperationThread(Operation<T> operation) {
        this.operation = operation;
        this.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                uncaughtExceptionThrown = true;
                uncaughtExceptionMessage = e.getMessage();
            }
        });
    }

    @Override
    public void run() {
        try {
            result = operation.execute();
        } catch (ThreadDeath e) {
            this.isKilled = true;
            throw new ThreadDeath();
        }
    }

    public T getResult() {
        return result;
    }

    public boolean isKilled() {
        return this.isKilled;
    }

    public boolean failed() {
        return this.uncaughtExceptionThrown;
    }

    public String getFailureMessage() {
        return this.uncaughtExceptionMessage;
    }
}
