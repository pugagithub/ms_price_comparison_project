import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

public class PriceComparisonWorker implements Runnable {

	Semaphore sem;
	
	static volatile boolean previousDataMapReady = false; 
	
	
	volatile static ConcurrentHashMap<String, Double> previousRecordSet = null;

	PriceComparisonWorker(Semaphore sem) {		
		this.sem = sem;
		
	}

	@Override
	public void run() {

		try {
			previousDataMapReady=false;
			System.out.println("Schedule Triggered ");
			sem.acquire();			
			previousRecordSet = BankUpdateListener.currentRecordSet;
			System.out.println("Banking Temp Map : "+ previousRecordSet);
			BankUpdateListener.initialize();
			previousDataMapReady=true;
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			sem.release();
		}

	}

}
