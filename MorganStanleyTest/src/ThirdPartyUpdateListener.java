import java.util.concurrent.Semaphore;

public class ThirdPartyUpdateListener implements PriceListener {
	
	
	AlertService alertService;
	
	ThirdPartyUpdateListener(){
	}

	@Override
	public  void priceUpdate(String symbol, double price) {
		try {
			
			while(!PriceComparisonWorker.previousDataMapReady){
				System.out.println("Waiting for Previous Record Set");
				Thread.sleep(10);
			}
			//PriceComparisonWorker.previousDataMapReady=false;
			if (PriceComparisonWorker.previousRecordSet != null	/*&& PriceComparisonWorker.previousRecordSet.get(symbol) != null*/) {
				
				if(PriceComparisonWorker.previousRecordSet.get(symbol) != null){
				if (PriceComparisonWorker.previousRecordSet.get(symbol) != price) {
					
					System.out.println("Value not correct for :" + symbol + "---> "+ "Price in Banking Data"+ 
							PriceComparisonWorker.previousRecordSet +
							"---> Price in 3rd Party --->" + price);
					//TODO
					//What happens if data in Banking is more than Third Party
					
					//TODO
					//What if data in 3rd party is more than Banking
					
					//TODO
					PricePublishMonitor.alertService.alert("Value not correct for :" + symbol + "---> "+ "Price in Banking Data"+ PriceComparisonWorker.previousRecordSet +"---> Price in 3rd Party --->" + price);
					
				} else {
					System.out.println("Prices are Equal For Symbol :" + symbol);
				}
				
				}else{
					PricePublishMonitor.alertService.alert("This Symbol hasnot been changed in last 30 seconds but wrongly sent to clients");
				}
				
				
			} else {
				System.out.println("Previous Record Set is null or Price not available for symbol :" + symbol);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
		}
	
		
		
	}
	
	
}
