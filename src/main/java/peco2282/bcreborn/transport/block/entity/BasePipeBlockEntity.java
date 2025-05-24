package peco2282.bcreborn.transport.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import peco2282.bcreborn.BCReborn;
import peco2282.bcreborn.event.PipeCreationEvent;
import peco2282.bcreborn.lib.block.entity.BCBaseBlockEntity;
import peco2282.bcreborn.transport.block.PipeMaterial;
import peco2282.bcreborn.transport.block.PipeType;
import peco2282.bcreborn.transport.block.pipe.PipeStorage;

import java.util.Objects;

public abstract class BasePipeBlockEntity extends BCBaseBlockEntity {
  protected final PipeMaterial material;
  protected final PipeType type;
  protected final PipeStorage<?> storage;
  public BasePipeBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_, PipeMaterial material, PipeType type) {
    super(p_155228_, p_155229_, p_155230_);
    this.material = material;
    this.type = type;
    this.storage = PipeStorage.create(this);

    BCReborn.EVENT_BUS.post(new PipeCreationEvent(this));
  }

  public final PipeType getPipeType() {
    return type;
  }

  public final PipeMaterial getPipeMaterial() {
    return material;
  }

  public final Level getLevel() {
    return Objects.requireNonNull(level, "level is null");
  }

  public abstract PipeStorage<?> getStorage();

  protected abstract void update(Level world, BlockPos pos, BlockState state);
}
