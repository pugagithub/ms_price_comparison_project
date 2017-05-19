import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NewDummyPriceUpdateService implements PriceUpdateService {

	
	volatile ConcurrentHashMap<String, Double> map ;
	long interval;
	volatile static boolean firstTime = true;
	volatile static boolean inTheProcessOfTransferring = false;
	
	NewDummyPriceUpdateService(long interval){
		this.interval=interval;
	}
	
	@Override
	public void subscribeToBankPriceUpdates(final PriceListener priceListener) {
		map = new ConcurrentHashMap<>();
		
		
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true){
					if((firstTime || PriceComparisonWorker.previousDataMapReady) && !inTheProcessOfTransferring){
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				double price = new Double(new Random().nextInt(100));
				priceListener.priceUpdate("A", price);
				map.put("A",price);
				
				price = new Double(new Random().nextInt(100));
				priceListener.priceUpdate("B", price);
				map.put("B",price);
				
				price = new Double(new Random().nextInt(100));
				priceListener.priceUpdate("C", price);
				map.put("C",price);
				
				price = new Double(new Random().nextInt(100));
				priceListener.priceUpdate("D", price);
				map.put("D",price);
					}
					else{
						System.out.println("Data is not ready hence not adding in the banking");
					}
				}
			}
		}).start();
		
		
		
		
	}

	@Override
	public void subscribeToCompanyPriceUpdates(PriceListener priceListener) {

			while (true) {
				try {
					// System.out.println("Before Wait");
					Thread.sleep(interval * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Lock lock = new ReentrantLock();
				lock.lock();
					firstTime = false;
					inTheProcessOfTransferring=true;
					// Thread.sleep(100);
					// System.out.println("After Wait");
				
				Iterator<String> itr = map.keySet().iterator();
				while (itr.hasNext()) {
					String key = itr.next();
					priceListener.priceUpdate(key, map.get(key));
				}
				
				inTheProcessOfTransferring = false;
				lock.unlock();
			}
	}

}
