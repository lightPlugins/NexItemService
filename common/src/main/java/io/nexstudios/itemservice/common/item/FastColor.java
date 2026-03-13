package io.nexstudios.itemservice.common.item;

import java.util.Objects;

/**
 * Platform-neutral RGB color.
 */
public record FastColor(int red, int green, int blue) {

  public FastColor {
    validate(red, "red");
    validate(green, "green");
    validate(blue, "blue");
  }

  public static FastColor rgb(int red, int green, int blue) {
    return new FastColor(red, green, blue);
  }

  private static void validate(int value, String name) {
    Objects.requireNonNull(name, "name must not be null");
    if (value < 0 || value > 255) {
      throw new IllegalArgumentException(name + " must be in range 0..255");
    }
  }
}