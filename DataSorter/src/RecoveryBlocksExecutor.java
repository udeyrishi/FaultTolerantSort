import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;

/**
 * A fault tolerant executive and adjudicator that can use multiple {@link Variant} objects to perform a computation
 * and obtain the results.
 *
 * The failure may be because of an unhandled exception within the execution of a variant, or the run time
 * exceeds a specified maximum run time, or the result doesn't pass the {@code acceptanceTest}, if any.
 *
 * Uses the {@link OperationThread} to run the variants in a multithreaded manner.
 * Uses {@link AcceptanceTest} to validate the results of the variants.
 * Uses {@link Watchdog} for preemption if needed.
 * Created by rishi on 2016-02-20.
 */
public class RecoveryBlocksExecutor<T> implements Operation<T> {
    private final List<Variant<T>> variants;
    private final long variantTimeLimitMilliseconds;
    private final AcceptanceTest<T> acceptanceTest;

    /**
     * Creates a new instance of {@link RecoveryBlocksExecutor}.
     * @param variantTimeLimitMilliseconds The maximum time that a {@link Variant} be allowed to run, before
     *                                     preempting it.
     * @param acceptanceTest The {@link AcceptanceTest} for validating the result for correctness. Can be null,
     *                       if no validation is needed.
     * @param primaryVariant The primary {@link Variant} for producing the results.
     * @param backupVariants 0 or more backup {@link Variant}s that will be used if the {@code primaryVariant} fails.
     * @throws IllegalArgumentException Thrown if {@code variantTimeLimitMilliseconds} is negative.
     */
    // Needed because of possible unsafe use of varargs. Safe here
    @SafeVarargs
    public RecoveryBlocksExecutor(long variantTimeLimitMilliseconds,
                                  AcceptanceTest<T> acceptanceTest,
                                  final Variant<T> primaryVariant,
                                  final Variant<T>... backupVariants) throws IllegalArgumentException {

        if (variantTimeLimitMilliseconds < 0) {
            throw new IllegalArgumentException("The time limit for variants can't be negative.");
        }
        variants = new ArrayList<>(Arrays.asList(backupVariants));
        variants.add(0, primaryVariant);

        this.variantTimeLimitMilliseconds = variantTimeLimitMilliseconds;
        this.acceptanceTest = acceptanceTest;
    }


    /**
     * Executes all the {@link Variant}s, starting with the primary one, until one gives a failure free
     * acceptable result. Prints the success and erroneous statuses to stdout and stderr (variant failures),
     * respectively.
     *
     * @return The successful result.
     * @throws RecoveryBlocksSystemFailedException Thrown if no {@link Variant} succeeded.
     */
    @Override
    public T execute() throws RecoveryBlocksSystemFailedException {
        // Executive thread
        for (Variant<T> variant : variants) {
            Timer timer = new Timer();
            OperationThread<T> variantThread = new OperationThread<>(variant);
            Watchdog watchdog = new Watchdog(variantThread);
            timer.schedule(watchdog, variantTimeLimitMilliseconds);
            try {
                try {
                    variantThread.start();
                    variantThread.join();
                    timer.cancel();

                    // Adjudicator
                    if (variantThread.isKilled()) {
                        throw new VariantFailureException(variant, "Timed out.");
                    }

                    if (variantThread.failed()) {
                        // If there was an unhandled exception in the operation
                        throw new VariantFailureException(variant, variantThread.getFailureMessage());
                    }

                    T result = variantThread.getResult();
                    if (acceptanceTest == null || acceptanceTest.testResult(result)) {
                        System.out.println(String.format("Variant '%s' successfully produced acceptable results.",
                                variant.getName()));
                        return result;
                    } else {
                        // Local exception
                        throw new VariantFailureException(variant, "Acceptance Test failed.");
                    }
                } catch (VariantFailureException e) {
                    throw e;
                } catch (Exception e) {
                    // Something totally unhandled
                    throw new VariantFailureException(variant, e);
                }
            } catch (VariantFailureException e) {
                // Print the local failure report
                System.err.println(e.getMessage());
            }
        }

        // Global exception
        throw new RecoveryBlocksSystemFailedException();
    }

    /**
     * The local failure exception thrown every time a {@link Variant} fails.
     */
    private static class VariantFailureException extends Exception {
        public VariantFailureException(Variant<?> failedVariant, String cause) {
            super(String.format("Variant '%s' failed. Cause: %s", failedVariant.getName(), cause));
        }

        public VariantFailureException(Variant<?> failedVariant, Throwable cause) {
            this(failedVariant, "Inner exception with message: " + cause.getMessage());
        }
    }

    /**
     * The exception thrown if the {@link RecoveryBlocksExecutor} failed to get an acceptable result from all
     * the {@link Variant}s.
     */
    public static class RecoveryBlocksSystemFailedException extends RuntimeException {
        public RecoveryBlocksSystemFailedException() {
            super("The result could not be obtained because all variants failed.");
        }
    }
}
