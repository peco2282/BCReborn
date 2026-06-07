/*
 * BC Reborn
 *
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * Copyright (c) 2025-2026 peco2282
 *
 * Contains original work and code derived from BuildCraft.
 *
 * Licensed under the Minecraft Mod Public License 1.0 (MMPL).
 * See LICENSE for details.
 */
package com.peco2282.bcreborn.transport.statements;

import com.peco2282.bcreborn.BCRebornTransport;
import com.peco2282.bcreborn.api.statements.IActionInternal;
import com.peco2282.bcreborn.api.statements.IStatementContainer;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.api.transport.IPipe;
import com.peco2282.bcreborn.common.utils.StringUtils;
import com.peco2282.bcreborn.core.statements.BCStatement;
import com.peco2282.bcreborn.core.statements.StatementParameterDirection;
import com.peco2282.bcreborn.transport.Gate;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;
import java.util.function.Function;

public class ActionValve extends BCStatement implements IActionInternal {

  public final ValveState state;

  public ActionValve(ValveState valveState) {
    super("pipe.valve." + valveState.name().toLowerCase(Locale.ENGLISH));
    state = valveState;
  }

  @Override
  public String getDescription() {
    return StringUtils.localize("gate.action.pipe.valve." + state.name().toLowerCase(Locale.ENGLISH));
  }

  @Override
  public int maxParameters() {
    return 1;
  }

  @Override
  public int minParameters() {
    return 0;
  }

  @Override
  public IStatementParameter createParameter(int index) {
    return new StatementParameterDirection();
  }

  @Override
  public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
    icon = textureGetter.apply(BCRebornTransport.location("triggers/action_valve_" + state.name().toLowerCase(Locale.ENGLISH)));
  }

  @Override
  public void actionActivate(IStatementContainer container, IStatementParameter[] parameters) {
    IPipe pipe = ((Gate) container).getPipe();

    if (pipe != null) {
      // TODO: IPipe または PipeBlockEntity に allowInput/allowOutput を実装し、ここで呼び出す
    }
  }

  public enum ValveState implements StringRepresentable {
    OPEN(true, true),
    INPUT_ONLY(true, false),
    OUTPUT_ONLY(false, true),
    CLOSED(false, false);

    public static final ValveState[] VALUES = values();
    public final boolean inputOpen;
    public final boolean outputOpen;

    ValveState(boolean in, boolean out) {
      inputOpen = in;
      outputOpen = out;
    }

    @Override
    public String getSerializedName() {
      return name().toLowerCase(Locale.ENGLISH);
    }
  }
}
