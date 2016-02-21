import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;

/**
 * Created by rishi on 2016-02-20.
 */
public class RecoveryBlocksExecutor<T> implements Operation<T> {
    private final ArrayList<Variant<T>> variants;
    private final long variantTimeLimitMilliseconds;
    private final AcceptanceTest<T> acceptanceTest;

    public RecoveryBlocksExecutor(long variantTimeLimitMilliseconds,
                                  AcceptanceTest<T> acceptanceTest,
                                  final Variant<T> primaryVariant,
                                  final Variant<T>... backupVariants) {
        variants = new ArrayList<Variant<T>>() {
            {
                add(primaryVariant);
                addAll(Arrays.asList(backupVariants));
            }
        };

        this.variantTimeLimitMilliseconds = variantTimeLimitMilliseconds;
        this.acceptanceTest = acceptanceTest;
    }


    @Override
    public T execute() {
        // Executive thread
        for (Variant<T> variant : variants) {
            Timer timer = new Timer();
            OperationThread<T> variantThread = new OperationThread<>(variant);
            Watchdog watchdog = new Watchdog(variantThread);
            timer.schedule(watchdog, variantTimeLimitMilliseconds);
            variantThread.start();
            try {
                try {
                    variantThread.join();
                    timer.cancel();

                    // Adjudicator
                    T result = variantThread.getResult();
                    if (acceptanceTest.testResult(result)) {
                        return result;
                    } else {
                        // Local exception
                        throw new VariantFailureException(variant, "Acceptance Test failed.");
                    }
                } catch (Exception e) {
                    // Local exception. Includes Acceptance test failures, timeouts, or other failures internal to
                    // the variant.
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

    private class VariantFailureException extends Exception {
        public VariantFailureException(Variant<T> failedVariant) {
            super(String.format("Variant '%s' failed.", failedVariant.getVariantName()));
        }

        public VariantFailureException(Variant<T> failedVariant, String cause) {
            super(String.format("Variant '%s' failed. Cause: %s", failedVariant.getVariantName(), cause));
        }

        public VariantFailureException(Variant<T> failedVariant, Throwable cause) {
            this(failedVariant, cause.getMessage());
        }
    }

    public static class RecoveryBlocksSystemFailedException extends RuntimeException {
        public RecoveryBlocksSystemFailedException() {
            super("The result could not be obtained because all variants failed.");
        }
    }
}
