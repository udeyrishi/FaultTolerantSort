/**
 * An interface for an acceptance test for a particular {@link Variant} that can be used by the
 * {@link RecoveryBlocksExecutor}.
 * Created by rishi on 2016-02-20.
 */
public interface AcceptanceTest<T> {

    /**
     * Tests the result of a {@link Variant} for meeting the acceptance requirements.
     * @param result The result to be verified for acceptance.
     * @return Whether or not the result is acceptable.
     */
    boolean testResult(T result);
}
