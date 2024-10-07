package peco2282.bcreborn.annotation;

import net.minecraft.FieldsAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.meta.TypeQualifierDefault;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate {MethodsReturnNonnullByDefault, FieldsAreNonnullByDefault, ParametersAreNonnullByDefault}
 * @see MethodsReturnNonnullByDefault
 * @see FieldsAreNonnullByDefault
 * @see ParametersAreNonnullByDefault
 */
@Nonnull
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.PACKAGE, ElementType.TYPE})
@TypeQualifierDefault({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_PARAMETER, ElementType.RECORD_COMPONENT})
public @interface AllSuppressNotNull {
}
