package peco2282.bcreborn.model;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;

public class ResourceContext {
  private Set<ResourceLocation> done;
  private final Deque<ResourceLocation> loading = new ArrayDeque<>();
  public ResourceContext() {
  }

  public InputStreamReader open(ResourceLocation location) throws IOException {
    if (!done.add(location)) throw new IOException(location.toString() + " already loaded");
    ResourceManager manager = Minecraft.getInstance().getResourceManager();
    loading.push(location);
    return new InputStreamReader(manager.open(location), StandardCharsets.UTF_8);
  }

  public void done() {
    loading.pop();
  }
}
