/**
 * An interface representing an executable object/functor.
 * Created by rishi on 2016-02-20.
 */
public interface Operation<T> {
    /**
     * Execute the operation.
     * @return The operation's results.
     */
    T execute();
}
