package fun.stgoder.psmgr.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtil {
    private static Gson gson;

    static {
        // .registerTypeAdapter(String.class, new StringConverter())
        gson = new GsonBuilder().serializeNulls().create();
    }

    public static <T> T parse(String json, Class<T> cls) {
        return gson.fromJson(json, cls);
    }

    public static <T> String toJson(T t) {
        return gson.toJson(t);
    }
}
