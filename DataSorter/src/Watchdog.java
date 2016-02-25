import java.util.TimerTask;

/**
 * A watchdog class for preempting an executing thread after some time.
 * Based on the version by Scott Dick, instructor, ECE 422 at the University of Alberta.
 * Created by rishi on 2016-02-20.
 */
public class Watchdog extends TimerTask {
    Thread watched;

    /**
     * Creates an instance of the {@link Watchdog}.
     * @param target The {@link Thread} to be monitored.
     */
    public Watchdog (Thread target) {
        // Constructor sets the class variable 'watched'
        watched = target;
    }

    /**
     * Kills the monitored thread.
     */
    @SuppressWarnings("deprecation")
    public void run() {
        watched.stop();
    }
}
