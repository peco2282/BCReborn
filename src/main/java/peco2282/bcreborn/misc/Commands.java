package peco2282.bcreborn.misc;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.versions.mcp.MCPVersion;
import peco2282.bcreborn.BCReborn;

import java.util.List;
import java.util.Objects;

public class Commands {

  public Commands() {
  }

  @SubscribeEvent
  public static void registerCommands(RegisterCommandsEvent event) {
    new Commands().register(event.getDispatcher());
  }

  LiteralArgumentBuilder<CommandSourceStack> literal(String command) {
    return LiteralArgumentBuilder.literal(command);
  }

  void register(CommandDispatcher<CommandSourceStack> dispatcher) {
    dispatcher.register(
        literal("bcreborn")
            .then(
                literal("version")
                    .requires(cs -> cs.hasPermission(0))
                    .executes(context -> {
                      List<IModInfo> info = ModList.get().getMods().stream().filter(i -> Objects.equals(i.getModId(), BCReborn.MODID)).toList();
                      if (info.size() > 1)
                        throw new SimpleCommandExceptionType(Component.literal("There is more than one " + BCReborn.MODID + " mod in cntainer")).create();
                      if (info.isEmpty())
                        throw new SimpleCommandExceptionType(Component.literal("Does not exist " + BCReborn.MODID + " mod in container")).create();
                      if (context.getSource().getPlayer() == null) return Command.SINGLE_SUCCESS;
                      VersionChecker.CheckResult result = VersionChecker.getResult(info.getFirst());
                      if (result.status() == VersionChecker.Status.FAILED) {
                        context.getSource().getPlayer().sendSystemMessage(Component.translatable("command.buildcraft.version.fail"));
                      }

                      ChatFormatting style;
                      if (result.status() == VersionChecker.Status.OUTDATED) {
                        style = ChatFormatting.RED;
                      } else {
                        style = ChatFormatting.GREEN;
                      }
                      String currentVersion = BCReborn.VERSION;

                      context.getSource().getPlayer().sendSystemMessage(Component.translatable("command.buildcraft.version", currentVersion, MCPVersion.getMCVersion(), result.target().toString()).withStyle(style));

                      if (currentVersion.toLowerCase().contains("-pre")) {
                        context.getSource().getPlayer().sendSystemMessage(Component.translatable("command.buildcraft.version.prerelease"));
                      }

                      return Command.SINGLE_SUCCESS;
                    })

            )
            .then(
                literal("changelog")
                    .requires(p -> p.hasPermission(0))
                    .executes(context -> {
                          context.getSource().getPlayer().sendSystemMessage(Component.literal("TODO! Implementation"));
                          return Command.SINGLE_SUCCESS;
                        }
                    )
            )
    );
  }
}
