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
package com.peco2282.bcreborn.api.blueprints;

import com.peco2282.bcreborn.api.StackHelper;
import com.peco2282.bcreborn.api.core.IInvSlot;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedList;

/**
 * A schematic is a piece of a blueprint. It allows to stock blocks or entities
 * to blueprints, and can have a state that moves from a blueprint referential
 * to a world referential. Although default schematic behavior will be OK for a
 * lot of objects, specific blocks and entities may be associated with a
 * dedicated schematic class, which will be instantiated automatically.
 * <p>
 * Schematic perform "id translation" in case the block ids between a blueprint
 * and the world installation are different. Mapping is done through the builder
 * context.
 * <p>
 * Detailed documentation on the schematic behavior can be found on
 * http://www.mod-buildcraft.com/wiki/doku.php?id=builder_support
 * <p>
 * Example of schematics for minecraft blocks are available in the package
 * buildcraft.core.schematics.
 */
public abstract class Schematic {
  /**
   * This is called to verify whether the required item is equal to the
   * supplied item.
   * <p>
   * Primarily rely on this for checking metadata/NBT - the item ID
   * itself might have been filtered out by previously running code.
   */
  public boolean isItemMatchingRequirement(ItemStack suppliedStack, ItemStack requiredStack) {
    return StackHelper.isEqualItem(suppliedStack, requiredStack);
  }

  /**
   * Perform a 90 degree rotation to the slot.
   */
  public void rotateLeft(IBuilderContext context) {

  }

  /**
   * Applies translations to all positions in the schematic to center in the
   * blueprint referential
   */
  public void translateToBlueprint(Translation transform) {

  }

  /**
   * Apply translations to all positions in the schematic to center in the
   * builder referential
   */
  public void translateToWorld(Translation transform) {

  }

  /**
   * Translates blocks and item ids to the blueprint referential
   */
  public void idsToBlueprint(MappingRegistry registry) {

  }

  /**
   * Translates blocks and item ids to the world referential
   */
  public void idsToWorld(MappingRegistry registry) {

  }

  /**
   * Initializes a schematic for blueprint according to an objet placed on {x,
   * y, z} on the world. For blocks, block and meta fields will be initialized
   * automatically.
   */
  public void initializeFromObjectAt(IBuilderContext context, int x, int y, int z) {

  }

  /**
   * Places the block in the world, at the location specified in the slot,
   * using the stack in parameters
   */
  public void placeInWorld(IBuilderContext context, int x, int y, int z, LinkedList<ItemStack> stacks) {

  }

  /**
   * Write specific requirements coming from the world to the blueprint.
   */
  public void storeRequirements(IBuilderContext context, int x, int y, int z) {

  }

  /**
   * Returns the requirements needed to build this block. When the
   * requirements are met, they will be removed all at once from the builder,
   * before calling writeToWorld.
   */
  public void getRequirementsForPlacement(IBuilderContext context, LinkedList<ItemStack> requirements) {

  }

  /**
   * Returns the amount of energy required to build this slot, depends on the
   * stacks selected for the build.
   */
  public int getEnergyRequirement(LinkedList<ItemStack> stacksUsed) {
    int result = 0;

    if (stacksUsed != null) {
      for (ItemStack s : stacksUsed) {
        result += s.getCount() * BuilderAPI.BUILD_ENERGY;
      }
    }

    return result;
  }

  /**
   * Returns the flying stacks to display in the builder animation.
   */
  public LinkedList<ItemStack> getStacksToDisplay(
    LinkedList<ItemStack> stackConsumed) {

    return stackConsumed;
  }

  /**
   * Return the stage where this schematic has to be built.
   */
  public BuildingStage getBuildStage() {
    return BuildingStage.STANDALONE;
  }

  /**
   * Return true if the block on the world correspond to the block stored in
   * the blueprint at the location given by the slot. By default, this
   * subprogram is permissive and doesn't take into account metadata.
   * <p>
   * Post processing will be called on these blocks.
   */
  public boolean isAlreadyBuilt(IBuilderContext context, int x, int y, int z) {
    return true;
  }

  /**
   * Return true if the block should not be placed to the world. Requirements
   * will not be asked on such a block, and building will not be called.
   * <p>
   * Post processing will be called on these blocks.
   */
  public boolean doNotBuild() {
    return false;
  }

  /**
   * Return true if the schematic should not be used at all. This is computed
   * straight after readFromNBT can be used to deactivate schematics in which
   * an inconsistency is detected. It will be considered as a block of air
   * instead.
   * <p>
   * Post processing will *not* be called on these blocks.
   */
  public boolean doNotUse() {
    return false;
  }

  /**
   * Return the maximium building permission for blueprint containing this
   * schematic.
   */
  public BuildingPermission getBuildingPermission() {
    return BuildingPermission.ALL;
  }

  /**
   * Called on a block when the blueprint has finished to place all the
   * blocks. This may be useful to adjust variable depending on surrounding
   * blocks that may not be there already at initial building.
   */
  public void postProcessing(IBuilderContext context, int x, int y, int z) {

  }

  /**
   * Saves this schematic to the blueprint NBT.
   */
  public void writeSchematicToNBT(CompoundTag nbt, MappingRegistry registry) {

  }

  /**
   * Loads this schematic from the blueprint NBT.
   */
  public void readSchematicFromNBT(CompoundTag nbt, MappingRegistry registry) {

  }

  /**
   * Returns the number of cycles to wait after building this schematic. Tiles
   * and entities typically require more wait, around 5 cycles.
   */
  public ItemStack useItem(IBuilderContext context, ItemStack requiredStack, IInvSlot slotInv) {
    ItemStack stack = slotInv.getStackInSlot();
    if (requiredStack.isEmpty() || isItemMatchingRequirement(stack, requiredStack)) {
      return slotInv.decreaseStackInSlot(1);
    }
    return ItemStack.EMPTY;
  }

  public int buildTime() {
    return 1;
  }

  /**
   * Blocks are build in various stages, in order to make sure that a block
   * can indeed be placed, and that it's unlikely to disturb other blocks.
   */
  public enum BuildingStage implements StringRepresentable {
    /**
     * Standalone blocks do not change once placed.
     */
    STANDALONE,

    /**
     * Expanding blocks will grow and may disturb other block locations,
     * like liquids.
     */
    EXPANDING;

    @Override
    public String getSerializedName() {
      return name().toLowerCase();
    }
  }
}
