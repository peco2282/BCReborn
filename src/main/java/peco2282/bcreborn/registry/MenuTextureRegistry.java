package peco2282.bcreborn.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import peco2282.bcreborn.BCReborn;

import java.util.Map;
import java.util.stream.IntStream;

@ApiStatus.Internal
public record MenuTextureRegistry(IntPair size, ResourceLocation texture,
                                  Map<String, TextureElement> elements) {
  public static final ResourceKey<Registry<MenuTextureRegistry>> MENU_TEXTURE = ResourceKey.createRegistryKey(BCReborn.location("menu_texture"));
  private static final Codec<Map<String, TextureElement>> ELEMENT_CODEC = Codec.unboundedMap(Codec.STRING, TextureElement.CODEC);
  public static final Codec<MenuTextureRegistry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      IntPair.CODEC.fieldOf("size").forGetter(MenuTextureRegistry::size),
      ResourceLocation.CODEC.fieldOf("texture").forGetter(MenuTextureRegistry::texture),
      ELEMENT_CODEC.fieldOf("elements").forGetter(MenuTextureRegistry::elements)
  ).apply(instance, MenuTextureRegistry::new));
  public static final ResourceKey<MenuTextureRegistry> FILLER = ResourceKey.create(MENU_TEXTURE, BCReborn.location("filler"));
  public static void bootstrap(BootstrapContext<MenuTextureRegistry> context) {
    context.register(FILLER, new MenuTextureRegistry(
        new IntPair(176, 241),
        BCReborn.location("gui/filler.png"),
        Map.of("title", new TextureElement(BCReborn.location("text"), new IntPair(88, 10), 0x404040)
        )
    ));
  }

  public static class IntPair {
    static final Codec<IntPair> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT_STREAM.fieldOf("size").xmap(s -> {
              int[] arr = s.toArray();
              assert arr.length == 2;
              return new IntPair(arr[0], arr[1]);
            },
            i -> IntStream.of(i.width, i.height)).forGetter(it -> it)
    ).apply(instance, IntPair::new));
    private final int width;
    private final int height;

    public IntPair(Integer width, Integer height) {
      this.width = width;
      this.height = height;
    }

    public IntPair(IntPair pair) {
      this.width = pair.width;
      this.height = pair.height;
    }
  }

  public static final class TextureElement {
    public static final Codec<TextureElement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("texture").forGetter(it -> it.texture),
        IntPair.CODEC.fieldOf("size").forGetter(it -> it.size),
        Codec.INT.fieldOf("color").forGetter(it -> it.color)
    ).apply(instance, TextureElement::new));
    private final ResourceLocation texture;
    private final IntPair size;
    private final Integer color;

    public TextureElement(ResourceLocation texture, IntPair size, Integer color) {
      this.texture = texture;
      this.size = size;
      this.color = color;
    }
  }
}
