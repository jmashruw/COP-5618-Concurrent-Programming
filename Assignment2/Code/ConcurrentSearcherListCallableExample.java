import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

public class ConcurrentSearcherListCallableExample{
	public static void main(String[] args){
		  ConcurrentSearcherList temp = new ConcurrentSearcherList();
		  
		  ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
           
          List<Future<Integer>> resultList = new ArrayList<>();
           
          Random random = new Random();
           
          /*insert 20 integers*/
          for (int i=0; i<10; i++)
          {
              Integer number = random.nextInt(10);
              ConcurrentInsert k  = new ConcurrentInsert(number, temp);
              Future<Integer> result = executor.submit(k);
              resultList.add(result);	
          }

          for (int i=0; i<10; i++)
          {
              Integer number = random.nextInt(10);
              if(i%2 == 0){
              		ConcurrentSearch k  = new ConcurrentSearch(number, temp);
              		Future<Integer> result = executor.submit(k);
              		resultList.add(result);
              	}
              else {
              		ConcurrentRemove k  = new ConcurrentRemove(number, temp);
              		Future<Integer> result = executor.submit(k);
              		resultList.add(result);
              	}			

          }
           
          for(Future<Integer> future : resultList)
          {
                try
                {
                    System.out.println("Future result is - " + " - " + future.get() + "; And Task done is " + future.isDone());
                } 
                catch (InterruptedException | ExecutionException e) 
                {
                    e.printStackTrace();
                }
            }
            //shut down the executor service now
            executor.shutdown();
	}
}




