package peco2282.bcreborn.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

public class OptionalWith<T> {
  private static final OptionalWith<?> EMPTY = new OptionalWith<>(null);
  private @Nullable T value;

  private OptionalWith(@Nullable T value) {
    this.value = value;
  }

  public static <T> OptionalWith<T> of(@NotNull T value) {
    return new OptionalWith<>(Objects.requireNonNull(value));
  }

  public static <T> OptionalWith<T> ofNullable(@Nullable T value) {
    return new OptionalWith<>(value);
  }

  @SuppressWarnings("unchecked")
  public static <T> OptionalWith<T> empty() {
    return (OptionalWith<T>) EMPTY;
  }

  public boolean isPresent() {
    return value != null;
  }

  public @Nullable T get() {
    return value;
  }

  public @NotNull T getNotNull() {
    return Objects.requireNonNull(value);
  }

  public @NotNull T orElse(T other) {
    return value != null ? value : other;
  }

  public void set(T value) {
    set(value, true);
  }

  public void set(T value, boolean notnull) {
    this.value = notnull ? Objects.requireNonNull(value) : value;
  }

  public void ifPresent(@NotNull Consumer<T> consumer) {
    if (isPresent()) consumer.accept(value);
  }

  public void ifAbsentSet(@NotNull T value) {
    if (!isPresent()) set(value);
  }
}
