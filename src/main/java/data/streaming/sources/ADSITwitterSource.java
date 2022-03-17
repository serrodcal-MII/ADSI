package data.streaming.sources;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.FindOptions;
import org.mongodb.morphia.query.Query;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import data.streaming.dto.TweetDTO;

public class ADSITwitterSource implements SourceFunction<String> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5152035615641900332L;
	private static final String DATASTORE = "ssiilab1";
	private static final String URI = "mongodb://alumno:alumno1@ssiilab1-shard-00-00.o2eqo.mongodb.net:27017,ssiilab1-shard-00-01.o2eqo.mongodb.net:27017,ssiilab1-shard-00-02.o2eqo.mongodb.net:27017/myFirstDatabase?ssl=true&replicaSet=atlas-q2liyc-shard-0&authSource=admin&retryWrites=true&w=majority";
	private static final int LIMIT = 100;
	private volatile boolean isRunning = true;

	public void run(SourceContext<String> ctx) {

		MongoClientURI uri = new MongoClientURI(URI);
		Morphia morphia = new Morphia();

		// tell Morphia where to find your classes
		// can be called multiple times with different packages or classes
		morphia.mapPackage("data.streaming.dto");

		final Datastore datastore = morphia.createDatastore(new MongoClient(uri), DATASTORE);

		Query<TweetDTO> query = datastore.find(TweetDTO.class);

		List<String> data = query.asList(new FindOptions().limit(LIMIT)).stream()
				.map(x -> morphia.toDBObject(x).toString()).collect(Collectors.toList());

		Integer i = 0;
		Integer count = 0;
		while (isRunning && i < data.size()) {
			synchronized (ctx.getCheckpointLock()) {
				ctx.collect(data.get(i));
				i++;
				count++;
				if (i >= data.size()) {
					data = query.asList(new FindOptions().skip(count).limit(LIMIT)).stream()
							.map(x -> morphia.toDBObject(x).toString()).collect(Collectors.toList());
					i = 0;
				}
			}
		}
	}

	public void cancel() {
		isRunning = false;
	}

}