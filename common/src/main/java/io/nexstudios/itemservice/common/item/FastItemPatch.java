package io.nexstudios.itemservice.common.item;

import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

public record FastItemPatch(
    Component name,
    List<Component> lore,
    FastCustomModelData customModelData,

    Integer maxStackSize,
    Boolean unbreakable,

    String tooltipStyleKey,
    String itemModelKey,

    Map<String, Integer> enchantments,
    List<FastAttributeSpec> attributes,
    Set<FastItemFlag> flags,

    boolean hideTooltip
) {
}