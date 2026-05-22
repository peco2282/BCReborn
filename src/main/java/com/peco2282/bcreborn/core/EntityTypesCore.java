package com.peco2282.bcreborn.core;

import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.common.bean.InitRegister;
import com.peco2282.bcreborn.common.block.EntityBlock;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.RegistryObject;

@InitRegister(modId = BCRebornCore.MODID, priority = 1)
public class EntityTypesCore {
    private static final BCRegistry REGISTRY = BCRebornCore.getRegistry();

    public static final RegistryObject<EntityType<EntityBlock>> ENTITY_BLOCK = REGISTRY.registerEntityType("entity_block",
            () -> EntityType.Builder.<EntityBlock>of(EntityBlock::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .noSummon()
                    .fireImmune()
                    .build("entity_block"));
}
