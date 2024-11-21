package peco2282.bcreborn.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;
import org.joml.Vector3f;

import java.util.Arrays;

public class JsonQuad {
  public JsonQuad(JsonObject quad, Vector3f from, Vector3f to, Direction direction) {
    Float[] uv = GsonHelper.getAsJsonArray(quad, "uv").asList().stream().map(JsonElement::getAsFloat).toArray(Float[]::new);
    if (uv.length != 4) {
      throw new JsonSyntaxException("Expected exactly 4 floats, but got " + Arrays.toString(uv));
    }
    UvFace uvFace = new UvFace(uv[0], uv[1], uv[2], uv[3]);

    Vector3f radius = new Vector3f(to.x() - from.x(), to.y() - from.y(), to.z() - from.z());
    radius.mul(0.5f);
    Vector3f center = new Vector3f(from);
    center.add(radius);
  }
}
