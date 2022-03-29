package lambda.layer.online.functions;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import data.streaming.dto.TweetDTO;
import lambda.layer.data.access.MongoDBDAO;
import lambda.layer.data.access.SingletonMongoDBDAO;
import lambda.layer.data.access.dto.OnlineModelDTO;
import lambda.utils.Utils;

public class OnlineETLFunction extends ProcessAllWindowFunction<String, String, TimeWindow> {

	// 2.2: Definición de la función que transformará las ventas generadas por
	// Flink en modelos de la capa online.
	
	private static final long serialVersionUID = 1L;
	
	private static MongoDBDAO dao;
	
	public OnlineETLFunction() {
		dao = SingletonMongoDBDAO.getMongoDBDAO();
	}

	/*synchronized*/ 
	public void process(ProcessAllWindowFunction<String, String, TimeWindow>.Context arg0, 
			Iterable<String> iterable,Collector<String> arg2) throws Exception {
		List<String> hashtags = new LinkedList<>();
		Morphia morphia = dao.getMorphia();
		Datastore datastore = dao.getDatastore();
		
		for (String s: iterable) {
			DBObject object = BasicDBObject.parse(s);
			TweetDTO elem = morphia.fromDBObject(datastore, TweetDTO.class, object);
			List<String> tags = Stream.of(elem.getText().split(Utils.WHITESPACE)).filter(x -> x.startsWith(Utils.HASH)).collect(Collectors.toList());
			hashtags.addAll(tags);
		}
		
		Map<String, Long> freqs = hashtags.stream().collect(Collectors.groupingBy(x -> x, Collectors.counting())); 
		// datastore.save(new OnlineModelDTO(freqs));
	}

}
