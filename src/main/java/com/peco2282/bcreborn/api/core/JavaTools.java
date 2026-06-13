/*
 * BC Reborn
 *
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * Copyright (c) 2025-2026 peco2282
 *
 * Contains original work and code derived from BuildCraft.
 *
 * Licensed under the Minecraft Mod Public License 1.0 (MMPL).
 * See LICENSE for details.
 */
package com.peco2282.bcreborn.api.core;

import java.util.Arrays;

/**
 * Utility class for common Java operations.
 */
public final class JavaTools {
  private JavaTools() {

  }

  /**
   * Concatenates two arrays.
   *
   * @param first  The first array.
   * @param second The second array.
   * @param <T>    The type of the array elements.
   * @return A new array containing elements from both input arrays.
   */
  public static <T> T[] concat(T[] first, T[] second) {
    T[] result = Arrays.copyOf(first, first.length + second.length);
    System.arraycopy(second, 0, result, first.length, second.length);
    return result;
  }

  /**
   * Concatenates two int arrays.
   *
   * @param first  The first array.
   * @param second The second array.
   * @return A new array containing elements from both input arrays.
   */
  public static int[] concat(int[] first, int[] second) {
    int[] result = Arrays.copyOf(first, first.length + second.length);
    System.arraycopy(second, 0, result, first.length, second.length);
    return result;
  }

  /**
   * Concatenates two float arrays.
   *
   * @param first  The first array.
   * @param second The second array.
   * @return A new array containing elements from both input arrays.
   */
  public static float[] concat(float[] first, float[] second) {
    float[] result = Arrays.copyOf(first, first.length + second.length);
    System.arraycopy(second, 0, result, first.length, second.length);
    return result;
  }

  /**
   * Surrounds the given string with double quotes.
   *
   * @param stringToSurroundWithQuotes The string to surround.
   * @return The quoted string.
   */
  public static String surroundWithQuotes(String stringToSurroundWithQuotes) {
    return String.format("\"%s\"", stringToSurroundWithQuotes);
  }

  /**
   * Strips surrounding double quotes from the given string.
   *
   * @param stringToStripQuotes The string to strip.
   * @return The stripped string.
   */
  public static String stripSurroundingQuotes(String stringToStripQuotes) {
    return stringToStripQuotes.replaceAll("^\"|\"$", "");
  }
}
