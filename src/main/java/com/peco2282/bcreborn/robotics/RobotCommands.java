package com.peco2282.bcreborn.robotics;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.peco2282.bcreborn.api.robots.RobotEntityBase;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Comparator;
import java.util.List;

public class RobotCommands {
  public static LiteralArgumentBuilder<CommandSourceStack> command() {
    return literal("robot")
        .then(
            literal("create")
                .executes(RobotCommands::create)
        )
        .then(
            literal("delete")
                .executes(RobotCommands::delete)
        )
        .then(
            literal("list")
                .then(argument("sortBy", StringArgumentType.string())
                    .executes(RobotCommands::listBy)
                    .then(argument("pagent", IntegerArgumentType.integer(0)).executes(RobotCommands::listByPaged))
                )
        );
  }

  static LiteralArgumentBuilder<CommandSourceStack> literal(String name) {
    return LiteralArgumentBuilder.literal(name);
  }

  static <T> RequiredArgumentBuilder<CommandSourceStack, T> argument(String name, ArgumentType<T> argumentType) {
    return RequiredArgumentBuilder.argument(name, argumentType);
  }

  static int create(CommandContext<CommandSourceStack> context) {
    return Command.SINGLE_SUCCESS;
  }

  static int delete(CommandContext<CommandSourceStack> context) {
    return Command.SINGLE_SUCCESS;
  }

  static int listBy(CommandContext<CommandSourceStack> context) {
    var entities = sorted(context);
    CommandSourceStack source = context.getSource();
    int pageSize = 10;
    int to = Math.min(pageSize, entities.size());

    if (entities.isEmpty()) {
      source.sendFailure(Component.literal("Page out of range"));
      return 0;
    }

    var top10 = entities.subList(0, to);
    for (var entity : top10) {
      source.sendSuccess(() -> entity.getDisplayName().append("RobotId #" + entity.getRobotId()).append(" % RobotType: " + entity.getBoard().getType().id()), false);
    }

    return Command.SINGLE_SUCCESS;
  }

  static int listByPaged(CommandContext<CommandSourceStack> context) {
    var entities = sorted(context);
    var pagent = IntegerArgumentType.getInteger(context, "pagent");
    CommandSourceStack source = context.getSource();
    int pageSize = 10;
    int from = pagent * pageSize;
    int to = Math.min(from + pageSize, entities.size());

    if (from >= entities.size()) {
      source.sendFailure(Component.literal("Page out of range"));
      return 0;
    }

    var disp10 = entities.subList(from, to);
    for (var entity : disp10) {
      source.sendSuccess(() -> entity.getDisplayName().append("RobotId #" + entity.getRobotId()).append(" % RobotType: " + entity.getBoard().getType().id()), false);
    }

    source.sendSuccess(() -> Component.literal("Page " + pagent).append("  ").append(Component.literal("Total: " + entities.size())).append("  ").append(Component.literal("Next").withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/robot list " + (pagent + 1))))), false);

    // todo display
    return Command.SINGLE_SUCCESS;
  }

  static List<? extends RobotEntityBase> sorted(CommandContext<CommandSourceStack> context) {
    var sortBy = StringArgumentType.getString(context, "sortBy");
    var entities = context.getSource().getLevel().getEntities(EntityTypeTest.forClass(RobotEntityBase.class), RobotEntityBase::isAlive);

    Comparator<RobotEntityBase> comparator = (switch (sortBy) {
      case "distance" -> Comparator.comparing(e -> e.distanceToSqr(context.getSource().getPosition()));
      case "type" ->
          Comparator.comparing(e -> {
            var loc = ForgeRegistries.ENTITY_TYPES.getKey(e.getType());
            return loc == null ? "" : loc.toString();
          });
      default -> Comparator.comparingLong(RobotEntityBase::getRobotId);
    });

    entities.sort(comparator);
    return entities;
  }
}
