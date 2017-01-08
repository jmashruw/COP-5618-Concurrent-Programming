import java.io.*;
import java.util.concurrent.Callable;
import java.lang.Object.*;

public class ConcurrentSearch implements Callable<Integer>{
	private Integer number;
	private ConcurrentSearcherList mysearcher;

	public ConcurrentSearch(Integer number, ConcurrentSearcherList mysearcher){
		this.number = number;
		this.mysearcher = mysearcher;
	}

	@Override
	public Integer call() throws Exception{
		System.out.println("\n I am in search");
		Boolean result = new Boolean(mysearcher.search(number));
		if(result.equals(true)) 
			System.out.println("\nNumber "+number + " found in the singly-linkedlist");
		else
			System.out.println("\nNumber "+number + " not found in the singly-linkedlist");
	    return number;
	}
}