package lambda.layer.data.access.dto;

import java.util.List;
import java.util.Map;

import org.mongodb.morphia.annotations.Entity;

@Entity("online")
public class OnlineModelDTO extends ModelDTO {

	public OnlineModelDTO(Map<String, List<String>> map) {
		super(map);
	}
	
	public OnlineModelDTO() {
		super();
	}
	

}
