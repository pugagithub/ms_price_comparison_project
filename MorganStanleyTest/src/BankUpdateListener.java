import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

public class BankUpdateListener implements PriceListener {
	
	volatile static ConcurrentHashMap<String, Double> currentRecordSet = new ConcurrentHashMap<String, Double>();
	

	Semaphore sem ;
	long timeInterval;
	
	static long timeInMilliSecs ;
	
	

	
	BankUpdateListener(Semaphore sem,long timeInterval, long timeInMilliSecs){
		this.timeInMilliSecs = timeInMilliSecs;
		this.sem=sem;
		this.timeInterval=timeInterval;		
	}
	
	
	@Override
	public void priceUpdate(String symbol, double price) {
		
		try {
			sem.acquire();
			if((System.currentTimeMillis()-timeInMilliSecs)<=(timeInterval*1000)){
				//System.out.println("Am adding data in banking map");
				currentRecordSet.put(symbol,price);
			}
			else {
				System.out.println("Time is not okay"+ (System.currentTimeMillis()-timeInMilliSecs));
				PriceComparisonWorker.previousDataMapReady=false;
				sem.release();	
				while(currentRecordSet.size()>0){  
					Thread.sleep(10);}
				currentRecordSet.put(symbol,price);
				
				
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally{		
		sem.release();
		}
		
	}
	
	public static void initialize(){
		BankUpdateListener.currentRecordSet=new ConcurrentHashMap<>();
		timeInMilliSecs=System.currentTimeMillis();
		System.out.println("Data Initialized");
	}

}
