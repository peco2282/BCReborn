/*
 * BC Reborn
 *
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * Copyright (c) 2025-2026 peco2282
 *
 * Contains original work and code derived from BuildCraft.
 *
 * Licensed under the Minecraft Mod Public License 1.0 (MMPL).
 * See LICENSE for details.
 */
package com.peco2282.bcreborn.transport.item;

import com.peco2282.bcreborn.api.facades.FacadeType;
import com.peco2282.bcreborn.api.facades.IFacadeItem;
import com.peco2282.bcreborn.api.transport.PipeWire;
import com.peco2282.bcreborn.common.item.BuildCraftItem;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FacadeItem extends BuildCraftItem implements IFacadeItem {

  public FacadeItem() {
    super(new Properties());
  }

  public static FacadeState[] getStates(ItemStack stack) {
    if (stack.getItem() instanceof FacadeItem facade) {
      return facade.getFacadeStates(stack);
    }
    return new FacadeState[0];
  }

  public static FacadeType getType(ItemStack stack) {
    if (stack.getItem() instanceof FacadeItem facade) {
      return facade.getFacadeType(stack);
    }
    return FacadeType.Basic;
  }

  public static boolean isBlockValidForFacade(BlockState state) {
    Block block = state.getBlock();
    if (block == Blocks.AIR) return false;
    // オリジナルのBuildCraftの挙動に合わせ、モデルを持つフルブロックを基本とする
    if (state.getRenderShape() != RenderShape.MODEL) return false;
    
    // 技術的に問題がある可能性のあるブロック（TileEntityを持つものなど）を除外
//    if (block instanceof net.minecraft.world.level.block.EntityBlock) return false;
    if (block.asItem() instanceof IFacadeItem) return false;
    
    // 不透明なブロックか、ガラスのような一部の透過ブロックを許可
    // 簡易的な判定として、solid かつ impermeable (ガラス等) をチェック
    return state.isSolid() || state.is(BlockTags.IMPERMEABLE);
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    Level level = context.getLevel();
    BlockPos pos = context.getClickedPos();
    BlockEntity be = level.getBlockEntity(pos);

    if (be instanceof PipeBlockEntity pipe) {
      Direction side = context.getClickedFace();
      if (pipe.sideProperties.pluggables[side.ordinal()] == null) {
        if (!level.isClientSide) {
          FacadeState[] states = getFacadeStates(context.getItemInHand());
          if (states.length > 0 && states[0] != null && states[0].state != null) {
            pipe.sideProperties.pluggables[side.ordinal()] = new com.peco2282.bcreborn.transport.pipe.pluggable.FacadePluggable(states[0].state);
            pipe.setChanged();
            level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
            if (!context.getPlayer().getAbilities().instabuild) {
              context.getItemInHand().shrink(1);
            }
          }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
      }
    }

    return super.useOn(context);
  }

  @Override
  public Component getName(ItemStack stack) {
    FacadeType type = getFacadeType(stack);
    if (type == FacadeType.Basic) {
      FacadeState[] states = getFacadeStates(stack);
      if (states.length > 0) {
        return Component.translatable("item.bcreborntransport.facade.facade_basic_format", states[0].getDisplayName());
      }
    } else if (type == FacadeType.Phased) {
      return Component.translatable("item.bcreborntransport.facade.name");
    }
    return super.getName(stack);
  }

  @Override
  public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
    FacadeType type = getFacadeType(stack);
    FacadeState[] states = getFacadeStates(stack);

    if (type == FacadeType.Phased) {
      FacadeState defaultState = null;
      for (FacadeState state : states) {
        if (state.wire == null) {
          defaultState = state;
          continue;
        }
        tooltip.add(Component.translatable("item.FacadePhased.state", state.wire.getColor(), state.getDisplayName()));
      }
      if (defaultState != null) {
        tooltip.add(Component.translatable("item.FacadePhased.state_default", defaultState.getDisplayName()));
      }
    }
  }

  @Override
  public FacadeType getFacadeType(ItemStack stack) {
    if (stack.isEmpty() || !stack.hasTag()) {
      return FacadeType.Basic;
    }
    CompoundTag tag = stack.getTag();
    if (tag == null) return FacadeType.Basic;
    return FacadeType.fromOrdinal(tag.getByte("type"));
  }

  public FacadeState[] getFacadeStates(ItemStack stack) {
    if (stack.isEmpty() || !stack.hasTag()) {
      return new FacadeState[0];
    }
    CompoundTag tag = stack.getTag();
    if (tag == null) return new FacadeState[0];
    if (tag.contains("states", Tag.TAG_LIST)) {
      ListTag list = tag.getList("states", Tag.TAG_COMPOUND);
      FacadeState[] states = new FacadeState[list.size()];
      for (int i = 0; i < list.size(); i++) {
        states[i] = FacadeState.fromNbt(list.getCompound(i));
      }
      return states;
    } else if (tag.contains("block")) {
      return new FacadeState[]{FacadeState.fromNbt(tag)};
    }
    return new FacadeState[0];
  }

  @Override
  public ItemStack getFacadeForBlock(BlockState state) {
    ItemStack stack = new ItemStack(this);
    setFacadeStates(stack, new FacadeState[]{new FacadeState(state, null, false)});
    return stack;
  }

  @Override
  public List<BlockState> getBlockStatesForFacade(ItemStack stack) {
    return Arrays.stream(getFacadeStates(stack))
      .map(FacadeState::state)
      .filter(Objects::nonNull)
      .collect(Collectors.toList());
  }

  public void setFacadeStates(ItemStack stack, FacadeState[] states) {
    CompoundTag tag = stack.getOrCreateTag();
    if (states.length == 1 && states[0].wire == null) {
      states[0].writeToNbt(tag);
      tag.putByte("type", (byte) FacadeType.Basic.ordinal());
    } else {
      ListTag list = new ListTag();
      for (FacadeState state : states) {
        CompoundTag stateTag = new CompoundTag();
        state.writeToNbt(stateTag);
        list.add(stateTag);
      }
      tag.put("states", list);
      tag.putByte("type", (byte) FacadeType.Phased.ordinal());
    }
  }

  public record FacadeState(
    @Nullable BlockState state,
    @Nullable PipeWire wire,
    boolean hollow
  ) {
    public static FacadeState fromNbt(CompoundTag nbt) {
      BlockState state = null;
      if (nbt.contains("block", Tag.TAG_STRING)) {
        try {
          String stateStr = nbt.getString("block");
          var result = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), stateStr, true);
          state = result.blockState();
        } catch (Exception ignored) {
        }
      }
      PipeWire wire = nbt.contains("wire") ? PipeWire.fromOrdinal(nbt.getByte("wire")) : null;
      boolean hollow = nbt.getBoolean("hollow");
      return new FacadeState(state, wire, hollow);
    }

    public void writeToNbt(CompoundTag nbt) {
      if (state != null) {
        nbt.putString("block", BlockStateParser.serialize(state));
      }
      if (wire != null) {
        nbt.putByte("wire", (byte) wire.ordinal());
      }
      if (hollow) {
        nbt.putBoolean("hollow", true);
      }
    }

    public Component getDisplayName() {
      if (state == null) {
        return Component.translatable("item.bcreborntransport.facade.state_transparent");
      }
      Component name = state.getBlock().getName();
      if (hollow) {
        return Component.translatable("item.bcreborntransport.facade.state_hollow_format", name);
      }
      return name;
    }
  }
}
