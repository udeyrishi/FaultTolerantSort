/**
 * The failure exception thrown when a memory access failure occurs.
 * Created by rishi on 2016-02-20.
 */
public class MemoryAccessFailureException extends RuntimeException {
    public MemoryAccessFailureException(String message) {
        super(message);
    }

    public MemoryAccessFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public MemoryAccessFailureException(Throwable cause) {
        super(cause);
    }

    public MemoryAccessFailureException() {
        super();
    }
}
