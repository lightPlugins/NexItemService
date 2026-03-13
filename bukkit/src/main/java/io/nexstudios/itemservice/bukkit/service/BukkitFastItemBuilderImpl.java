package io.nexstudios.itemservice.bukkit.service;

import io.nexstudios.itemservice.bukkit.api.BukkitFastItemBuilder;
import io.nexstudios.itemservice.common.item.FastAttributeOperation;
import io.nexstudios.itemservice.common.item.FastAttributeSpec;
import io.nexstudios.itemservice.common.item.FastCustomModelData;
import io.nexstudios.itemservice.common.item.FastItemFlag;
import io.nexstudios.itemservice.common.item.FastItemStack;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

final class BukkitFastItemBuilderImpl implements BukkitFastItemBuilder {

  private FastItemStack current;

  BukkitFastItemBuilderImpl(FastItemStack base) {
    this.current = Objects.requireNonNull(base, "base must not be null");
  }

  @Override
  public BukkitFastItemBuilder amount(int amount) {
    current = current.withAmount(amount);
    return this;
  }

  @Override
  public BukkitFastItemBuilder maxStackSize(Integer size) {
    current = current.withMaxStackSize(size);
    return this;
  }

  @Override
  public BukkitFastItemBuilder unbreakable(Boolean unbreakable) {
    current = current.withUnbreakable(unbreakable);
    return this;
  }

  @Override
  public BukkitFastItemBuilder tooltipStyleKey(String namespacedKey) {
    current = current.withTooltipStyleKey(namespacedKey);
    return this;
  }

  @Override
  public BukkitFastItemBuilder itemModelKey(String namespacedKey) {
    current = current.withItemModelKey(namespacedKey);
    return this;
  }

  @Override
  public BukkitFastItemBuilder customModelData(FastCustomModelData value) {
    current = current.withCustomModelData(value);
    return this;
  }

  @Override
  public BukkitFastItemBuilder name(Component name) {
    current = current.withName(name);
    return this;
  }

  @Override
  public BukkitFastItemBuilder lore(List<Component> lore) {
    current = current.withLore(lore);
    return this;
  }

  @Override
  public BukkitFastItemBuilder addLoreLine(Component line) {
    current = current.addLoreLine(line);
    return this;
  }

  @Override
  public BukkitFastItemBuilder replaceLoreLine(int index, Component line) {
    Objects.requireNonNull(line, "line must not be null");

    List<Component> lore = new ArrayList<>(current.loreOrEmpty());
    if (index < 0 || index >= lore.size()) {
      throw new IndexOutOfBoundsException("index=" + index + ", size=" + lore.size());
    }
    lore.set(index, line);
    current = current.withLore(lore);
    return this;
  }

  @Override
  public BukkitFastItemBuilder removeLoreLine(int index) {
    List<Component> lore = new ArrayList<>(current.loreOrEmpty());
    if (index < 0 || index >= lore.size()) {
      throw new IndexOutOfBoundsException("index=" + index + ", size=" + lore.size());
    }
    lore.remove(index);
    current = lore.isEmpty() ? current.clearLore() : current.withLore(lore);
    return this;
  }

  @Override
  public BukkitFastItemBuilder clearLore() {
    current = current.clearLore();
    return this;
  }

  @Override
  public BukkitFastItemBuilder enchantments(Map<String, Integer> enchantments) {
    current = current.withEnchantments(enchantments);
    return this;
  }

  @Override
  public BukkitFastItemBuilder addEnchantment(String enchantmentKey, int level) {
    current = current.addEnchantment(enchantmentKey, level);
    return this;
  }

  @Override
  public BukkitFastItemBuilder removeEnchantment(String enchantmentKey) {
    Objects.requireNonNull(enchantmentKey, "enchantmentKey must not be null");

    Map<String, Integer> ench = current.patch().enchantments() == null ? Map.of() : current.patch().enchantments();
    if (ench.isEmpty() || !ench.containsKey(enchantmentKey)) {
      return this;
    }

    Map<String, Integer> next = new HashMap<>(ench);
    next.remove(enchantmentKey);
    current = current.withEnchantments(next.isEmpty() ? null : next);
    return this;
  }

  @Override
  public BukkitFastItemBuilder attributes(List<FastAttributeSpec> attributes) {
    current = current.withAttributes(attributes);
    return this;
  }

  @Override
  public BukkitFastItemBuilder flags(Set<FastItemFlag> flags) {
    current = current.withFlags(flags);
    return this;
  }

  @Override
  public BukkitFastItemBuilder addFlag(FastItemFlag flag) {
    current = current.addFlag(flag);
    return this;
  }

  @Override
  public BukkitFastItemBuilder removeFlag(FastItemFlag flag) {
    Objects.requireNonNull(flag, "flag must not be null");

    Set<FastItemFlag> flags = current.patch().flags();
    if (flags == null || flags.isEmpty() || !flags.contains(flag)) {
      return this;
    }

    Set<FastItemFlag> next = new HashSet<>(flags);
    next.remove(flag);
    current = current.withFlags(next.isEmpty() ? null : next);
    return this;
  }

  @Override
  public BukkitFastItemBuilder hideTooltip(boolean hide) {
    current = current.hideTooltip(hide);
    return this;
  }

  @Override
  public FastItemStack build() {
    return current;
  }

  // ---- Typed Bukkit API ----

  @Override
  public BukkitFastItemBuilder addEnchantment(Enchantment enchantment, int level) {
    Objects.requireNonNull(enchantment, "enchantment must not be null");
    NamespacedKey key = enchantment.getKey();
    return addEnchantment(key.getNamespace() + ":" + key.getKey(), level);
  }

  @Override
  public BukkitFastItemBuilder removeEnchantment(Enchantment enchantment) {
    Objects.requireNonNull(enchantment, "enchantment must not be null");
    NamespacedKey key = enchantment.getKey();
    return removeEnchantment(key.getNamespace() + ":" + key.getKey());
  }

  @Override
  public BukkitFastItemBuilder enchantment(Enchantment enchantment, int level) {
    return addEnchantment(enchantment, level);
  }

  @Override
  public BukkitFastItemBuilder attribute(
      String name,
      Attribute attribute,
      double amount,
      FastAttributeOperation operation,
      EquipmentSlot... slots
  ) {
    Objects.requireNonNull(attribute, "attribute must not be null");
    Objects.requireNonNull(operation, "operation must not be null");

    Set<String> slotNames = null;
    if (slots != null && slots.length > 0) {
      EnumSet<EquipmentSlot> set = EnumSet.noneOf(EquipmentSlot.class);
      for (EquipmentSlot s : slots) {
        if (s != null) set.add(s);
      }
      if (!set.isEmpty()) {
        slotNames = new HashSet<>();
        for (EquipmentSlot s : set) slotNames.add(s.name());
      }
    }

    NamespacedKey attrKey = attribute.getKey();
    String attrKeyString = attrKey.getNamespace() + ":" + attrKey.getKey();

    FastAttributeSpec spec = new FastAttributeSpec(
        name == null ? "" : name,
        attrKeyString,
        amount,
        operation,
        slotNames
    );

    List<FastAttributeSpec> next = new ArrayList<>(current.patch().attributes() == null ? List.of() : current.patch().attributes());
    next.add(spec);
    current = current.withAttributes(next);
    return this;
  }

  @Override
  public BukkitFastItemBuilder hideEnchants() {
    return addFlag(FastItemFlag.HIDE_ENCHANTS);
  }

  @Override
  public BukkitFastItemBuilder hideAttributes() {
    return addFlag(FastItemFlag.HIDE_ATTRIBUTES);
  }

  @Override
  public BukkitFastItemBuilder hideTooltip() {
    return hideTooltip(true);
  }
}