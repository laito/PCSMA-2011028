import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 */

/**
 * Producer Consumer Problem using Semaphores, Conditional Variables and Monitors
 * @author Apoorv Singh
 *
 */
public class ProducerConsumer_2011028 {
	

    static abstract class ConcurrentQueue
    {
        protected List<String> mQ = new ArrayList<String>();

        abstract public void put(String msg);

        abstract public String take();
        
    }
    
    static class QueueFactory {
    	public static ConcurrentQueue getQueue(String type) {
    		ConcurrentQueue queue = null;
    		switch(type) {
    		case "semaphore":
    			queue = new SemaphoreQueue();
    			System.out.println("Producer Consumer using Semaphores");
    			break;
    		case "cv":
    			queue = new CVQueue();
    			System.out.println("Producer Consumer using a Conditional Variables");
    			break;
    		case "monitors":
    			queue = new SynchronizedQueue();
    			System.out.println("Producer Consumer using Monitors");
    			break;
    		default:
    			break;
    		}
    		return queue;
    	}
    }

    static class SynchronizedQueue extends ConcurrentQueue {

    	Boolean full = false;
    	
		@Override
		public synchronized void put(String msg) {
			while(full) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			mQ.add(msg);
			full = true;
			notifyAll();
		}
		
		@Override
		public synchronized String take() {
			while(!full) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			String removed = mQ.remove(0);
			full = false;
			notifyAll();
			return removed;
		}
    	
    }
    
    static class SemaphoreQueue extends ConcurrentQueue {
		
    	Semaphore put = null;
    	Semaphore take = null;
    	
    	public SemaphoreQueue() {
    		put = new Semaphore(1, true);
    		take = new Semaphore(0, true);
    	}
    	
		@Override
        public void put(String msg) {
        	try {
				put.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        	mQ.add(msg);
        	take.release();
        }
        
		@Override
        public String take(){
        	try {
				take.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        	String removed = mQ.remove(0);
        	put.release();
        	return removed;
        }
	}
    
    static class CVQueue extends ConcurrentQueue {
		
    	Lock lock = null;
    	Condition full = null;
    	Condition empty = null;
    	Boolean isFull = false;
    	
    	
    	public CVQueue() {
    		lock = new ReentrantLock();
    		full = lock.newCondition();
    		empty = lock.newCondition();
    	}
        
		@Override
        public void put(String msg) {
        	lock.lock();
        	try {
        		while(isFull == true) {
		        	try {
						empty.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
        		}
	        	mQ.add(msg);
	        	isFull = true;
	        	full.signal();
        	} finally {
        		lock.unlock();
        	}
        }
        
		@Override
        @SuppressWarnings("finally")
		public String take() {
        	lock.lock();
        	String removed = null;
        	try {
        		while(isFull == false) {
		        	try {
						full.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
        		}
	        	removed = mQ.remove(0);
	        	empty.signal();
	        	isFull = false;
        	} finally {
        		lock.unlock();
            	return removed;
        	}
        }
	}

    static int mMaxIterations = 100;

    public static String displayMenu() {
    	System.out.println("Please choose the method:");
    	System.out.println("1) Semaphores");
    	System.out.println("2) Condition Variables");
    	System.out.println("3) Monitors");
//    	System.out.println("4) CountDownLatch");
    	Scanner in = new Scanner(System.in);
    	String input = in.nextLine();
    	String choice = null;
    	switch(input.toLowerCase()) {
    	case "1":
    	case "semaphores":
    		choice = "semaphore";
    		break;
    	case "2":
    	case "condition variables":
    		choice = "cv";
    		break;
    	case "3":
    	case "monitors":
    		choice = "monitors";
    		break;
//    	case "4":
//    	case "countdownlatch":
//    		choice = "countDownlatch";
//    		break;
    	default:
    		System.out.println("Invalid Choice!");
    		choice = displayMenu();
    		break;
    	}
    	return choice;
    }
    
    
    public static void main(String argv[]) {
    	
    	String waitType = displayMenu();

        final ConcurrentQueue queue = QueueFactory.getQueue(waitType);

        /* 
         * Create a producer thread.
         */
        
        Thread producer = 
            new Thread(new Runnable(){ 
                    public void run(){ 
                        for(int i = 0; i < mMaxIterations; i++)
                            queue.put(Integer.toString(i)); 
                    }});
        /* 
         * Create a consumer thread.
         */
        
        Thread consumer =
            new Thread(new Runnable(){
                    public void run(){ 
                        for(int i = 0; i < mMaxIterations; i++)
                            System.out.println(queue.take());
                    }});

        /* 
         * Run both threads concurrently.
         */
       
        producer.start(); 
        consumer.start();
    }

}
