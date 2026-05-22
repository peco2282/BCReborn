package com.peco2282.bcreborn.api.statements.containers;

import com.peco2282.bcreborn.api.statements.IStatementContainer;
import net.minecraft.core.Direction;

/**
 * Created by asie on 3/14/15.
 */
public interface ISidedStatementContainer extends IStatementContainer {
	Direction getSide();
}
