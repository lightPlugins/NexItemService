package io.nexstudios.itemservice.common.impl;

import io.nexstudios.itemservice.common.api.FastItemBuilder;
import io.nexstudios.itemservice.common.item.FastAttributeSpec;
import io.nexstudios.itemservice.common.item.FastCustomModelData;
import io.nexstudios.itemservice.common.item.FastItemFlag;
import io.nexstudios.itemservice.common.item.FastItemStack;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class DefaultFastItemBuilder implements FastItemBuilder {

  private FastItemStack current;

  public DefaultFastItemBuilder(FastItemStack base) {
    this.current = Objects.requireNonNull(base, "base must not be null");
  }

  @Override
  public FastItemBuilder amount(int amount) {
    current = current.withAmount(amount);
    return this;
  }

  @Override
  public FastItemBuilder maxStackSize(Integer size) {
    current = current.withMaxStackSize(size);
    return this;
  }

  @Override
  public FastItemBuilder unbreakable(Boolean unbreakable) {
    current = current.withUnbreakable(unbreakable);
    return this;
  }

  @Override
  public FastItemBuilder tooltipStyleKey(String namespacedKey) {
    current = current.withTooltipStyleKey(namespacedKey);
    return this;
  }

  @Override
  public FastItemBuilder itemModelKey(String namespacedKey) {
    current = current.withItemModelKey(namespacedKey);
    return this;
  }

  @Override
  public FastItemBuilder customModelData(FastCustomModelData value) {
    current = current.withCustomModelData(value);
    return this;
  }

  @Override
  public FastItemBuilder name(Component name) {
    current = current.withName(name);
    return this;
  }

  @Override
  public FastItemBuilder lore(List<Component> lore) {
    current = current.withLore(lore);
    return this;
  }

  @Override
  public FastItemBuilder addLoreLine(Component line) {
    current = current.addLoreLine(line);
    return this;
  }

  @Override
  public FastItemBuilder replaceLoreLine(int index, Component line) {
    current = current.replaceLoreLine(index, line);
    return this;
  }

  @Override
  public FastItemBuilder removeLoreLine(int index) {
    current = current.removeLoreLine(index);
    return this;
  }

  @Override
  public FastItemBuilder clearLore() {
    current = current.clearLore();
    return this;
  }

  @Override
  public FastItemBuilder enchantments(Map<String, Integer> enchantments) {
    current = current.withEnchantments(enchantments);
    return this;
  }

  @Override
  public FastItemBuilder addEnchantment(String enchantmentKey, int level) {
    current = current.addEnchantment(enchantmentKey, level);
    return this;
  }

  @Override
  public FastItemBuilder removeEnchantment(String enchantmentKey) {
    current = current.removeEnchantment(enchantmentKey);
    return this;
  }

  @Override
  public FastItemBuilder attributes(List<FastAttributeSpec> attributes) {
    current = current.withAttributes(attributes);
    return this;
  }

  @Override
  public FastItemBuilder flags(Set<FastItemFlag> flags) {
    current = current.withFlags(flags);
    return this;
  }

  @Override
  public FastItemBuilder addFlag(FastItemFlag flag) {
    current = current.addFlag(flag);
    return this;
  }

  @Override
  public FastItemBuilder removeFlag(FastItemFlag flag) {
    current = current.removeFlag(flag);
    return this;
  }

  @Override
  public FastItemBuilder hideTooltip(boolean hide) {
    current = current.hideTooltip(hide);
    return this;
  }

  @Override
  public FastItemStack build() {
    return current;
  }
}