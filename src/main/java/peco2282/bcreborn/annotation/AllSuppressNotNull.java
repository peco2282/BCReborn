/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;

/**
 * Annotation to indicate that all elements of the specified types within the annotated package or
 * type (like fields, methods, parameters, etc.) are considered {@code @Nonnull} by default unless
 * explicitly annotated otherwise.
 *
 * <p>This is useful for enforcing nonnull behavior across packages or types to reduce repetitive
 * {@code @Nonnull} declarations.
 *
 * @author peco2282
 */
@Nonnull
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.PACKAGE, ElementType.TYPE})
@TypeQualifierDefault({
  ElementType.TYPE,
  ElementType.METHOD,
  ElementType.FIELD,
  ElementType.PARAMETER,
  ElementType.TYPE_PARAMETER,
  ElementType.RECORD_COMPONENT
})
public @interface AllSuppressNotNull {}
