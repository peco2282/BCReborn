package peco2282.bcreborn.lib.block;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import peco2282.bcreborn.api.block.BCBlock;
import peco2282.bcreborn.api.block.BCProperties;
import peco2282.bcreborn.api.block.Facing;
import peco2282.bcreborn.api.block.RotatableFacing;

public abstract class BlockBaseNeptune extends Block implements BCBlock {
  private static final Logger log = LoggerFactory.getLogger(BlockBaseNeptune.class);
  protected final String id;

  @SuppressWarnings({"unchecked", "rawtypes"})
  @SafeVarargs
  public BlockBaseNeptune(Properties properties, @NotNull String id, Tuple<Property<?>, ?>... states) {
    super(update(properties));
    BlockState raw = getStateDefinition().any();
    for (Tuple<Property<?>, ?> state : states)
      raw.setValue((Property) state.getA(), (Comparable) state.getB());
    registerDefaultState(raw);
    this.id = id;
  }

  private static @NotNull Properties update(@NotNull Properties properties) {
    return properties
        .destroyTime(5.0F)
        .explosionResistance(10.0F)
        .sound(SoundType.METAL);
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {
    gatherStateDefinition(p_49915_);
  }

  protected abstract void gatherStateDefinition(StateDefinition.Builder<Block, BlockState> builder);

  public String getId() {
    return id;
  }

  @Override
  protected BlockState rotate(BlockState state, Rotation rotation) {
    if (this instanceof RotatableFacing facing) {
      Property<Direction> prop = facing.getFacingProperty();
      return state.setValue(prop, rotation.rotate(state.getValue(prop)));
    }
    return state;
  }

  @Override
  protected BlockState mirror(BlockState p_60528_, Mirror p_60529_) {
    if (this instanceof RotatableFacing facing) {
      Property<Direction> prop = facing.getFacingProperty();
      return p_60528_.rotate(p_60529_.getRotation(p_60528_.getValue(prop)));
    }
    return p_60528_;
  }

  @NotNull
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext p_49820_) {
    BlockState state = super.defaultBlockState();
    if (this instanceof Facing facing) {
      Direction orientation = p_49820_.getHorizontalDirection();
      if (
          Mth.abs((float) p_49820_.getPlayer().getX() - p_49820_.getClickedPos().getX()) < 2F &&
              Mth.abs((float) p_49820_.getPlayer().getZ() - p_49820_.getClickedPos().getZ()) < 2F
      ) {
        double y = p_49820_.getPlayer().getY() + p_49820_.getPlayer().getEyeHeight();

        if (y - p_49820_.getClickedPos().getY() > 2.0D) {
          orientation = Direction.DOWN;
        }
        if (p_49820_.getClickedPos().getY() - y > 0.0D) {
          orientation = Direction.UP;
        }
      } else {
        orientation = orientation.getOpposite();
      }
      return state.setValue(facing.getFacingProperty(), p_49820_.getNearestLookingDirection().getOpposite().getOpposite());
    }
    return state;
  }

  public Item.Properties itemProperties() {
    return new Item.Properties();
  }
}
