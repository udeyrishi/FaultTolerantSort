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

import java.util.TimerTask;

/**
 * A watchdog class for preempting an executing thread after some time.
 * Based on the version by Scott Dick, instructor, ECE 422 at the University of Alberta.
 * Created by rishi on 2016-02-20.
 */
public class Watchdog extends TimerTask {
    private Thread watched;

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
    @Override
    public void run() {
        watched.stop();
    }
}
