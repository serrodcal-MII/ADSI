package data.streaming.dto;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;


@Entity("tweets")
public class TweetDTO {
	@Id
	private ObjectId id;
	private String createdAt;
	private TweetUserDTO user;
	private String text;
	private String language;

	public TweetDTO() {
		super();
	}

	public TweetUserDTO getUser() {
		return user;
	}

	public String getText() {
		return text;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public String getLanguage() {
		return language;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((createdAt == null) ? 0 : createdAt.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TweetDTO other = (TweetDTO) obj;
		if (createdAt == null) {
			if (other.createdAt != null)
				return false;
		} else if (!createdAt.equals(other.createdAt))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	public String toString() {
		return "TweetDTO [user=" + user + ", text=" + text + ", createdAt=" + createdAt + ", language=" + language
				+ "]";
	}

}