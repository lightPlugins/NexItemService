package io.nexstudios.itemservice.common.item;

import java.util.Set;

/**
 * Platform-neutral attribute modifier spec.
 *
 * attributeKey format: namespace:key (e.g. minecraft:attack_damage)
 * slots contain Bukkit EquipmentSlot names (e.g. "HAND", "OFF_HAND", "HEAD"...)
 */
public record FastAttributeSpec(
    String name,
    String attributeKey,
    double amount,
    FastAttributeOperation operation,
    Set<String> slots
) {
}