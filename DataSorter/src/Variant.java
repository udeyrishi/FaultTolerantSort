/**
 * An extension interface of the {@link Operation} for operations that can be used as variants in the
 * Recovery Blocks fault tolerant algorithm implemented in the {@link RecoveryBlocksExecutor}.
 * Created by rishi on 2016-02-20.
 */
public interface Variant<T> extends Operation<T> {
    /**
     * Returns a string descriptor for this variant.
     * @return A string descriptor for this variant.
     */
    String getName();
}
