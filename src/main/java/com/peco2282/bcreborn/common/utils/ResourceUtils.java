package com.peco2282.bcreborn.common.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;

public final class ResourceUtils {
	private ResourceUtils() {

	}

	public static TextureAtlasSprite getIconPriority(Function<ResourceLocation, TextureAtlasSprite> register, String prefix, String[] suffixes) {
		for (int i = 0; i < suffixes.length; i++) {
			String suffix = suffixes[i];
			String path = prefix + "/" + suffix;
			if (i == suffixes.length - 1 || resourceExists(iconToResourcePath(path))) {
				return register.apply(ResourceLocation.parse(path));
			}
		}
		return null;
	}

	public static TextureAtlasSprite getIcon(Function<ResourceLocation, TextureAtlasSprite> register, String prefix, String suffix) {
		return register.apply(ResourceLocation.parse(prefix + "/" + suffix));
	}

	public static String iconToResourcePath(String name) {
		int splitLocation = name.indexOf(":");
		if (splitLocation < 0) {
			return "minecraft:textures/blocks/" + name + ".png";
		}
		// Defaulting to blocks for simplicity in transition
		return name.substring(0, splitLocation) + ":textures/blocks/" + name.substring(splitLocation + 1) + ".png";
	}

	/**
	 * Turns a block/item name into a prefix for finding textures.
	 *
	 * @param objectName
	 * @return
	 */
	public static String getObjectPrefix(String objectName) {
		if (objectName == null) {
			return null;
		}

		int splitLocation = objectName.indexOf(":");
		return objectName.substring(0, splitLocation).replaceAll("[^a-zA-Z0-9\\s]", "") + objectName.substring(splitLocation);
	}

	public static boolean resourceExists(String name) {
		Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(ResourceLocation.parse(name));
		return resource.isPresent();
	}
}
