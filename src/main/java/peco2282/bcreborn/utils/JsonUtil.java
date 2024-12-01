package peco2282.bcreborn.utils;

import com.google.gson.*;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
import net.minecraft.util.GsonHelper;
import org.joml.Vector3f;

import java.io.InputStreamReader;
import java.util.function.Function;

public class JsonUtil {
  private static void containCheck(JsonObject json, String key) {
    if (!json.has(key)) {
      throw new JsonSyntaxException("Expected " + key + " to be present");
    }
  }

  private static <E extends JsonElement> E safetyMapping(JsonElement json, Function<JsonElement, Boolean> function, Function<JsonElement, E> supplier) {
    if (function.apply(json))
      return supplier.apply(json);
    throw new JsonSyntaxException("Expected " + function + " to be present");
  }

  private static JsonObject safetyObjecting(JsonElement json) {
    return safetyMapping(json, JsonElement::isJsonObject, JsonElement::getAsJsonObject);
  }

  private static JsonArray safetyArraying(JsonElement json) {
    return safetyMapping(json, JsonElement::isJsonArray, JsonElement::getAsJsonArray);
  }

  public static boolean isString(JsonElement element) {
    return element.isJsonPrimitive() && element.getAsJsonPrimitive().isString();
  }

  public static boolean getBoolean(JsonElement element, boolean defaultValue) {
    return element.isJsonPrimitive() && element.getAsJsonPrimitive().getAsBoolean() ? element.getAsBoolean() : defaultValue;
  }

  public static JsonArray getArray(JsonObject json, String key) {
    containCheck(json, key);
    JsonElement jsonElement = json.get(key);
    if (jsonElement.isJsonArray()) {
      return jsonElement.getAsJsonArray();
    }
    throw new JsonSyntaxException("Expected a array value");
  }

  public static JsonArray getArray(JsonObject json, String key, int sized) {
    containCheck(json, key);
    JsonElement jsonElement = json.get(key);
    JsonArray array = getArray(safetyObjecting(jsonElement), key);
    if (array.size() == sized) {
      return array;
    }
    throw new JsonSyntaxException("Expected a size of " + sized + " value");
  }

  public static String[] getStringArray(JsonElement jsonElement, String key) {
    JsonArray array = getArray(safetyObjecting(jsonElement), key);
    String[] strings = new String[array.size()];
    for (int i = 0; i < strings.length; i++) {
      strings[i] = array.get(i).getAsString();
    }
    return strings;
  }

  public static String[] getStringArray(JsonElement jsonElement, String key, int sized) {
    JsonArray array = getArray(safetyObjecting(jsonElement), key);
    if (array.size() != sized) {
      throw new JsonSyntaxException("Expected a size of " + sized + " value");
    }
    String[] strings = new String[array.size()];
    for (int i = 0; i < strings.length; i++) {
      strings[i] = array.get(i).getAsString();
    }
    return strings;
  }

  public static Vector3f getVector3f(JsonObject p_111335_, String p_111336_) {
    JsonArray jsonarray = GsonHelper.getAsJsonArray(p_111335_, p_111336_);
    if (jsonarray.size() != 3) {
      throw new JsonParseException("Expected 3 " + p_111336_ + " values, found: " + jsonarray.size());
    } else {
      float[] afloat = new float[3];

      for (int i = 0; i < afloat.length; ++i) {
        afloat[i] = GsonHelper.convertToFloat(jsonarray.get(i), p_111336_ + "[" + i + "]");
      }

      return new Vector3f(afloat[0], afloat[1], afloat[2]);
    }
  }

  public static JsonObject fromISR(InputStreamReader reader) {
    return new Gson().fromJson(reader, JsonObject.class);
  }
}
