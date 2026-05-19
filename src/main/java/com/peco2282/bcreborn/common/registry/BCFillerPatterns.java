package com.peco2282.bcreborn.common.registry;

import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.api.filler.IFillerPattern;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class BCFillerPatterns {
  private static <P extends IFillerPattern> RegistryObject<P> register(String name, Supplier<P> supplier) {
    return BCRebornCore.getRegistry().registerFillerPattern(name, supplier);
  }
}
