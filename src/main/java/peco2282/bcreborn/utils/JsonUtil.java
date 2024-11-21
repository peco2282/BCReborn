package peco2282.bcreborn.utils;

import com.google.gson.*;
import net.minecraft.util.GsonHelper;
import org.joml.Vector3f;

public class JsonUtil {
  public static float[] getSubAsFloatArray(JsonObject obj, String string) {
    if (!obj.has(string)) {
      throw new JsonSyntaxException("Required member " + string + " in " + obj);
    }
    return getAsFloatArray(obj.get(string));
  }

  public static float[] getAsFloatArray(JsonElement elem) {
    if (elem.isJsonArray()) {
      JsonArray array = elem.getAsJsonArray();
      float[] floats = new float[array.size()];
      for (int i = 0; i < floats.length; i++) {
        floats[i] = getAsFloat(array.get(i));
      }
      return floats;
    } else if (elem.isJsonPrimitive()) {
      return new float[] { getAsFloat(elem) };
    } else {
      throw new JsonSyntaxException("Needed an array of floats or a single float but got " + elem);
    }
  }
  public static float getAsFloat(JsonElement element) {
    if (!element.isJsonPrimitive()) {
      throw new JsonSyntaxException("Needed a primitive, but got " + element);
    }
    JsonPrimitive prim = element.getAsJsonPrimitive();
    try {
      return prim.getAsFloat();
    } catch (NumberFormatException nfe) {
      throw new JsonSyntaxException("Expected a valid float, but got " + prim, nfe);
    }
  }

  public static Vector3f getVector3f(JsonObject p_111335_, String p_111336_) {
    JsonArray jsonarray = GsonHelper.getAsJsonArray(p_111335_, p_111336_);
    if (jsonarray.size() != 3) {
      throw new JsonParseException("Expected 3 " + p_111336_ + " values, found: " + jsonarray.size());
    } else {
      float[] afloat = new float[3];

      for(int i = 0; i < afloat.length; ++i) {
        afloat[i] = GsonHelper.convertToFloat(jsonarray.get(i), p_111336_ + "[" + i + "]");
      }

      return new Vector3f(afloat[0], afloat[1], afloat[2]);
    }
  }
}
