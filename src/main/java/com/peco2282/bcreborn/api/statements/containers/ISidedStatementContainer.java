package com.peco2282.bcreborn.api.statements.containers;

import com.peco2282.bcreborn.api.statements.IStatementContainer;
import net.minecraft.core.Direction;

public interface ISidedStatementContainer extends IStatementContainer {
    Direction getSide();
}
