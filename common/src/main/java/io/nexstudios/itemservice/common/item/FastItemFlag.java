package io.nexstudios.itemservice.common.item;

/**
 * High-level flags that influence tooltip visibility.
 *
 * This is intentionally not org.bukkit.inventory.ItemFlag to keep common platform-neutral.
 */
public enum FastItemFlag {
  HIDE_ENCHANTS,
  HIDE_ATTRIBUTES,
  HIDE_UNBREAKABLE,
  HIDE_CAN_DESTROY,
  HIDE_CAN_PLACE,
  HIDE_ADDITIONAL_TOOLTIP,
  HIDE_DYE,
  HIDE_ARMOR_TRIM,
  HIDE_STORED_ENCHANTS
}