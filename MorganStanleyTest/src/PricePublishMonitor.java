import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PricePublishMonitor {
	
	
	
	static PriceUpdateService priceUpdateService;
	static AlertService alertService;
	static int timeInterval;
	
	Semaphore sem ;
	
	static BankUpdateListener bankUpdateListener;;
	static ThirdPartyUpdateListener thirdPartyUpdateListener;
	
	
	
	PricePublishMonitor(PriceUpdateService priceUpdateService,AlertService alertService, Semaphore sem, int timeInterval){
		
		this.priceUpdateService=priceUpdateService;
		this.alertService=alertService;
		this.sem=sem;
		this.timeInterval=timeInterval;
		
	}
	
	
	public static void main(String[] args) {
		
		
		
		int interval = 30;
		new PricePublishMonitor(
				null, // Price Update Service
				null, // Alert Service
				new Semaphore(1,true),
				interval).monitor();
		
		}
	
	
	public void monitor(){ 
		
		if(priceUpdateService==null || alertService==null){
			System.out.println("Either Price Update Service Or Alert Service is Null hence Exiting.");
			System.exit(-1);
		}
		
		long currentTimeInMillisec = System.currentTimeMillis();
		
		bankUpdateListener = new BankUpdateListener(sem,timeInterval,currentTimeInMillisec);
		thirdPartyUpdateListener = new ThirdPartyUpdateListener();
		
		//Executor For Scheduled Task
				ScheduledExecutorService schExecutor = 	Executors.newScheduledThreadPool(10);	
				schExecutor.scheduleWithFixedDelay(
						new PriceComparisonWorker(sem), timeInterval, timeInterval, TimeUnit.SECONDS);
				
		
		//Subscribing to the Updates
		priceUpdateService.subscribeToBankPriceUpdates(bankUpdateListener);	
		priceUpdateService.subscribeToCompanyPriceUpdates(thirdPartyUpdateListener);
		 
		

		
	}
	

}
