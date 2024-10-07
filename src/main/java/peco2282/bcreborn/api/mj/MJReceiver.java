package peco2282.bcreborn.api.mj;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import peco2282.bcreborn.api.block.BCBlock;

@AutoRegisterCapability
public interface MJReceiver extends MJCapability {
  long require();
  <B extends Block & BCBlock> B receivedBy();
}
