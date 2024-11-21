package peco2282.bcreborn.model;

import com.google.gson.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import peco2282.bcreborn.utils.ModelUtil;

import java.util.HashMap;
import java.util.Map;

public class GeometryLoader implements IGeometryLoader<FunctionalGeometry> {
  private static final Logger log = LoggerFactory.getLogger(GeometryLoader.class);
  private static final String RULE = "rule";
  private static final String VARIABLE = "variable";
  private static final String TYPE = "type";
  private static final String TEXTURE = "texture";
  private static final String ELEMENTS = "elements";

  @Override
  public FunctionalGeometry read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
    log.info("RRR {}", deserializationContext);
    JsonObject elements = jsonObject.getAsJsonObject(ELEMENTS);
    JsonObject textures = jsonObject.getAsJsonObject(TEXTURE);
    Map<String, ResourceLocation> textureMap = new HashMap<>();
    for (Map.Entry<String, JsonElement> entry : textures.entrySet()) {
      int offset = entry.getKey().startsWith("#") ? 1 : 0;
      textureMap.put(entry.getKey().substring(offset), ResourceLocation.parse(entry.getValue().getAsString()));
    }

    for (JsonElement element : elements.getAsJsonArray(ELEMENTS)) {
      if (element.isJsonObject()) {
        element(element.getAsJsonObject(), deserializationContext);
      }
    }

    return new FunctionalGeometry();
  }

  private static void element(JsonObject object, JsonDeserializationContext context) {
    Vector3f from = ModelUtil.readPosition(object, "from", 16);
    Vector3f to = ModelUtil.readPosition(object, "to", 16);
    JsonObject faces = object.getAsJsonObject("faces");
    for (Direction direction : Direction.values()) {
      if (faces.has(direction.getSerializedName())) {
        JsonObject face = faces.getAsJsonObject(direction.getSerializedName());
      }
    }
  }
}
