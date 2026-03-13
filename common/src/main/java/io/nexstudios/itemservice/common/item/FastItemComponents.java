package io.nexstudios.itemservice.common.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.Objects;

public final class FastItemComponents {

  private FastItemComponents() {
  }

  public static Component normalizeLoreLine(Component line) {
    Objects.requireNonNull(line, "line must not be null");
    return line.decoration(TextDecoration.ITALIC, false);
  }
}