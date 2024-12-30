package peco2282.bcreborn.bean;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import peco2282.bcreborn.registry.BCRegistry;

import javax.annotation.meta.TypeQualifierDefault;
import java.lang.annotation.ElementType;

/**
 * Use for {@link BCRegistry#init(IEventBus)} and {@link ContextProcessor#initRegister()}
 *
 * Annotate for register object when before {@link DeferredRegister} bus registering
 *
 * @author peco2282
 */
@TypeQualifierDefault(ElementType.TYPE)
public @interface InitRegister {
}
