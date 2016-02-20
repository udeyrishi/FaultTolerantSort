import java.io.*;
import java.lang.*;



/*
 * Created on Aug 2, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * @author dick
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/* This is a test of the JNI interface. main() sets up a sorted int array, and accepts a number to search for. This is 
 * then passed to a C native method which implements a binary search function.
 *
 */

public class Driver {

	public static void main(String[] args) {
		
		int[] buf = new int[1000];
		int i;
		for (i=0; i < 1000; i++){
			buf[i] = i;
		}
		System.out.print("Enter a number to search for: ");
		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
		String name;
		try{
			name = console.readLine();
			int target = Integer.parseInt(name);
			int result;
			MyBinarySearch bs = new MyBinarySearch();
			System.loadLibrary("binsearch");
			result = bs.binsearch(buf,target);
			System.out.print("Index is ");
			System.out.println(result+1);
				
		}
		catch (IOException e){
				System.out.println("You blew it!");
		
			}
		

	
		
	}
}
