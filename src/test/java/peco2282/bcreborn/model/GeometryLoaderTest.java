package peco2282.bcreborn.model;

import com.google.gson.JsonParser;
import net.minecraftforge.client.model.ExtendedBlockModelDeserializer;
import org.junit.jupiter.api.Test;

class GeometryLoaderTest {
  static final String testJson = """
      {
        "type": {
          "x": "bcreborn:int",
          "y": "bcreborn:boolean",
          "z": "bcreborn:direction"
         },
        "variable": ["x", "y", "z"],
        "rule": [{"submission": ""}]
      }
      """;
  private final GeometryLoader geometryLoader = new GeometryLoader();

  @Test
  void read() {
    geometryLoader.read(JsonParser.parseString(testJson).getAsJsonObject(), ExtendedBlockModelDeserializer.INSTANCE::fromJson);
  }
}