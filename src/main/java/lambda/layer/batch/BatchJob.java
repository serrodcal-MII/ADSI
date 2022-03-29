package lambda.layer.batch;

import java.util.ArrayList;
import java.util.Arrays;
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
		
		Datastore datastore = dao.getDatastore();
		
		List<TweetDTO> datos = dao.getDataLake();

		Map<String, List<String>> usersByHashtag = new HashMap<>(); // Hashatag -> List<User>

		datos.stream().forEach( tweet -> {
			String user = tweet.getUser().getScreenName();
			String text = tweet.getText();
			List<String> hashtags = Stream.of(text.split(Utils.WHITESPACE)).filter(x -> x.startsWith(Utils.HASH)).collect(Collectors.toList());
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
				
		});

		Map<String, List<String>> topFreqs = new HashMap<>();

		for (String hashtag : usersByHashtag.keySet()) {
			List<String> users = usersByHashtag.get(hashtag);
			Map<String, Long> numbers = users.stream().collect(Collectors.groupingBy(user -> user, Collectors.counting()));
			Map<String, Long> orderedNumbers = sortByValue(numbers);
			Map<String, Long> top = getFirstsElements(orderedNumbers, 3);
			topFreqs.put(hashtag, new ArrayList<>(top.keySet()));
		}
		
		datastore.save(new OfflineModelDTO(topFreqs));
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
