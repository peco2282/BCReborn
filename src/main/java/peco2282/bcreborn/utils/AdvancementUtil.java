package peco2282.bcreborn.utils;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class AdvancementUtil {
  public static void unlockAdvancement(Player player, ResourceLocation advancementName) {
    if (player instanceof ServerPlayer playerMP) {
      ServerAdvancementManager advancementManager = playerMP.getServer().getAdvancements();
      AdvancementHolder advancement = advancementManager.get(advancementName);
      if (advancement != null) {
        // never assume the advancement exists, we create them but they are removable by datapacks
        PlayerAdvancements tracker = playerMP.getAdvancements();
        // When the fake player gets constructed it will set itself to the main player advancement tracker
        // (So this just harmlessly removes it)
        tracker.setPlayer(playerMP);
        tracker.award(advancement, "code_trigger");
      }
    }
  }
}
