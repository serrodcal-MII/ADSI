package lambda.layer.data.access.dto;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.mongodb.morphia.annotations.Id;

public class ModelDTO {
	@Id
	private Long timestamp;
	
	private List<String> claves;
	
	private List<List<String>> topUsers;
	
	public ModelDTO(Map<String, List<String>> map) {
		this.timestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
		this.claves = new ArrayList<String>(map.keySet());
		this.topUsers = claves.stream().map(x -> map.get(x)).collect(Collectors.toList());
	}
	
	public ModelDTO() {
		super();
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public List<String> getClaves() {
		return claves;
	}

	public void setClaves(List<String> claves) {
		this.claves = claves;
	}

	public List<List<String>> getTop10Users() {
		return topUsers;
	}

	public void setTop10Users(List<List<String>> topUsers) {
		this.topUsers = topUsers;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ModelDTO other = (ModelDTO) obj;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ModelDTO [timestamp=" + timestamp + ", claves=" + claves + "]";
	}

}
