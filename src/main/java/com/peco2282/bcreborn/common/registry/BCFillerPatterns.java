package com.peco2282.bcreborn.common.registry;

import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.api.filler.IFillerPattern;
import com.peco2282.bcreborn.common.RegistryUtil;
import com.peco2282.bcreborn.common.bean.InitRegister;
import com.peco2282.bcreborn.common.builder.patterns.*;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@InitRegister(modId = BCRebornCore.MODID)
public class BCFillerPatterns {
  public static final RegistryObject<PatternBox> BOX = register("box", PatternBox::new);
  public static final RegistryObject<PatternClear> CLEAR = register("clear", PatternClear::new);
  public static final RegistryObject<PatternCylinder> CYLINDER = register("cylinder", PatternCylinder::new);
  public static final RegistryObject<PatternFill> FILL = register("fill", PatternFill::new);
  public static final RegistryObject<PatternFlatten> FLATTEN = register("flatten", PatternFlatten::new);
  public static final RegistryObject<PatternFrame> FRAME = register("frame", PatternFrame::new);
  public static final RegistryObject<PatternHorizon> HORIZON = register("horizon", PatternHorizon::new);
  public static final RegistryObject<PatternPyramid> PYRAMID = register("pyramid", PatternPyramid::new);
  public static final RegistryObject<PatternStairs> STAIRS = register("stairs", PatternStairs::new);
  private static <P extends IFillerPattern> RegistryObject<P> register(String name, Supplier<P> supplier) {
    return BCRebornCore.getRegistry().registerFillerPattern(name, supplier);
  }

  public static List<Map.Entry<ResourceKey<IFillerPattern>, IFillerPattern>> collection() {
    return RegistryUtil.getFillerPatterns().stream().sorted(Map.Entry.comparingByKey()).toList();
  }

  public static IFillerPattern getCurrentlySelected(int index) {
    var collection = collection();
    if (index < 0) {
      index = 0;
    }
    if (index >= collection.size()) {
      index = collection.size() - 1;
    }
    return collection.get(index).getValue();
  }
}
