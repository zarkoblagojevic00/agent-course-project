package util;

import com.google.gson.Gson;

public class JsonMarshaller {

	private static final Gson gson = new Gson();
	
	public static <T> String toJson(T object) {
		return gson.toJson(object);
	}
	
	public static <T> String toBson(T object) {
		if (object instanceof String) {
			return (String) object;
		}
		return toJson(object).replaceAll("\"", "\'");
	}
	
	public static <T> T fromJson(String json, Class<T> clazz) {
		return gson.fromJson(json, clazz);
	}
 
}
