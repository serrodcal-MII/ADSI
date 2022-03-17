package lambda.layer.batch;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.mongodb.morphia.Datastore;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import data.streaming.dto.TweetDTO;
import lambda.layer.data.access.MongoDBDAO;
import lambda.layer.data.access.SingletonMongoDBDAO;
import lambda.layer.data.access.dto.OfflineModelDTO;
import lambda.utils.Utils;

public class BatchJob implements Job {
	// 3.2: Definición del job. En realidad debe hacer algo bastante parecido a
	// lo que hicimos en la capa online pero trabajando con todos los datos del data
	// lake.

	private static MongoDBDAO dao;

	public BatchJob() {
		dao = SingletonMongoDBDAO.getMongoDBDAO();
	}

	public void execute(JobExecutionContext context) throws JobExecutionException {
		process();
	}

	synchronized public void process() {
		List<String> hashtags = new LinkedList<>();
		
		Datastore datastore = dao.getDatastore();
		
		List<TweetDTO> datos = dao.getDataLake();

		List<String> tags = datos.stream()
			.flatMap(x -> Stream.of(x.getText().split(Utils.WHITESPACE)))
			.filter(x -> x.startsWith(Utils.HASH))
			.collect(Collectors.toList());

		hashtags.addAll(tags);
		
		Map<String, Long> freqs = hashtags.stream().collect(Collectors.groupingBy(x -> x, Collectors.counting())); 
		datastore.save(new OfflineModelDTO(freqs));
	}

}
