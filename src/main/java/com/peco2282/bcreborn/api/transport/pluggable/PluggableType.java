package com.peco2282.bcreborn.api.transport.pluggable;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public record PluggableType<T extends PipePluggable<T>>(
        ResourceLocation id,
        Supplier<T> factory
) {
    public static <T extends PipePluggable<T>> PluggableType<T> of(ResourceLocation id, Supplier<T> factory) {
        return new PluggableType<>(id, factory);
    }

    public T create() {
        return factory.get();
    }
}
