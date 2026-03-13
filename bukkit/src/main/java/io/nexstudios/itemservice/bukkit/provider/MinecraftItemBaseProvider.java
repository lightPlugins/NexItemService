package io.nexstudios.itemservice.bukkit.provider;

import io.nexstudios.itemservice.common.item.ItemId;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public final class MinecraftItemBaseProvider implements ItemBaseProvider {

  @Override
  public String namespace() {
    return "minecraft";
  }

  @Override
  public boolean isAvailable() {
    return true;
  }

  @Override
  public ItemStack createBase(ItemId id) {
    String key = id.key().toUpperCase(Locale.ROOT);
    Material material = Material.matchMaterial(key);
    if (material == null) {
      throw new IllegalArgumentException("Unknown minecraft material: " + id);
    }
    return new ItemStack(material);
  }
}