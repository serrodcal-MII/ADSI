package lambda.layer.data.access;

import java.util.ArrayList;
import java.util.List;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.FindOptions;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import data.streaming.dto.TweetDTO;
import lambda.layer.data.access.dto.ModelDTO;
import lambda.layer.data.access.dto.OfflineModelDTO;
import lambda.layer.data.access.dto.OnlineModelDTO;

public class MongoDBDAOImpl implements MongoDBDAO {
	private static final String DATASTORE = "adsilab";
	//private static final String URI = "mongodb://localhost:27017";
	//private static final String URI = "mongodb://alumno:alumno@localhost:27017";
	//private static final String URI = "mongodb://alumno:alumno@localhost:27017/adsilab?authSource=dbWithUserCredentials";
	private static final String URI = "mongodb://root:example@localhost:27017";
	private Datastore datastore;
	private Morphia morphia;

	public MongoDBDAOImpl() {
		MongoClientURI uri = new MongoClientURI(URI);
		morphia = new Morphia();

		// tell Morphia where to find your classes
		// can be called multiple times with different packages or classes
		morphia.mapPackage("data.streaming.dto");

		datastore = morphia.createDatastore(new MongoClient(uri), DATASTORE);
	}

	public Datastore getDatastore() {
		return datastore;
	}

	public Morphia getMorphia() {
		return morphia;
	}

	public void deleteOnline() {
		// 1.3: Definir método para "borrar" los modelos de la capa online.
		datastore.delete(datastore.find(OnlineModelDTO.class));
	}

	public void deleteDataLake() {
		// 1.4: Definir método para "borrar" los datos guardados en el data lake.
		datastore.delete(datastore.find(TweetDTO.class));
	}

	// 4.2: Generar modelos a partir de lo que tenemos guardado en MongoDB
	public List<ModelDTO> getOnlineModel(Long timestamp) {
		List<ModelDTO> result = new ArrayList<ModelDTO>();
		List<OnlineModelDTO> models = getDatastore().find(OnlineModelDTO.class).filter("_id >", timestamp).asList();
		if (models != null && !models.isEmpty()) {
			result.addAll(models);
		}
		return result;
	}

	public List<TweetDTO> getDataLake() {
		return getDatastore().find(TweetDTO.class).asList();
	}

	public ModelDTO getOfflineModel() {
		ModelDTO result = new OfflineModelDTO();
		List<OfflineModelDTO> models = getDatastore()
			.find(OfflineModelDTO.class)
			.order("-_id")
			// .order("_id")
			.asList(new FindOptions().limit(1));
		if (models != null && !models.isEmpty()) {
			result = models.get(0);
		}
		return result;
	}



}
