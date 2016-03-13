/**
 Copyright 2016 Udey Rishi
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

/**
 * A {@link Thread} that runs an {@link Operation} in an exception safe way (catches all unhandled exceptions).
 * Created by rishi on 2016-02-20.
 */
public class OperationThread<T> extends Thread {
    private final Operation<T> operation;
    private boolean isKilled = false;
    private T result = null;
    private boolean uncaughtExceptionThrown = false;
    private String uncaughtExceptionMessage = null;

    /**
     * Creates an {@link OperationThread} object.
     * @param operation The {@link Operation} to be executed.
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        try {
            result = operation.execute();
        } catch (ThreadDeath e) {
            this.isKilled = true;
            throw new ThreadDeath();
        }
    }

    /**
     * Gets the results produced by the {@link Operation}.
     * @return The results produced by the {@link Operation}. Null, if the {@link Operation} hasn't finished processing
     * yet, or if it threw an unhandled exception.
     */
    public T getResult() {
        return result;
    }

    /**
     * True if the thread was externally terminated using {@link Thread#stop()}.
     * @return If the thread has been externally terminated or not.
     */
    public boolean isKilled() {
        return this.isKilled;
    }

    /**
     * True, if the {@link Operation} threw an unhandled exception, else false.
     * @return True, if the {@link Operation} threw an unhandled exception, else false.
     */
    public boolean failed() {
        return this.uncaughtExceptionThrown;
    }

    /**
     * Returns the exception message if an unhandled exception was thrown by the {@link Operation}, else null.
     * @return The exception message if an unhandled exception was thrown by the {@link Operation}, else null.
     */
    public String getFailureMessage() {
        return this.uncaughtExceptionMessage;
    }
}
