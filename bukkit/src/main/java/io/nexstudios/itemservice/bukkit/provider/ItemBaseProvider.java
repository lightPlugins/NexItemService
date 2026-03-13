package io.nexstudios.itemservice.bukkit.provider;

import io.nexstudios.itemservice.common.item.ItemId;
import org.bukkit.inventory.ItemStack;

public interface ItemBaseProvider {

  String namespace();

  boolean isAvailable();

  ItemStack createBase(ItemId id);
}