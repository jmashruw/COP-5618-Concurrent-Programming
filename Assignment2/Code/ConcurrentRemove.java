import java.io.*;
import java.util.concurrent.Callable;
import java.lang.Object.*;

public class ConcurrentRemove implements Callable<Integer>{
	private Integer number;
	private ConcurrentSearcherList myremover;

	public ConcurrentRemove(Integer number, ConcurrentSearcherList myremover){
		this.number = number;
		this.myremover = myremover;
	}

	@Override
	public Integer call() throws Exception{
		System.out.println("\n I am in remove");
		Boolean result = new Boolean(myremover.remove(number));
		if(result.equals(true)) 
			System.out.println("\nNumber "+number + " removed from the singly-linkedlist");
		else
			System.out.println("\nNumber "+number + " not found in the singly-linkedlist");
	    
	    return number;
	}
}