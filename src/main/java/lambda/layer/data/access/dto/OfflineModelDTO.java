package lambda.layer.data.access.dto;

import java.util.List;
import java.util.Map;

import org.mongodb.morphia.annotations.Entity;

@Entity("offline")
public class OfflineModelDTO extends ModelDTO{

	public OfflineModelDTO() {
		super();
	}

	public OfflineModelDTO(Map<String, List<String>> map) {
		super(map);
	}
}
