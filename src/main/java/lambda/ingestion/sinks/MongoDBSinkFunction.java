package lambda.ingestion.sinks;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import data.streaming.dto.TweetDTO;
import lambda.layer.data.access.MongoDBDAO;
import lambda.layer.data.access.SingletonMongoDBDAO;
import lambda.utils.Utils;

public class MongoDBSinkFunction implements SinkFunction<String> {

	// 1.2: Definir la función necaria para guardar los datos en Mongo.
	// Necesitaremos completar el DAO.

	private static final long serialVersionUID = 1L;
	private static List<String> tweets;
	private static MongoDBDAO dao;

	public MongoDBSinkFunction() {
		this.tweets = new LinkedList<>();
		this.dao = SingletonMongoDBDAO.getMongoDBDAO();
		this.dao.deleteDataLake();
	}

	public void invoke(String value) {
		save(value);
	}

	synchronized private void save(String value) {
		this.tweets.add(value);
		if (this.tweets.size() >= Utils.PACKAGE_SIZE) {
			Morphia m = this.dao.getMorphia();
			Datastore ds = this.dao.getDatastore();
			
			List<TweetDTO> jsons = this.tweets.stream()
					.map((String s) ->  
						m.fromDBObject(ds, TweetDTO.class, (DBObject) BasicDBObject.parse(s))
					)
					.collect(Collectors.toList());
			ds.save(jsons);
			this.tweets.clear();
		}
	}
	
	

}
