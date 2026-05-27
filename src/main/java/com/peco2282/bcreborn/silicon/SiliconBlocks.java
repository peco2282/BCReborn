package com.peco2282.bcreborn.silicon;

import com.peco2282.bcreborn.BCRebornSilicon;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.common.bean.InitRegister;
import com.peco2282.bcreborn.silicon.block.LaserBlock;
import com.peco2282.bcreborn.silicon.block.LaserTableBlock;
import com.peco2282.bcreborn.silicon.block.PackagerBlock;
import com.peco2282.bcreborn.silicon.item.LaserTableItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

@InitRegister(modId = BCRebornSilicon.MODID)
public class SiliconBlocks {
    private static final BCRegistry REGISTRY = BCRebornSilicon.getRegistry();

    public static final RegistryObject<LaserBlock> LASER = register("laser", LaserBlock::new);
    public static final RegistryObject<LaserTableBlock> ASSEMBLY_TABLE = register("assembly_table", (block, props) -> new LaserTableItem(block), LaserTableBlock::new);
    public static final RegistryObject<LaserTableBlock> ADVANCED_CRAFTING_TABLE = register("advanced_crafting_table", (block, props) -> new LaserTableItem(block), LaserTableBlock::new);
    public static final RegistryObject<LaserTableBlock> INTEGRATION_TABLE = register("integration_table", (block, props) -> new LaserTableItem(block), LaserTableBlock::new);
    public static final RegistryObject<LaserTableBlock> CHARGING_TABLE = register("charging_table", (block, props) -> new LaserTableItem(block), LaserTableBlock::new);
    public static final RegistryObject<LaserTableBlock> PROGRAMMING_TABLE = register("programming_table", (block, props) -> new LaserTableItem(block), LaserTableBlock::new);
    public static final RegistryObject<LaserTableBlock> STAMPING_TABLE = register("stamping_table", (block, props) -> new LaserTableItem(block), LaserTableBlock::new);
    public static final RegistryObject<PackagerBlock> PACKAGER = register("packager", PackagerBlock::new);

    private static <B extends Block, BI extends LaserTableItem> RegistryObject<B> register(String name, BCRegistry.BlockItemSupplier<BI> item, Supplier<B> blockSupplier) {
        return REGISTRY.registerBlockItem(name, item, blockSupplier);
    }

    private static <B extends Block> RegistryObject<B> register(String name, Supplier<B> blockSupplier) {
        return REGISTRY.registerBlockItem(name, blockSupplier);
    }
}
