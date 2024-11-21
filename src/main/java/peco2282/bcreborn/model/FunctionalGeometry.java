package peco2282.bcreborn.model;

import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Function;

public class FunctionalGeometry implements IUnbakedGeometry<FunctionalGeometry> {
  public static final IGeometryLoader<FunctionalGeometry> LOADER = new GeometryLoader();
  private static final Logger log = LoggerFactory.getLogger(FunctionalGeometry.class);

  @Override
  public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides) {
    // BlockGeometryBakingContext, ModelBakery$ModelBakerImpl, ModelBakery$ModelBakerImpl$$Lambda, Variant, ItemOverrides
    log.info("BBB {}, {}, {}, {}, {}", context, baker, spriteGetter, modelState, overrides);

    return new FunctionalBakedModel(List.of(), true);
  }
}
