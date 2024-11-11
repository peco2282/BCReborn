package peco2282.bcreborn.api.block;

import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import peco2282.bcreborn.api.enums.*;

import java.util.Map;

/**
 * BlockState-properties
 * @author peco2282
 */
public interface BCProperties {
  DirectionProperty BLOCK_FACING = DirectionProperty.create("facing");
  DirectionProperty BLOCK_FACING_6 = DirectionProperty.create("facing");

  EnumProperty<DyeColor> BLOCK_COLOR = EnumProperty.create("color", DyeColor.class);
  EnumProperty<EnumSpring> SPRING_TYPE = EnumProperty.create("type", EnumSpring.class);
  EnumProperty<EnumEngineType> ENGINE_TYPE = EnumProperty.create("type", EnumEngineType.class);
  EnumProperty<EnumLaserTableType> LASER_TABLE_TYPE = EnumProperty.create("type", EnumLaserTableType.class);
  EnumProperty<EnumMachineState> MACHINE_STATE = EnumProperty.create("state", EnumMachineState.class);
  EnumProperty<EnumPowerStage> ENERGY_STAGE = EnumProperty.create("stage", EnumPowerStage.class);
  EnumProperty<EnumOptionalSnapshotType> SNAPSHOT_TYPE = EnumProperty.create("snapshot_type", EnumOptionalSnapshotType.class);
  EnumProperty<EnumDecoratedBlock> DECORATED_BLOCK = EnumProperty.create("decoration_type", EnumDecoratedBlock.class);
  EnumProperty<EnumFillerType> FILLER_TYPE = EnumProperty.create("filler", EnumFillerType.class);

  IntegerProperty GENERIC_PIPE_DATA = IntegerProperty.create("pipe_data", 0, 15);
  IntegerProperty LED_POWER = IntegerProperty.create("led_power", 0, 3);
  IntegerProperty ENGINE_MODEL = IntegerProperty.create("engine_model", 1, 9);

  BooleanProperty JOINED_BELOW = BooleanProperty.create("joined_below");
  BooleanProperty MOVING = BooleanProperty.create("moving");
  BooleanProperty LED_DONE = BooleanProperty.create("led_done");
  BooleanProperty ACTIVE = BooleanProperty.create("active");
  BooleanProperty VALID = BooleanProperty.create("valid");
  BooleanProperty CONNECTED = BooleanProperty.create("connected");

  BooleanProperty CONNECTED_UP = BooleanProperty.create("connected_up");
  BooleanProperty CONNECTED_DOWN = BooleanProperty.create("connected_down");
  BooleanProperty CONNECTED_EAST = BooleanProperty.create("connected_east");
  BooleanProperty CONNECTED_WEST = BooleanProperty.create("connected_west");
  BooleanProperty CONNECTED_NORTH = BooleanProperty.create("connected_north");
  BooleanProperty CONNECTED_SOUTH = BooleanProperty.create("connected_south");

  Map<Direction, BooleanProperty> CONNECTED_MAP = Util.make(() -> {
    Map<Direction, BooleanProperty> map;
    map = Maps.newEnumMap(Direction.class);
    map.put(Direction.DOWN, CONNECTED_DOWN);
    map.put(Direction.UP, CONNECTED_UP);
    map.put(Direction.EAST, CONNECTED_EAST);
    map.put(Direction.WEST, CONNECTED_WEST);
    map.put(Direction.NORTH, CONNECTED_NORTH);
    map.put(Direction.SOUTH, CONNECTED_SOUTH);
    return Maps.immutableEnumMap(map);
  });

}
