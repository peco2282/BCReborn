package com.peco2282.bcreborn.api.statements;

import net.minecraft.core.Direction;

public interface ITriggerExternalOverride {
    enum Result {
        TRUE, FALSE, IGNORE
    }

    Result override(Direction side, IStatementContainer source, IStatementParameter[] parameters);
}
