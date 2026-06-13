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
 * A schematic is a component of a blueprint. It handles the storage and placement of blocks or entities.
 * <p>
 * Schematics manage coordinate and ID translations between the blueprint and the world.
 * Dedicated schematic classes can be associated with specific blocks or entities for custom behavior.
 */
public abstract class Schematic {
  /**
   * Checks if a supplied item stack matches a required item stack for building.
   *
   * @param suppliedStack The stack provided for building.
   * @param requiredStack The stack required by the schematic.
   * @return True if they match.
   */
  public boolean isItemMatchingRequirement(ItemStack suppliedStack, ItemStack requiredStack) {
    return StackHelper.isEqualItem(suppliedStack, requiredStack);
  }

  /**
   * Rotates the schematic 90 degrees to the left.
   *
   * @param context The builder context.
   */
  public void rotateLeft(IBuilderContext context) {

  }

  /**
   * Applies translations to center the schematic in the blueprint referential.
   *
   * @param transform The translation transform.
   */
  public void translateToBlueprint(Translation transform) {

  }

  /**
   * Applies translations to center the schematic in the world referential.
   *
   * @param transform The translation transform.
   */
  public void translateToWorld(Translation transform) {

  }

  /**
   * Translates block and item IDs to the blueprint referential.
   *
   * @param registry The mapping registry.
   */
  public void idsToBlueprint(MappingRegistry registry) {

  }

  /**
   * Translates block and item IDs to the world referential.
   *
   * @param registry The mapping registry.
   */
  public void idsToWorld(MappingRegistry registry) {

  }

  /**
   * Initializes the schematic from an object at the specified world coordinates.
   *
   * @param context The builder context.
   * @param x       The x-coordinate.
   * @param y       The y-coordinate.
   * @param z       The z-coordinate.
   */
  public void initializeFromObjectAt(IBuilderContext context, int x, int y, int z) {

  }

  /**
   * Places the block or entity in the world at the specified coordinates.
   *
   * @param context The builder context.
   * @param x       The x-coordinate.
   * @param y       The y-coordinate.
   * @param z       The z-coordinate.
   * @param stacks  The item stacks used for placement.
   */
  public void placeInWorld(IBuilderContext context, int x, int y, int z, LinkedList<ItemStack> stacks) {

  }

  /**
   * Stores building requirements from the world into the schematic.
   *
   * @param context The builder context.
   * @param x       The x-coordinate.
   * @param y       The y-coordinate.
   * @param z       The z-coordinate.
   */
  public void storeRequirements(IBuilderContext context, int x, int y, int z) {

  }

  /**
   * Gets the requirements needed to build this component.
   *
   * @param context      The builder context.
   * @param requirements The list to add requirements to.
   */
  public void getRequirementsForPlacement(IBuilderContext context, LinkedList<ItemStack> requirements) {

  }

  /**
   * Gets the amount of energy required to build this component.
   *
   * @param stacksUsed The stacks used for the build.
   * @return The energy requirement.
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
   * Gets the stacks to display in the builder's animation.
   *
   * @param stackConsumed The stacks consumed during the build.
   * @return A list of stacks to display.
   */
  public LinkedList<ItemStack> getStacksToDisplay(
    LinkedList<ItemStack> stackConsumed) {

    return stackConsumed;
  }

  /**
   * Gets the building stage for this schematic.
   *
   * @return The {@link BuildingStage}.
   */
  public BuildingStage getBuildStage() {
    return BuildingStage.STANDALONE;
  }

  /**
   * Checks if the component is already built at the specified world position.
   *
   * @param context The builder context.
   * @param x       The x-coordinate.
   * @param y       The y-coordinate.
   * @param z       The z-coordinate.
   * @return True if it is already built.
   */
  public boolean isAlreadyBuilt(IBuilderContext context, int x, int y, int z) {
    return true;
  }

  /**
   * Checks if the component should not be built in the world.
   *
   * @return True if building should be skipped.
   */
  public boolean doNotBuild() {
    return false;
  }

  /**
   * Checks if the schematic should not be used at all.
   * This is typically used to deactivate schematics when an inconsistency is detected.
   *
   * @return True if it should not be used.
   */
  public boolean doNotUse() {
    return false;
  }

  /**
   * Gets the building permission for this schematic.
   *
   * @return The {@link BuildingPermission}.
   */
  public BuildingPermission getBuildingPermission() {
    return BuildingPermission.ALL;
  }

  /**
   * Called after the builder has finished placing all blocks in the blueprint.
   * Use this to adjust state based on neighbor blocks.
   *
   * @param context The builder context.
   * @param x       The x-coordinate.
   * @param y       The y-coordinate.
   * @param z       The z-coordinate.
   */
  public void postProcessing(IBuilderContext context, int x, int y, int z) {

  }

  /**
   * Writes the schematic data to an NBT tag.
   *
   * @param nbt      The NBT tag.
   * @param registry The mapping registry.
   */
  public void writeSchematicToNBT(CompoundTag nbt, MappingRegistry registry) {

  }

  /**
   * Reads the schematic data from an NBT tag.
   *
   * @param nbt      The NBT tag.
   * @param registry The mapping registry.
   */
  public void readSchematicFromNBT(CompoundTag nbt, MappingRegistry registry) {

  }

  /**
   * Consumes an item from an inventory slot for building.
   *
   * @param context       The builder context.
   * @param requiredStack The stack required.
   * @param slotInv       The inventory slot.
   * @return The stack taken from the slot, or {@link ItemStack#EMPTY} if no item was taken.
   */
  public ItemStack useItem(IBuilderContext context, ItemStack requiredStack, IInvSlot slotInv) {
    ItemStack stack = slotInv.getStackInSlot();
    if (requiredStack.isEmpty() || isItemMatchingRequirement(stack, requiredStack)) {
      return slotInv.decreaseStackInSlot(1);
    }
    return ItemStack.EMPTY;
  }

  /**
   * Gets the time required to build this schematic (in builder cycles).
   *
   * @return The build time.
   */
  public int buildTime() {
    return 1;
  }

  /**
   * Represents the building stage of a schematic.
   */
  public enum BuildingStage implements StringRepresentable {
    /**
     * Standalone blocks do not change once placed.
     */
    STANDALONE,

    /**
     * Expanding blocks may grow and disturb other block locations (e.g., liquids).
     */
    EXPANDING;

    @Override
    public String getSerializedName() {
      return name().toLowerCase();
    }
  }
}
