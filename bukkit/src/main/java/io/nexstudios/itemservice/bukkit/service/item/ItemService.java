package io.nexstudios.itemservice.bukkit.service.item;

import io.nexstudios.itemservice.bukkit.builder.NexItemBuilder;
import io.nexstudios.itemservice.bukkit.fast.FastItemStack;
import io.nexstudios.itemservice.bukkit.testable.Testable;
import io.nexstudios.itemservice.bukkit.testable.pdc.PdcMarkerTestable;
import io.nexstudios.itemservice.bukkit.testable.pdc.PdcStringEqualsTestable;
import io.nexstudios.serviceregistry.di.Service;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/**
 * Public API service.
 */
public interface ItemService extends Service {

  Plugin plugin();

  default NamespacedKey key(String path) {
    return new NamespacedKey(plugin(), path);
  }

  NexItemBuilder builder(Material material);

  default NexItemBuilder builder(ItemStack stack) {
    return new NexItemBuilder(plugin(), stack);
  }

  default FastItemStack fast(ItemStack stack) {
    return FastItemStack.wrap(plugin(), stack);
  }

  default Testable testableMarker(String markerPath) {
    return new PdcMarkerTestable(key(markerPath));
  }

  default Testable testableString(NamespacedKey key, String expectedValue) {
    return new PdcStringEqualsTestable(key, expectedValue);
  }
}