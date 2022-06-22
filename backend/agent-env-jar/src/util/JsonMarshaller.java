package util;

import com.google.gson.Gson;

public class JsonMarshaller {

	private static final Gson gson = new Gson();
	
	public static <T> String toJson(T object) {
		return gson.toJson(object);
	}

}
