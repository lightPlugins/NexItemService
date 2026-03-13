package io.nexstudios.itemservice.bukkit.testable.pdc;

import io.nexstudios.itemservice.bukkit.testable.Testable;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public final class PdcMarkerTestable implements Testable {

  private final NamespacedKey key;

  public PdcMarkerTestable(NamespacedKey key) {
    this.key = Objects.requireNonNull(key, "key must not be null");
  }

  @Override
  public boolean test(ItemStack stack) {
    if (stack == null || stack.getType().isAir()) {
      return false;
    }

    Byte val = stack.getPersistentDataContainer().get(key, PersistentDataType.BYTE);
    return val != null && val == (byte) 1;
  }
}