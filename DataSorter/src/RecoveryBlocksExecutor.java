import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;

/**
 * Created by rishi on 2016-02-20.
 */
public class RecoveryBlocksExecutor<T> implements Operation<T> {
    private final List<Variant<T>> variants;
    private final long variantTimeLimitMilliseconds;
    private final AcceptanceTest<T> acceptanceTest;

    // Needed because of possible unsafe use of varargs. Safe here
    @SafeVarargs
    public RecoveryBlocksExecutor(long variantTimeLimitMilliseconds,
                                  AcceptanceTest<T> acceptanceTest,
                                  final Variant<T> primaryVariant,
                                  final Variant<T>... backupVariants) {
        variants = new ArrayList<>(Arrays.asList(backupVariants));
        variants.add(0, primaryVariant);

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

    private static class VariantFailureException extends Exception {
        public VariantFailureException(Variant<?> failedVariant) {
            super(String.format("Variant '%s' failed.", failedVariant.getName()));
        }

        public VariantFailureException(Variant<?> failedVariant, String cause) {
            super(String.format("Variant '%s' failed. Cause: %s", failedVariant.getName(), cause));
        }

        public VariantFailureException(Variant<?> failedVariant, Throwable cause) {
            this(failedVariant, "Inner exception with message: " + cause.getMessage());
        }
    }

    public static class RecoveryBlocksSystemFailedException extends RuntimeException {
        public RecoveryBlocksSystemFailedException() {
            super("The result could not be obtained because all variants failed.");
        }
    }
}
