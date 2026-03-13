package io.nexstudios.itemservice.bukkit.api;

import io.nexstudios.itemservice.common.api.FastItemBuilder;
import io.nexstudios.itemservice.common.item.FastAttributeOperation;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;

public interface BukkitFastItemBuilder extends FastItemBuilder {

  BukkitFastItemBuilder addEnchantment(Enchantment enchantment, int level);

  BukkitFastItemBuilder removeEnchantment(Enchantment enchantment);

  BukkitFastItemBuilder enchantment(Enchantment enchantment, int level);

  BukkitFastItemBuilder attribute(
      String name,
      Attribute attribute,
      double amount,
      FastAttributeOperation operation,
      EquipmentSlot... slots
  );

  BukkitFastItemBuilder hideEnchants();

  BukkitFastItemBuilder hideAttributes();

  BukkitFastItemBuilder hideTooltip();
}