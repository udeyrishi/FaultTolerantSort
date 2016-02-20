/*
 * Created on Aug 6, 2004
 * Tests the stop method; this is the thread I'm gonna halt using a watchdog timer
 
 */

/**
 * @author dick
 *
 
 */
public class TestStopMethod extends Thread {
	
	public void run(){
		
		try{
			for (;;);
		}
		
		catch (ThreadDeath td){
			System.out.println("I'm dead!!!");
			throw new ThreadDeath();
		}
		
	}

}
