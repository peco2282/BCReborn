package peco2282.bcreborn.transport.block;

import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import peco2282.bcreborn.api.block.RotatableFacing;
import peco2282.bcreborn.lib.block.TileBaseNeptune;
import peco2282.bcreborn.utils.PropertyBuilder;


public abstract class BaseBlockPipe extends TileBaseNeptune implements RotatableFacing {
  public static final VoxelShape SHAPE_NORTH_SOUTH = box(0, 4, 4, 16, 12, 12);
  public static final VoxelShape SHAPE_EAST_WEST = box(4, 4, 0, 12, 12, 16);
  public static final VoxelShape SHAPE_UP_DOWN = box(4, 0, 4, 12, 16, 12);

  public static final Codec<PipeMaterial> MATERIAL_CODEC = StringRepresentable.fromEnum(PipeMaterial::values);
  public static final Codec<PipeType> TYPE_CODEC = StringRepresentable.fromEnum(PipeType::values);

  private final PipeMaterial material;
  private final PipeType type;
  public BaseBlockPipe(Properties properties, @NotNull String id, PipeMaterial material, PipeType type) {
    super(properties, id, PropertyBuilder.builder());
    this.material = material;
    this.type = type;
  }

  @Override
  protected VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
    return switch (p_60555_.getValue(getFacingProperty())) {
      case DOWN, UP -> SHAPE_UP_DOWN;
      case NORTH, SOUTH -> SHAPE_NORTH_SOUTH;
      case WEST, EAST -> SHAPE_EAST_WEST;
    };
  }

  @Override
  protected void gatherStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(getFacingProperty());
  }

  protected PipeMaterial getPipeMaterial() {
    return material;
  }
  protected PipeType getPipeType() {
    return type;
  }

  public abstract @NotNull String getId();

  protected static <P extends BaseBlockPipe> MapCodec<P> codecInstance(Function4<Properties, String, PipeMaterial, PipeType, P> function) {
    return RecordCodecBuilder.mapCodec(instance -> instance.group(
        propertiesCodec(),
        Codec.STRING.fieldOf("id").forGetter(BaseBlockPipe::getId),
        MATERIAL_CODEC.fieldOf("material").forGetter(BaseBlockPipe::getPipeMaterial),
        TYPE_CODEC.fieldOf("material").forGetter(BaseBlockPipe::getPipeType)
    ).apply(instance, function));
  }

  public enum PipeMaterial implements StringRepresentable {
    WOOD("wood"),
    STONE("stone"),
    COBBLESTONE("cobblestone"),
    IRON("iron"),
    GOLD("gold"),
    DIAMOND("diamond"),
    ;
    private final String material;

    PipeMaterial(String material) {
      this.material = material;
    }

    @Override
    public String getSerializedName() {
      return material;
    }
  }

  public enum PipeType implements StringRepresentable {
    ITEM("item"),
    @ApiStatus.Internal
    FLUID("fluid"),
    ENERGY("energy");
    private final String type;

    PipeType(String type) {
      this.type = type;
    }

    @Override
    public String getSerializedName() {
      return type;
    }
  }
}
