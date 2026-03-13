package io.nexstudios.itemservice.common.api;

import io.nexstudios.itemservice.common.item.FastAttributeSpec;
import io.nexstudios.itemservice.common.item.FastCustomModelData;
import io.nexstudios.itemservice.common.item.FastItemFlag;
import io.nexstudios.itemservice.common.item.FastItemStack;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FastItemBuilder {

  FastItemBuilder amount(int amount);

  FastItemBuilder maxStackSize(Integer size);

  FastItemBuilder unbreakable(Boolean unbreakable);

  FastItemBuilder tooltipStyleKey(String namespacedKey);

  FastItemBuilder itemModelKey(String namespacedKey);

  FastItemBuilder customModelData(FastCustomModelData value);

  FastItemBuilder name(Component name);

  FastItemBuilder lore(List<Component> lore);

  FastItemBuilder addLoreLine(Component line);

  FastItemBuilder replaceLoreLine(int index, Component line);

  FastItemBuilder removeLoreLine(int index);

  FastItemBuilder clearLore();

  FastItemBuilder enchantments(Map<String, Integer> enchantments);

  FastItemBuilder addEnchantment(String enchantmentKey, int level);

  FastItemBuilder removeEnchantment(String enchantmentKey);

  FastItemBuilder attributes(List<FastAttributeSpec> attributes);

  FastItemBuilder flags(Set<FastItemFlag> flags);

  FastItemBuilder addFlag(FastItemFlag flag);

  FastItemBuilder removeFlag(FastItemFlag flag);

  FastItemBuilder hideTooltip(boolean hide);

  FastItemStack build();
}