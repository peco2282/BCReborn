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
package com.peco2282.bcreborn.transport;

import com.peco2282.bcreborn.BCRebornTransport;
import com.peco2282.bcreborn.api.core.EnumColor;
import com.peco2282.bcreborn.api.statements.IStatement;
import com.peco2282.bcreborn.api.transport.PipeWire;
import com.peco2282.bcreborn.api.transport.PowerMode;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.common.bean.InitRegister;
import com.peco2282.bcreborn.common.registry.KeyedRegistryObject;
import com.peco2282.bcreborn.common.registry.RegistryBooleanObject;
import com.peco2282.bcreborn.common.registry.RegistryEnumObject;
import com.peco2282.bcreborn.common.registry.RegistrySizedObject;
import com.peco2282.bcreborn.transport.statements.*;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

@InitRegister(modId = BCRebornTransport.MODID)
public class TransportStatements {
  private static final BCRegistry REGISTRY = BCRebornTransport.getRegistry();

  public static final RegistryObject<ActionEnergyPulsar> ACTION_ENERGY_PULSAR = register("action_energy_pulsar", ActionEnergyPulsar::new);
  public static final RegistryEnumObject<EnumColor, ActionExtractionPreset> ACTION_EXTRACTION_PRESET = registerEnum("action_extraction_preset", ActionExtractionPreset::new, EnumColor.class);
  public static final RegistryEnumObject<EnumColor, ActionPipeColor> ACTION_PARAMETER_SIGNAL = registerEnum("action_parameter_signal", ActionPipeColor::new, EnumColor.class);
  public static final RegistryEnumObject<Direction, ActionPipeDirection> ACTION_PIPE_DIRECTION = registerEnum("action_pipe_direction", ActionPipeDirection::new, Direction.class);
  public static final RegistryEnumObject<PowerMode, ActionPowerLimiter> ACTION_POWER_LIMITER = registerEnum("action_power_limiter", ActionPowerLimiter::new, PowerMode.class);
  public static final RegistrySizedObject<ActionRedstoneFaderOutput> ACTION_REDSTONE_FADER_OUTPUT = registerSized("action_redstone_fader_output", ActionRedstoneFaderOutput::new, 15);
  public static final RegistryEnumObject<PipeWire, ActionSignalOutput> ACTION_SIGNAL_OUTPUT = registerEnum("action_signal_output", ActionSignalOutput::new, PipeWire.class);
  public static final RegistryObject<ActionSingleEnergyPulse> ACTION_SINGLE_ENERGY_PULSE = register("action_single_energy_pulse", ActionSingleEnergyPulse::new);
  public static final RegistryEnumObject<ActionValve.ValveState, ActionValve> ACTION_VALVE = registerEnum("action_valve", ActionValve::new, ActionValve.ValveState.class);
  public static final RegistryEnumObject<TriggerClockTimer.Time, TriggerClockTimer> TRIGGER_CLOCK_TIMER = registerEnum("trigger_clock_timer", TriggerClockTimer::new, TriggerClockTimer.Time.class);
  public static final RegistryBooleanObject<TriggerLightSensor> TRIGGER_LIGHT_SENSOR = registerBoolean("trigger_light_sensor", TriggerLightSensor::new, "bright", "dark");
  public static final RegistryEnumObject<TriggerPipeContents.PipeContents, TriggerPipeContents> TRIGGER_PIPE_CONTENTS = registerEnum("trigger_pipe_contents", TriggerPipeContents::new, TriggerPipeContents.PipeContents.class);
  public static final KeyedRegistryObject.TwoKeys<TriggerPipeSignal, Boolean, PipeWire> TRIGGER_PIPE_SIGNAL = KeyedRegistryObject.two(
    List.of(true, false),
    Arrays.stream(PipeWire.class.getEnumConstants()).toList(),
    (k1, k2) -> "trigger_pipe_signal" + k2.name().toLowerCase(Locale.ENGLISH) + (k1 ? "_active" : "_inactive"),
    TransportStatements::register,
    TriggerPipeSignal::new
  );

  public static final RegistrySizedObject<TriggerRedstoneFaderInput> TRIGGER_REDSTONE_FADER_INPUT = registerSized("trigger_redstone_fader_input", TriggerRedstoneFaderInput::new, 15);

  private static <S extends IStatement> RegistryObject<S> register(String name, Supplier<S> supplier) {
    return REGISTRY.registerStatement(name, supplier);
  }

  private static <S extends IStatement, E extends Enum<E> & StringRepresentable> RegistryEnumObject<E, S> registerEnum(String name, Function<E, S> mapper, Class<E> enumClass) {
    RegistryEnumObject<E, S> map = RegistryEnumObject.create(enumClass);
    for (E e : enumClass.getEnumConstants()) {
      map.put(e, register(name + "_" + e.getSerializedName().toLowerCase(Locale.ENGLISH), () -> mapper.apply(e)));
    }
    return map;
  }

  private static <S extends IStatement> RegistrySizedObject<S> registerSized(String name, IntFunction<S> supplier, int size) {
    RegistrySizedObject<S> map = RegistrySizedObject.create(size);
    for (int i = 0; i < size; i++) {
      int finalI = i;
      map.put(i, register(name + "_" + i, () -> supplier.apply(finalI)));
    }
    return map;
  }

  private static <S extends IStatement> RegistryBooleanObject<S> registerBoolean(String name, Function<Boolean, S> supplier, String trueSuffix, String falseSuffix) {
    return RegistryBooleanObject.create(
      register(name + "_" + trueSuffix, () -> supplier.apply(true)),
      register(name + "_" + falseSuffix, () -> supplier.apply(false))
    );
  }
}
