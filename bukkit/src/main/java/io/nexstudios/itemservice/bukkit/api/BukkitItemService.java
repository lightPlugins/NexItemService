package io.nexstudios.itemservice.bukkit.api;

import io.nexstudios.itemservice.common.api.ItemService;
import io.nexstudios.itemservice.common.item.FastItemStack;
import org.bukkit.inventory.ItemStack;

public interface BukkitItemService extends ItemService {

  BukkitFastItemBuilder bukkitBuilder(String baseId);

  BukkitFastItemBuilder bukkitBuilder(FastItemStack base);

  ItemStack render(FastItemStack fast);

  FastItemStack fromItemStack(ItemStack stack);
}