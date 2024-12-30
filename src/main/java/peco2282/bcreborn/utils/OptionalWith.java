package peco2282.bcreborn.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;


/**
 * A utility class representing an enhanced version of Optional with additional functionality.
 * This class allows values to be optionally present or absent, similar to {@link java.util.Optional},
 * but with extended methods for setting, retrieving, and performing operations conditionally.
 *
 * @param <T> The type of the value wrapped by this class.
 *
 * @author peco2282
 */
public class OptionalWith<T> {
  /**
   * A constant instance of {@code OptionalWith} representing an empty value (no value is present).
   */
  private static final OptionalWith<?> EMPTY = new OptionalWith<>(null);
  private @Nullable T value;

  private OptionalWith(@Nullable T value) {
    this.value = value;
  }

  /**
   * Creates a new {@code OptionalWith} instance with the specified non-null value.
   *
   * @param value The non-null value to be wrapped.
   * @param <T> The type of the value.
   * @return A new {@code OptionalWith} containing the specified value.
   * @throws NullPointerException if {@code value} is null.
   */
  public static <T> OptionalWith<T> of(@NotNull T value) {
    return new OptionalWith<>(Objects.requireNonNull(value));
  }

  /**
   * Creates a new {@code OptionalWith} instance that may or may not contain the specified value.
   *
   * @param value The value to be wrapped, can be null.
   * @param <T> The type of the value.
   * @return A new {@code OptionalWith} wrapping the specified value, or an empty {@code OptionalWith} if null.
   */
  public static <T> OptionalWith<T> ofNullable(@Nullable T value) {
    return new OptionalWith<>(value);
  }

  @SuppressWarnings("unchecked")
  public static <T> OptionalWith<T> empty() {
    return (OptionalWith<T>) EMPTY;
  }

  /**
   * Checks if a value is present in this {@code OptionalWith}.
   *
   * @return {@code true} if the value is present, {@code false} otherwise.
   */
  public boolean isPresent() {
    return value != null;
  }

  /**
   * Retrieves the value wrapped by this {@code OptionalWith}, or null if no value is present.
   *
   * @return The value if present, or null if absent.
   */
  public @Nullable T get() {
    return value;
  }

  /**
   * Retrieves the value wrapped by this {@code OptionalWith}, ensuring it is non-null.
   *
   * @return The non-null value wrapped by this {@code OptionalWith}.
   * @throws NullPointerException if no value is present.
   */
  public @NotNull T getNotNull() {
    return Objects.requireNonNull(value);
  }

  /**
   * Retrieves the value wrapped by this {@code OptionalWith}, or the specified default value if absent.
   *
   * @param other The default value to return if no value is present.
   * @return The wrapped value if present, or {@code other} if value is absent.
   */
  public @NotNull T orElse(T other) {
    return value != null ? value : other;
  }

  /**
   * Sets a new value for this {@code OptionalWith}.
   * The value must not be null; if null, an exception is thrown.
   *
   * @param value The non-null value to set.
   * @throws NullPointerException if {@code value} is null.
   */
  public void set(T value) {
    set(value, true);
  }

  /**
   * Sets a new value for this {@code OptionalWith}.
   * The behavior depends on the {@code notnull} flag to enforce non-null values.
   *
   * @param value The value to set. Can be null if {@code notnull} is {@code false}.
   * @param notnull A flag indicating whether the new value must be non-null.
   * @throws NullPointerException if {@code notnull} is {@code true} and {@code value} is null.
   */
  public void set(T value, boolean notnull) {
    this.value = notnull ? Objects.requireNonNull(value) : value;
  }

  /**
   * Executes the provided {@link Consumer} if a value is present in this {@code OptionalWith}.
   *
   * @param consumer The {@link Consumer} to execute. Must not be null.
   * @throws NullPointerException if {@code consumer} is null.
   */
  public void ifPresent(@NotNull Consumer<T> consumer) {
    if (isPresent()) consumer.accept(value);
  }

  /**
   * Sets the specified value if no value is currently present in this {@code OptionalWith}.
   *
   * @param value The value to set if absent. Must not be null.
   * @throws NullPointerException if {@code value} is null.
   */
  public void ifAbsentSet(@NotNull T value) {
    if (!isPresent()) set(value);
  }
}
