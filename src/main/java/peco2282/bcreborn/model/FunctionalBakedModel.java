package peco2282.bcreborn.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.IForgeBakedModel;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FunctionalBakedModel implements BakedModel {
  private final boolean isGui3d;
  private final List<BakedQuad> quads;
  private final TextureAtlasSprite particle;

  public FunctionalBakedModel(@Nullable List<BakedQuad> quads, boolean isGui3d) {
    this.quads = quads == null ? ImmutableList.of() : ImmutableList.copyOf(quads);
    this.isGui3d = isGui3d;
    if (this.quads.isEmpty()) {
      // net.minecraft.client.renderer.texture.TextureAtlas.missingSprite
      this.particle = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(null);
    } else {
      this.particle = this.quads.getFirst().getSprite();
    }
  }

  /**
   * @param p_235039_
   * @param p_235040_
   * @param p_235041_
   * @deprecated Forge: Use {@link IForgeBakedModel#getQuads(BlockState, Direction, RandomSource, ModelData, RenderType)}
   */
  @Override
  @Deprecated
  public List<BakedQuad> getQuads(@Nullable BlockState p_235039_, @Nullable Direction p_235040_, RandomSource p_235041_) {
    return p_235040_ == null ? ImmutableList.of() : this.quads;
  }

  @Override
  public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType) {
    return this.getQuads(state, side, rand);
  }

  @Override
  public boolean useAmbientOcclusion() {
    return false;
  }

  @Override
  public boolean isGui3d() {
    return this.isGui3d;
  }

  @Override
  public boolean usesBlockLight() {
    return true;
  }

  @Override
  public boolean isCustomRenderer() {
    return true;
  }

  /**
   * @deprecated Forge: Use {@link IForgeBakedModel#getParticleIcon(ModelData)}
   */
  @Override
  @Deprecated
  public TextureAtlasSprite getParticleIcon() {
    return this.particle;
  }

  @Override
  public ItemOverrides getOverrides() {
    return ItemOverrides.EMPTY;
  }
}
