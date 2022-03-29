package lambda;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lambda.ingestion.DataIngestor;
import lambda.layer.batch.BatchLayerLauncher;
import lambda.layer.online.OnlineLayerLauncher;
import lambda.layer.service.ServiceLayerLauncher;

public class Launcher {
	private static final Integer NUM_THREADS = 4;

	public static void main(String... strings) {
		ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

		// Runnable ingestor = new DataIngestor();
		// executor.execute(ingestor);

		// Runnable online = new OnlineLayerLauncher();
		// executor.execute(online);

		Runnable batch = new BatchLayerLauncher();
		executor.execute(batch);

		// Runnable service = new ServiceLayerLauncher();
		// executor.execute(service);
	}

}
