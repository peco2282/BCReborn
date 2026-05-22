package com.peco2282.bcreborn.builders.blueprints;


import com.peco2282.bcreborn.BCRebornBuilders;
import com.peco2282.bcreborn.api.library.LibraryTypeHandlerNBT;
import com.peco2282.bcreborn.builders.item.BlueprintItem;
import com.peco2282.bcreborn.builders.item.BlueprintStandardItem;
import com.peco2282.bcreborn.builders.item.BlueprintTemplateItem;
import com.peco2282.bcreborn.common.blueprint.BlueprintBase;
import com.peco2282.bcreborn.common.blueprint.LibraryId;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class LibraryBlueprintTypeHandler extends LibraryTypeHandlerNBT {
	private final boolean isBlueprint;

	public LibraryBlueprintTypeHandler(boolean isBlueprint) {
		super(isBlueprint ? "bpt" : "tpl");
		this.isBlueprint = isBlueprint;
	}

	@Override
	public boolean isHandler(ItemStack stack, HandlerType type) {
		if (isBlueprint) {
			return stack.getItem() instanceof BlueprintStandardItem && (type == HandlerType.LOAD || BlueprintItem.isContentReadable(stack));
		} else {
			return stack.getItem() instanceof BlueprintTemplateItem && (type == HandlerType.LOAD || BlueprintItem.isContentReadable(stack));
		}
	}

	@Override
	public int getTextColor() {
		return isBlueprint ? 0x305080 : 0;
	}

	@Override
	public String getName(ItemStack stack) {
		LibraryId id = BlueprintItem.getId(stack);
		return id != null ? id.name : "<<CORRUPT>>";
	}

	@Override
	public ItemStack load(ItemStack stack, CompoundTag compound) {
		BlueprintBase blueprint = BlueprintBase.loadBluePrint(compound.copy());
		blueprint.id.name = compound.getString("__filename");
		blueprint.id.extension = getOutputExtension();
		BCRebornBuilders.getServerDB().add(blueprint.id, compound);
		return blueprint.getStack();
	}

	@Override
	public boolean store(ItemStack stack, CompoundTag compound) {
		LibraryId id = BlueprintItem.getId(stack);
		if (id == null) {
			return false;
		}

		CompoundTag nbt = BCRebornBuilders.getServerDB().load(id);
		if (nbt == null) {
			return false;
		}

		for (String key : nbt.getAllKeys()) {
			compound.put(key, nbt.get(key));
		}
		id.write(compound);
		return true;
	}
}
