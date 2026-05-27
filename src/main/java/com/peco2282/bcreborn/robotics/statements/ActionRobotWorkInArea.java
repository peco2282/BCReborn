package com.peco2282.bcreborn.robotics.statements;

import com.peco2282.bcreborn.api.core.IZone;
import com.peco2282.bcreborn.api.items.IMapLocation;
import com.peco2282.bcreborn.api.statements.IActionInternal;
import com.peco2282.bcreborn.api.statements.IStatementContainer;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.api.statements.StatementSlot;
import com.peco2282.bcreborn.common.utils.StringUtils;
import com.peco2282.bcreborn.core.statements.BCStatement;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;

public class ActionRobotWorkInArea extends BCStatement implements IActionInternal {

	public enum AreaType {
		WORK("work_in_area"),
		LOAD_UNLOAD("load_unload_area");

		private final String name;

		AreaType(String iName) {
			name = iName;
		}

		public String getTag() {
			return "buildcraft:robot." + name;
		}

		public String getUnlocalizedName() {
			return "gate.action.robot." + name;
		}

		public String getIcon() {
			return "triggers/action_robot_" + name;
		}
	}

	private final AreaType areaType;

	public ActionRobotWorkInArea(AreaType iAreaType) {
		super(iAreaType.getTag());

		areaType = iAreaType;
	}

	@Override
	public String getDescription() {
		return StringUtils.localize(areaType.getUnlocalizedName());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
		icon = textureGetter.apply(new ResourceLocation("bcrebornrobotics", areaType.getIcon()));
	}

	public static IZone getArea(StatementSlot slot) {
		if (slot.parameters[0] == null) {
			return null;
		}

		ItemStack stack = slot.parameters[0].getItemStack();

		if (stack.isEmpty() || !(stack.getItem() instanceof IMapLocation)) {
			return null;
		}

		IMapLocation map = (IMapLocation) stack.getItem();
		return map.getZone(stack);
	}

	@Override
	public int minParameters() {
		return 1;
	}

	@Override
	public int maxParameters() {
		return 1;
	}

	@Override
	public IStatementParameter createParameter(int index) {
		return new StatementParameterMapLocation();
	}

	@Override
	public void actionActivate(IStatementContainer source, IStatementParameter[] parameters) {
	}

	public AreaType getAreaType() {
		return areaType;
	}
}
