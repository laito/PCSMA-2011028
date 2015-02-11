import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/*
 * Ping Pong Game using Semaphores, Conditional Variables, Monitors and Count Down Latches
 */
public class PingPong_2011028 {
	
	static boolean pinged = false;    
    public static int mMaxIterations = 10;
    public static Boolean mainCond = false;

    
    static abstract class PingPongThread extends Thread {
    	
    	protected String mStringToPrint;
    	protected Integer printCount = 0;
    	
    	public PingPongThread(String stringToPrint) {
    		this.mStringToPrint = stringToPrint;
    	}
    	
    	public abstract void run();
    }
    
    static class SemaphorePingPong extends PingPongThread {
    	
    	Semaphore self;
    	Semaphore other;
    	
    	public SemaphorePingPong(String stringToPrint, Semaphore self, Semaphore other) {
			super(stringToPrint);
			this.self = self;
			this.other = other;
		}

		public void run() {
    		while(printCount < mMaxIterations) {
                try {
                    ((Semaphore) self).acquire();
                } catch (InterruptedException ex) {
                    System.out.println("Caught Interrupted Exception : " + ex.getMessage());
                }
                System.out.println(mStringToPrint);
                printCount++;
                ((Semaphore) other).release();
            }
    	}
    }
    
    static class CVPingPong extends PingPongThread {

    	Lock lock;
    	Condition self;
    	Condition other;
    	Boolean checkCond;
    	
		public CVPingPong(String stringToPrint, Lock lock, Condition self, Condition other) {
			super(stringToPrint);
            this.self = self;
            this.other = other;
            this.lock = lock;
            if(this.mStringToPrint.equals("Ping!")) {
            	this.checkCond = true;
            } else {
            	this.checkCond = false;
            }
		}

		@Override
		public void run() {
			while(printCount < mMaxIterations) {
        		lock.lock();
	        	try {
	    			while(mainCond == checkCond) {
	    				try {
							other.await();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
	    			}
	    			System.out.println(mStringToPrint);
	    			printCount++;
	    			mainCond = checkCond;
	    			self.signal();
	    		} finally {
	    			lock.unlock();
	    		}
        	}
		}	
    }
    
    
    static class SynchronizedPingPong extends PingPongThread {
    	
    	Boolean checkCond;
    	PingPonger say = null;
    	public SynchronizedPingPong(String stringToPrint) {
			super(stringToPrint);
			say = PingPonger.getPingPonger();
			if(stringToPrint.equals("Ping!")) {
				checkCond = true;
			} else {
				checkCond = false;
			}
		}
    	
    	
    	/* Ping Ponger Singleton */
    	static class PingPonger {
    		
    		private static PingPonger say;
    		
    		public static synchronized PingPonger getPingPonger() {
    			if(say == null) {
    				say = new PingPonger();
    			}
    			return say;
    		}
	    	public synchronized void ping(String StringToPrint) {
				while(pinged) {
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				System.out.println(StringToPrint);
				pinged = true;
				notifyAll();
	    	}
	    	
	    	public synchronized void pong(String StringToPrint) {
				while(!pinged) {
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				System.out.println(StringToPrint);
				pinged = false;
				notifyAll();
	    	}
    	}
    	
		public void run() {
			if(mStringToPrint.equals("Ping!")) {
				while(printCount < mMaxIterations) {
					say.ping(mStringToPrint);
					printCount++;
				}
			} else {
				while(printCount < mMaxIterations) {
					say.pong(mStringToPrint);
					printCount++;
				}
			}
    	}
    }
    
    static class LatchPingPong extends PingPongThread {

    	CountDownLatch latch;
    	PingPonger pingPonger;
    	String otherMove;
    	
		public LatchPingPong(String stringToPrint, CountDownLatch latch) {
			super(stringToPrint);
			this.latch = latch;
			this.pingPonger = PingPonger.getPingPonger();
			if(stringToPrint.equals("Ping!")) {
				otherMove = "Pong!";
			} else {
				otherMove = "Ping!";
			}
		}
		
		static class PingPonger {
			
			private String toPrint = "Ping!";
			private static PingPonger pingPonger = null;
			
			public static synchronized PingPonger getPingPonger() {
				if(pingPonger == null) {
					pingPonger = new PingPonger();
				}
				return pingPonger;
			}
			
			public synchronized void setMove(String toPrint) {
				this.toPrint = toPrint;
			}
			
			public synchronized void waitForOther() throws InterruptedException {
				wait();
			}
			
			public synchronized boolean shouldMove(String toPrint) {
				return this.toPrint.equals(toPrint);
			}
		}

		@Override
		public void run() {
			while(printCount < mMaxIterations) {
				while(!pingPonger.shouldMove(mStringToPrint));
				System.out.println(mStringToPrint);
				pingPonger.setMove(otherMove);
				printCount++;
			}
			latch.countDown();
		}
    }
    
    static class PingPongFactory {
    	
    	public static PingPongThread[] getThread(String type) {
    		PingPongThread threads[] = new PingPongThread[2];
    		PingPongThread PingThread = null;
    		PingPongThread PongThread = null;
    		
    		switch(type) {
    		case "semaphore":
    	        Semaphore sPing = new Semaphore(1, true);
                Semaphore sPong = new Semaphore(0, true);
                PingThread = new SemaphorePingPong("Ping!", sPing, sPong);
                PongThread = new SemaphorePingPong("Pong!", sPong, sPing);
    			System.out.println("Ping Pong using Semaphores");
    			break;
    		case "cv":
    	        Lock lock = new ReentrantLock();
                Condition cvPing  = lock.newCondition(); 
                Condition cvPong = lock.newCondition();
    			PingThread = new CVPingPong("Ping!", lock, cvPing, cvPong);
    			PongThread = new CVPingPong("Pong!", lock, cvPong, cvPing);
    			System.out.println("Ping Pong using a Conditional Variables");
    			break;
    		case "monitors":
    			PingThread = new SynchronizedPingPong("Ping!");
    			PongThread = new SynchronizedPingPong("Pong!");
    			System.out.println("Ping Pong using Monitors");
    			break;
    		case "countDownlatch":
    			CountDownLatch latch = new CountDownLatch(2);
    			PingThread = new LatchPingPong("Ping!", latch);
    			PongThread = new LatchPingPong("Pong!", latch);
    			System.out.println("Ping Pong using CountDownLatch");
    			PingThread.start();
    			PongThread.start();
    			try {
					latch.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					System.out.println("Done!");
				}
    			break;
    		default:
    			break;
    		}
    		threads[0] = PingThread;
    		threads[1] = PongThread;
    		return threads;
    	}
    }
    
    public static String displayMenu() {
    	System.out.println("Please choose the method:");
    	System.out.println("1) Semaphores");
    	System.out.println("2) Condition Variables");
    	System.out.println("3) Monitors");
    	System.out.println("4) CountDownLatch");
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
    	case "4":
    	case "countdownlatch":
    		choice = "countDownlatch";
    		break;
    	default:
    		System.out.println("Invalid Choice!");
    		choice = displayMenu();
    		break;
    	}
    	return choice;
    }
    
    public static void main(String[] args) throws InterruptedException {
    	
    	String waitType = displayMenu();
    	
    	System.out.println("Ready...Set...Go!");
    	
        PingPongThread threads[] = PingPongFactory.getThread(waitType);
        
        PingPongThread ping = threads[0];
        PingPongThread pong = threads[1];
        
        /* The implementation here will be a little bit different for CountDownLatch */
        if(!waitType.equals("countDownlatch")) {
	        // Start ping and pong threads, which calls their run() methods.
	        ping.start();
	        pong.start();
	
	        // Wait for both threads to exit before exiting main().
	        ping.join();
	        pong.join();
	        
	        System.out.println("Done!");
        }
    }
}