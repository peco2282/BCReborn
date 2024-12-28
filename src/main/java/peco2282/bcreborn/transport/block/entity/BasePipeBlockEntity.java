package peco2282.bcreborn.transport.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import peco2282.bcreborn.BCReborn;
import peco2282.bcreborn.event.PipeCreationEvent;
import peco2282.bcreborn.lib.block.entity.NeptuneBlockEntity;
import peco2282.bcreborn.transport.block.PipeMaterial;
import peco2282.bcreborn.transport.block.PipeType;
import peco2282.bcreborn.transport.block.pipe.PipeStorage;

public abstract class BasePipeBlockEntity extends NeptuneBlockEntity {
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

  public abstract PipeStorage<?> getStorage();
}
