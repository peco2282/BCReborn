package com.peco2282.bcreborn.builders.block.entity;

import com.peco2282.bcreborn.builders.BlockEntityTypesBuilders;
import com.peco2282.bcreborn.common.SimpleInventory;
import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import com.peco2282.bcreborn.common.blueprint.BlueprintReadConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class ArchitectBlockEntity extends BuildCraftBlockEntity implements MenuProvider {
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setReadConfiguration(BlueprintReadConfiguration config) {
    this.readConfiguration = config;
  }

  @Override
  protected void tick(Level level, BlockPos pos, BlockState state) {

  }

  public enum Mode {
    NONE, EDIT, COPY
  }

  public String currentAuthorName = "";
  public Mode mode = Mode.NONE;

//  public Box box = new Box();
  private String name = "";
  public BlueprintReadConfiguration readConfiguration = new BlueprintReadConfiguration();

  private SimpleInventory inv = new SimpleInventory(2, "Architect", 1);
  private boolean clientIsWorking, initialized;
  public ArchitectBlockEntity(BlockPos pos, BlockState state) {
    super(BlockEntityTypesBuilders.ARCHITECT.get(), pos, state);
  }


  @Override
  public @NotNull Component getDisplayName() {
    return Component.translatable("container.bcrebornbuilders.architect");
  }

  @Override
  public @Nullable AbstractContainerMenu createMenu(int windowId, @NotNull Inventory inventory, @NotNull Player player) {
    return null;
  }
}
