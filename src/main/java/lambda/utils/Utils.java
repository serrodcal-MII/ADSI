package lambda.utils;

import java.util.Properties;

public class Utils {
	public static final String TOPIC_NAME = "ingesta";
	public static final int SECONDS_TO_NEW_OFFLINE = 100;
	public static final String WHITESPACE = "[ \\t\\n\\x0B\\f\\r]+";
	public static final String HASH = "#";
	public static final Integer PACKAGE_SIZE = 10000;
	public static final Integer ONLINE_MILLIS = 10000;
	
	public static final Properties PROPIEDADES = getProperties();

	private static Properties getProperties() {
		Properties result = new Properties();
		result.put("topic", "ingesta");
		result.put("bootstrap.servers", "localhost:29092");
		return result;
	}
}
