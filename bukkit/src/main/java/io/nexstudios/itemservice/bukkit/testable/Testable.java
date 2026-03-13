package io.nexstudios.itemservice.bukkit.testable;

import org.bukkit.inventory.ItemStack;

@FunctionalInterface
public interface Testable {
  boolean test(ItemStack stack);
}