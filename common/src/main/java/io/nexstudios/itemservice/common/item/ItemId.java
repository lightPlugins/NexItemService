package io.nexstudios.itemservice.common.item;

import java.util.Objects;

public record ItemId(String namespace, String key) {

  public ItemId {
    Objects.requireNonNull(namespace, "namespace must not be null");
    Objects.requireNonNull(key, "key must not be null");
    if (namespace.isBlank() || key.isBlank()) {
      throw new IllegalArgumentException("namespace and key must not be blank");
    }
  }

  public static ItemId parse(String raw) {
    Objects.requireNonNull(raw, "raw must not be null");
    int idx = raw.indexOf(':');
    if (idx <= 0 || idx >= raw.length() - 1) {
      throw new IllegalArgumentException("Invalid item id: " + raw + " (expected namespace:key)");
    }
    return new ItemId(raw.substring(0, idx), raw.substring(idx + 1));
  }

  public String asString() {
    return namespace + ":" + key;
  }

  @Override
  public String toString() {
    return asString();
  }
}