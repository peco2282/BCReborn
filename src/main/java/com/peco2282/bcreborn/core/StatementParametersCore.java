package com.peco2282.bcreborn.core;

import com.peco2282.bcreborn.BCReborn;
import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.api.statements.StatementParameterItemStack;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.common.bean.InitRegister;
import com.peco2282.bcreborn.core.statements.StatementParameterDirection;
import com.peco2282.bcreborn.core.statements.StatementParameterItemStackExact;
import com.peco2282.bcreborn.core.statements.StatementParameterRedstoneGateSideOnly;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

@InitRegister(modId = BCRebornCore.MODID)
public class StatementParametersCore {
  private static final BCRegistry REGISTRY = BCRebornCore.getRegistry();

  public static final RegistryObject<StatementParameterItemStack> STACK_TRIGGER = register("stack_trigger", () -> new StatementParameterItemStack(BCReborn.getBasedLocation("stack_trigger")));
  public static final RegistryObject<StatementParameterItemStack> STACK_ACTION = register("stack_action", () -> new StatementParameterItemStack(BCReborn.getBasedLocation("stack_action")));
  public static final RegistryObject<StatementParameterItemStack> STACK = register("stack", StatementParameterItemStack::new);
  public static final RegistryObject<StatementParameterItemStackExact> STACK_EXACT = register("stack_exact", StatementParameterItemStackExact::new);
  public static final RegistryObject<StatementParameterDirection> DIRECTION = register("direction", StatementParameterDirection::new);
  public static final RegistryObject<StatementParameterRedstoneGateSideOnly> REDSTONE_GATE_SIDE_ONLY = register("redstone_gate_side_only", StatementParameterRedstoneGateSideOnly::new);

  private static <S extends IStatementParameter> RegistryObject<S> register(String name, Supplier<S> statement) {
    return REGISTRY.registerStatementParameter(name, statement);
  }
}
