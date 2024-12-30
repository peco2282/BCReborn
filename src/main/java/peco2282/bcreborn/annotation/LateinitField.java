package peco2282.bcreborn.annotation;

/**
 * Marker annotation that indicates fields are initially null
 * but will be assigned a non-null value before being accessed.
 * <p>
 * Fields annotated with {@code @LateinitField} are expected to be
 * assigned a valid non-null value at runtime, avoiding {@code NullPointerException}.
 * Use this annotation to highlight fields that are managed manually and
 * should not remain uninitialized during their lifecycle.
 * </p>
 *
 * @author peco2282
 */
public @interface LateinitField {
}
