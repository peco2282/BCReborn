package com.peco2282.bcreborn.robotics.boards;

import com.peco2282.bcreborn.api.boards.RedstoneBoardRobot;
import com.peco2282.bcreborn.api.boards.RedstoneBoardRobotNBT;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class RedstoneBoardRobotEmptyNBT extends RedstoneBoardRobotNBT {

	public static RedstoneBoardRobotEmptyNBT instance = new RedstoneBoardRobotEmptyNBT();
	private TextureAtlasSprite icon;

	@Override
	public RedstoneBoardRobot create(Object robot) {
		return null; // TODO: Implement when BoardRobotEmpty is available
	}

	@Override
	public ResourceLocation getRobotTexture() {
		return new ResourceLocation("bcreborn", "textures/entity/robot/robot_base.png");
	}

	@Override
	public String getID() {
		return "buildcraft:boardRobotEmpty";
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
	}

	@Override
	public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
		icon = textureGetter.apply(new ResourceLocation("bcrebornrobotics", "board/clean"));
	}

	@Override
	public TextureAtlasSprite getIcon(CompoundTag nbt) {
		return icon;
	}

}
