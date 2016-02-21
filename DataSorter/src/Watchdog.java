import java.util.TimerTask;

/**
 * Created by rishi on 2016-02-20.
 * Based on the version by Scott Dick, instructor, ECE 422 at the University of Alberta.
 */
public class Watchdog extends TimerTask {
    Thread watched;

    public Watchdog (Thread target) {
        // Constructor sets the class variable 'watched'
        watched = target;
    }

    @SuppressWarnings("deprecation")
    public void run() {
        watched.stop();
    }
}
