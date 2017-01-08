import java.io.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class ConcurrentSearcherList<T> {
    
    /*
     * Three kinds of threads share access to a singly-linked list:
     * searchers, inserters and deleters. Searchers merely examine the list;
     * hence they can execute concurrently with each other. Inserters add
     * new items to the front of the list; insertions must be mutually exclusive
     * to preclude two inserters from inserting new items at about
     * the same time. However, one insert can proceed in parallel with
     * any number of searches. Finally, deleters remove items from anywhere
     * in the list. At most one deleter process can access the list at
     * a time, and deletion must also be mutually exclusive with searches
     * and insertions.
     *
     * Make sure that there are no data races between concurrent inserters and searchers!
     */

	private int noOfSearch; /* counter to track number of current search operations */
	private int noOfInsert; /* counter to track number of current insert operations */
	private int noOfRemove; /* counter to track number of current remove operations */

	private final ReentrantLock myLock; /* Lock object to synchronize access */

    private final Condition searchCond; /* noOfRemove > 0 */
    private final Condition insertCond; /* noOfInsert > 0 && noOfRemove > 0 */
    private final Condition removeCond; /* noOfSearch > 0  && noOfInsert > 0 && noOfRemove > 0 */
    
	private static class Node<T>{
		final T item;
		Node<T> next;
		Node(T item, Node<T> next){
			this.item = item;
			this.next = next;
		}
	}

	private volatile Node<T> first; 
	
	public ConcurrentSearcherList() {
		first = null;
		noOfSearch = 0;
		noOfInsert = 0;
		noOfRemove = 0; 
		myLock = new ReentrantLock();
		searchCond = myLock.newCondition();
		insertCond = myLock.newCondition();
		removeCond = myLock.newCondition();
	}

    /**
     * Acquire the lock to insert if there are no concurrent insert or removal
     *
     * Precondition: noOfInsert == 0 && noOfRemove == 0
     *
     * @throws InterruptedException
     */
	private void start_insert() throws InterruptedException{
		myLock.lock();
		try{
			while(noOfInsert > 0 || noOfRemove > 0){
				insertCond.await();
			}
			noOfInsert++ ;
		}
		finally{
			myLock.unlock();
		}
    }
    
    /**
     * Release the lock and signal all waiting insert and remove operations
     */
    private void end_insert(){
        myLock.lock();
		try{
			noOfInsert-- ;
			if(noOfInsert == 0){
        		insertCond.signalAll();
        		removeCond.signalAll();
        	}
		}
		finally{
			myLock.unlock();
		}
	}

    /**
     * Acquire the lock to search if there is no concurrent removal
     *
     * Precondition: noOfRemove == 0
     *
     * @throws InterruptedException
     */
	private void start_search() throws InterruptedException{
		myLock.lock();
		try{
			while(noOfRemove > 0 )
			{
				searchCond.await();
			}
			noOfSearch++;
		}
		finally{
			myLock.unlock();
		}
	}

    /**
     * Release the lock and signal all waiting remove operations
     */
	private void end_search() throws InterruptedException{
        myLock.lock();
        try{
        	noOfSearch--;
        	if(noOfSearch == 0){
        		removeCond.signalAll();
        	}
        }
        finally{
        	myLock.unlock();
        }
    }
    
    /**
     * Acquire the lock to remove if there are no concurrent insert, search and removal
     *
     * Precondition: noOfSearch == 0  && noOfInsert == 0 && noOfRemove == 0
     *
     * @throws InterruptedException
     */
	private void start_remove() throws InterruptedException{
		myLock.lock();
		try{
			while(noOfSearch > 0  || noOfInsert > 0 || noOfRemove > 0){
				removeCond.await();
			}
			noOfRemove++;
		}
		finally{
			myLock.unlock();
		}
	}
	
    /**
     * Release the lock and signal all waiting insert, search and remove operations
     */
	private void end_remove() {
		myLock.lock();
		try{
			noOfRemove--;
			if(noOfRemove == 0){
        		insertCond.signalAll();
        		searchCond.signalAll();
        		removeCond.signalAll();
        	}
		}
		finally{
			myLock.unlock();
		}
	}


	/**
	 * Inserts the given item into the list.  
	 * 
	 * Precondition:  item != null
	 * 
	 * @param item
	 * @throws InterruptedException
	 */
	public void insert(T item) throws InterruptedException{
		assert item != null: "Error in ConcurrentSearcherList insert:  Attempt to insert null";
		start_insert();
		try{
			first = new Node<T>(item, first);
		}
		finally{
			end_insert();
		}
	}
	
	/**
	 * Determines whether or not the given item is in the list
	 * 
	 * Precondition:  item != null
	 * 
	 * @param item
	 * @return  true if item is in the list, false otherwise.
	 * @throws InterruptedException
	 */
	public boolean search(T item) throws InterruptedException{
		assert item != null: "Error in ConcurrentSearcherList insert:  Attempt to search for null";
		start_search();
		try{
			for(Node<T> curr = first;  curr != null ; curr = curr.next){
				if (item.equals(curr.item)) return true;
			}
			return false;
		}
		finally{
			end_search();
		}
	}
	
	/**
	 * Removes the given item from the list if it exists.  Otherwise the list is not modified.
	 * The return value indicates whether or not the item was removed.
	 * 
	 * Precondition:  item != null.
	 * 
	 * @param item
	 * @return  whether or not item was removed from the list.
	 * @throws InterruptedException
	 */
	public boolean remove(T item) throws InterruptedException{
		assert item != null: "Error in ConcurrentSearcherList insert:  Attempt to remove null";
		start_remove();
		try{
			if(first == null) return false;
			if (item.equals(first.item)){first = first.next; return true;}
			for(Node<T> curr = first;  curr.next != null ; curr = curr.next){
				if (item.equals(curr.next.item)) {
					curr.next = curr.next.next;
					return true;
				}
			}
			return false;			
		}
		finally{
			end_remove();
		}
	}
}