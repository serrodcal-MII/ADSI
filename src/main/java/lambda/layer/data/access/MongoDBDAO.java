package lambda.layer.data.access;

import java.util.List;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import data.streaming.dto.TweetDTO;
import lambda.layer.data.access.dto.ModelDTO;

public interface MongoDBDAO {
	Morphia getMorphia();

	Datastore getDatastore();

	List<ModelDTO> getOnlineModel(Long timestamp);
	
	ModelDTO getOfflineModel();
	
	List<TweetDTO> getDataLake();

	void deleteOnline();

	void deleteDataLake();


}