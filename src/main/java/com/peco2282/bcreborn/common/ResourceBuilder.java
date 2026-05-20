package com.peco2282.bcreborn.common;

import com.peco2282.bcreborn.*;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class ResourceBuilder {
  private String namespace;
  private String path;

  public ResourceBuilder(String namespace, String path) {
    this.namespace = namespace;
    this.path = path;
  }

  public ResourceBuilder(String namespace) {
    this.namespace = namespace;
    this.path = "";
  }

  public ResourceBuilder(ResourceLocation location) {
    this.namespace = location.getNamespace();
    this.path = location.getPath();
  }

  public ResourceBuilder editNamespace(Consumer<String> consumer) {
    consumer.accept(this.namespace);
    return this;
  }

  public ResourceBuilder editPath(Consumer<String> runnable) {
    runnable.accept(this.path);
    return this;
  }

  public ResourceBuilder addPath(String path) {
    this.path += "/" + (path.startsWith("/") ? path.substring(1) : path);
    return this;
  }

  public ResourceBuilder addPath(ResourceLocation path) {
    return this.addPath(path.getPath());
  }

  public String getPath() {
    return this.path;
  }


  public ResourceLocation build(ResourceType type) {
    var path = this.path.startsWith("/") ? this.path.substring(1) : this.path;
    return ResourceLocation.fromNamespaceAndPath(
        this.namespace,
        type.path + path
    );
  }

  public ModelLayerLocation asModel(String layer) {
    return new ModelLayerLocation(this.build(ResourceType.NO_PREFIX), layer);
  }

  public static ResourceBuilder core() {
    return new ResourceBuilder(BCRebornCore.MODID);
  }

  public static ResourceBuilder core(String path) {
    return new ResourceBuilder(BCRebornCore.MODID, path);
  }

  public static ResourceBuilder builder() {
    return new ResourceBuilder(BCRebornBuilders.MODID);
  }

  public static ResourceBuilder builder(String path) {
    return new ResourceBuilder(BCRebornBuilders.MODID, path);
  }

  public static ResourceBuilder energy() {
    return new ResourceBuilder(BCRebornEnergy.MODID);
  }

  public static ResourceBuilder energy(String path) {
    return new ResourceBuilder(BCRebornEnergy.MODID, path);
  }

  public static ResourceBuilder transport() {
    return new ResourceBuilder(BCRebornTransport.MODID);
  }

  public static ResourceBuilder transport(String path) {
    return new ResourceBuilder(BCRebornTransport.MODID, path);
  }

  public static ResourceBuilder factory() {
    return new ResourceBuilder(BCRebornFactory.MODID);
  }

  public static ResourceBuilder factory(String path) {
    return new ResourceBuilder(BCRebornFactory.MODID, path);
  }

  public static ResourceBuilder silicon() {
    return new ResourceBuilder(BCRebornSilicon.MODID);
  }

  public static ResourceBuilder silicon(String path) {
    return new ResourceBuilder(BCRebornSilicon.MODID, path);
  }

  public static ResourceBuilder robotics() {
    return new ResourceBuilder(BCRebornRobotics.MODID);
  }
  public static ResourceBuilder robotics(String path) {
    return new ResourceBuilder(BCRebornRobotics.MODID, path);
  }

  public static ResourceBuilder create(String path) {
    return new ResourceBuilder(ResourceLocation.parse(path));
  }

  public static ResourceBuilder create(String namespace, String path) {
    return new ResourceBuilder(namespace, path);
  }

  public enum ResourceType {
    BLOCK("textures/block/"),
    ITEM("textures/item/"),
    ENTITY("textures/entity/"),
    GUI("textures/gui/"),
    NO_PREFIX(""),
    ;

    private final String path;

    ResourceType(String path) {
      this.path = path;
    }

    public String getPath() {
      return path;
    }
  }
}
