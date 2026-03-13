package io.nexstudios.itemservice.common.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents minecraft:custom_model_data using the 1.21+ component model.
 * Values are stored in typed lists (floats, flags, strings, colors).
 */
public final class FastCustomModelData {

  private final List<Float> floats;
  private final List<Boolean> flags;
  private final List<String> strings;
  private final List<FastColor> colors;

  private FastCustomModelData(List<Float> floats, List<Boolean> flags, List<String> strings, List<FastColor> colors) {
    this.floats = floats;
    this.flags = flags;
    this.strings = strings;
    this.colors = colors;
  }

  public static FastCustomModelData empty() {
    return new FastCustomModelData(List.of(), List.of(), List.of(), List.of());
  }

  public List<Float> floats() {
    return floats;
  }

  public List<Boolean> flags() {
    return flags;
  }

  public List<String> strings() {
    return strings;
  }

  public List<FastColor> colors() {
    return colors;
  }

  public boolean isEmpty() {
    return floats.isEmpty() && flags.isEmpty() && strings.isEmpty() && colors.isEmpty();
  }

  public FastCustomModelData addFloat(float value) {
    List<Float> next = new ArrayList<>(floats);
    next.add(value);
    return new FastCustomModelData(Collections.unmodifiableList(next), flags, strings, colors);
  }

  public FastCustomModelData addFlag(boolean value) {
    List<Boolean> next = new ArrayList<>(flags);
    next.add(value);
    return new FastCustomModelData(floats, Collections.unmodifiableList(next), strings, colors);
  }

  public FastCustomModelData addString(String value) {
    Objects.requireNonNull(value, "value must not be null");
    List<String> next = new ArrayList<>(strings);
    next.add(value);
    return new FastCustomModelData(floats, flags, Collections.unmodifiableList(next), colors);
  }

  public FastCustomModelData addColor(FastColor value) {
    Objects.requireNonNull(value, "value must not be null");
    List<FastColor> next = new ArrayList<>(colors);
    next.add(value);
    return new FastCustomModelData(floats, flags, strings, Collections.unmodifiableList(next));
  }
}