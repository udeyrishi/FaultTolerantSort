import java.util.*;

/*
 * Created on Aug 6, 2004
 *
 
 */

/**
 * @author dick
 *
 
 */
public class Driver {

	public static void main(String[] args) {
		
		TestStopMethod mythread = new TestStopMethod();
		Timer t = new Timer();
		Watchdog w = new Watchdog(mythread);
		t.schedule(w, 1000);
		mythread.start();
		try {
			mythread.join();
			t.cancel();
			System.out.println("End of story");
		}
		catch (InterruptedException e){}
		
	}
}
