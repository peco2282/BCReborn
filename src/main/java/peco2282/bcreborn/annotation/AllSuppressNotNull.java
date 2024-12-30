package peco2282.bcreborn.annotation;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotation to indicate that all elements of the specified types 
 * within the annotated package or type (like fields, methods, parameters, etc.) 
 * are considered {@code @Nonnull} by default unless explicitly annotated otherwise.
 * <p>
 * This is useful for enforcing nonnull behavior across packages or types 
 * to reduce repetitive {@code @Nonnull} declarations.
 * </p>
 *
 * @author peco2282
 */
@Nonnull
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.PACKAGE, ElementType.TYPE})
@TypeQualifierDefault({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_PARAMETER, ElementType.RECORD_COMPONENT})
public @interface AllSuppressNotNull {
}
