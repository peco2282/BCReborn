package com.peco2282.bcreborn.silicon;

import com.peco2282.bcreborn.BCRebornSilicon;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.silicon.entity.PackageEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class SiliconEntityTypes {
    private static final BCRegistry REGISTRY = BCRebornSilicon.getRegistry();

    public static final RegistryObject<EntityType<PackageEntity>> PACKAGE = register("package", PackageEntity::new, MobCategory.MISC);

    private static <E extends Entity> RegistryObject<EntityType<E>> register(String name, EntityType.EntityFactory<E> factory, MobCategory category) {
        return REGISTRY.registerEntityType(name, of(factory, category, name));
    }

    private static <E extends Entity> Supplier<EntityType<E>> of(EntityType.EntityFactory<E> factory, MobCategory category, String name) {
        return () -> EntityType.Builder.of(factory, category).build(name);
    }
}
