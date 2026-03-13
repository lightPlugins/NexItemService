package io.nexstudios.itemservice.bukkit.testable.pdc;

import io.nexstudios.itemservice.bukkit.testable.Testable;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public final class PdcStringEqualsTestable implements Testable {

  private final NamespacedKey key;
  private final String expectedValue;

  public PdcStringEqualsTestable(NamespacedKey key, String expectedValue) {
    this.key = Objects.requireNonNull(key, "key must not be null");
    this.expectedValue = Objects.requireNonNull(expectedValue, "expectedValue must not be null");
  }

  @Override
  public boolean test(ItemStack stack) {
    if (stack == null || stack.getType().isAir()) {
      return false;
    }

    String val = stack.getPersistentDataContainer().get(key, PersistentDataType.STRING);
    return expectedValue.equals(val);
  }
}