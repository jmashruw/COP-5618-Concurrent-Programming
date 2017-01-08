import java.io.*;
import java.util.concurrent.Callable;
import java.lang.Object.*;

public class ConcurrentInsert implements Callable<Integer>{
	private Integer number;
	private ConcurrentSearcherList myinserter;

	public ConcurrentInsert(Integer number, ConcurrentSearcherList myinserter){
		this.number = number;
		this.myinserter = myinserter;
	}

	@Override
	public Integer call() throws Exception{
		System.out.println("\n I am in insert");
		myinserter.insert(number);
		System.out.println("\nNumber "+number + " inserted in the singly-linkedlist");
		return number;
	}
}
