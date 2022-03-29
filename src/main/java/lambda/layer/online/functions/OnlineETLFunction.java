package lambda.layer.online.functions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

		Map<String, List<String>> usersByHashtag = new HashMap<>();

		Morphia morphia = dao.getMorphia();
		Datastore datastore = dao.getDatastore();
		
		for (String s: iterable) {
			DBObject object = BasicDBObject.parse(s);
			TweetDTO elem = morphia.fromDBObject(datastore, TweetDTO.class, object);
			String user = elem.getUser().getScreenName();
			List<String> hashtags = Stream.of(elem.getText().split(Utils.WHITESPACE)).filter(x -> x.startsWith(Utils.HASH)).collect(Collectors.toList());
			hashtags.forEach(hashtag -> {
				if (usersByHashtag.containsKey(user)){
					List<String> users = usersByHashtag.get(user);
					users.add(user);
					usersByHashtag.put(hashtag, users);
				} else {
					List<String> users = new LinkedList<>();
					users.add(user);
					usersByHashtag.put(hashtag, users);
				}
			});
		}

		Map<String, List<String>> topFreqs = new HashMap<>();

		for (String hashtag : usersByHashtag.keySet()) {
			List<String> users = usersByHashtag.get(hashtag);
			Map<String, Long> numbers = users.stream().collect(Collectors.groupingBy(user -> user, Collectors.counting()));
			Map<String, Long> orderedNumbers = sortByValue(numbers);
			Map<String, Long> top = getFirstsElements(orderedNumbers, 3);
			topFreqs.put(hashtag, new ArrayList<>(top.keySet()));
		}
		
		datastore.save(new OnlineModelDTO(topFreqs));
	}

	private static <K, V> Map<K, V> sortByValue(Map<K, V> map) {
		List<Entry<K, V>> list = new LinkedList<>(map.entrySet());
		Collections.sort(list, new Comparator<Object>() {
			@SuppressWarnings("unchecked")
			public int compare(Object o1, Object o2) {
				return ((Comparable<V>) ((Map.Entry<K, V>) (o1)).getValue()).compareTo(((Map.Entry<K, V>) (o2)).getValue());
			}
		});
	
		Map<K, V> result = new LinkedHashMap<>();
		for (Iterator<Entry<K, V>> it = list.iterator(); it.hasNext();) {
			Map.Entry<K, V> entry = (Map.Entry<K, V>) it.next();
			result.put(entry.getKey(), entry.getValue());
		}
	
		return result;
	}

	private static <K, V> Map<K, V> getFirstsElements(Map<K, V> map, int elements) {
		int cont = 0;
		List<Entry<K, V>> list = new LinkedList<>(map.entrySet());

		Map<K, V> result = new LinkedHashMap<>();
		for (Iterator<Entry<K, V>> it = list.iterator(); it.hasNext();) {
			Map.Entry<K, V> entry = (Map.Entry<K, V>) it.next();
			result.put(entry.getKey(), entry.getValue());
			cont++;
			if (cont < elements) {
				break;
			}
		}

		return result;

	}

}
