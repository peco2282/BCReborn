/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.bean;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import peco2282.bcreborn.registry.BCRegistry;

import java.lang.annotation.ElementType;
import javax.annotation.meta.TypeQualifierDefault;

/**
 * Use for {@link BCRegistry#init(IEventBus)} and {@link ContextProcessor#initRegister()}
 *
 * <p>Annotate for register object when before {@link DeferredRegister} bus registering
 *
 * @author peco2282
 */
@TypeQualifierDefault(ElementType.TYPE)
public @interface InitRegister {}
