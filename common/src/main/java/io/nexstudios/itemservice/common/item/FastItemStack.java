package io.nexstudios.itemservice.common.item;

import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class FastItemStack {

  private final ItemId baseId;
  private final int amount;
  private final FastItemPatch patch;

  private FastItemStack(ItemId baseId, int amount, FastItemPatch patch) {
    this.baseId = Objects.requireNonNull(baseId, "baseId must not be null");
    this.patch = Objects.requireNonNull(patch, "patch must not be null");
    if (amount <= 0) throw new IllegalArgumentException("amount must be > 0");
    this.amount = amount;
  }

  public static FastItemStack of(String baseId) {
    return of(ItemId.parse(baseId));
  }

  public static FastItemStack of(ItemId baseId) {
    return new FastItemStack(
        baseId,
        1,
        new FastItemPatch(
            null, null, null,
            null, null,
            null, null,
            null, null, null,
            false
        )
    );
  }

  public ItemId baseId() {
    return baseId;
  }

  public int amount() {
    return amount;
  }

  public FastItemPatch patch() {
    return patch;
  }

  public FastItemStack withAmount(int amount) {
    return new FastItemStack(baseId, amount, patch);
  }

  public FastItemStack withName(Component name) {
    return new FastItemStack(baseId, amount, new FastItemPatch(
        name, patch.lore(), patch.customModelData(),
        patch.maxStackSize(), patch.unbreakable(),
        patch.tooltipStyleKey(), patch.itemModelKey(),
        patch.enchantments(), patch.attributes(), patch.flags(),
        patch.hideTooltip()
    ));
  }

  public FastItemStack withLore(List<Component> lore) {
    return new FastItemStack(baseId, amount, new FastItemPatch(
        patch.name(), normalizeLore(lore), patch.customModelData(),
        patch.maxStackSize(), patch.unbreakable(),
        patch.tooltipStyleKey(), patch.itemModelKey(),
        patch.enchantments(), patch.attributes(), patch.flags(),
        patch.hideTooltip()
    ));
  }

  public FastItemStack withCustomModelData(FastCustomModelData customModelData) {
    return new FastItemStack(baseId, amount, new FastItemPatch(
        patch.name(), patch.lore(), customModelData,
        patch.maxStackSize(), patch.unbreakable(),
        patch.tooltipStyleKey(), patch.itemModelKey(),
        patch.enchantments(), patch.attributes(), patch.flags(),
        patch.hideTooltip()
    ));
  }

  public FastItemStack withMaxStackSize(Integer maxStackSize) {
    return new FastItemStack(baseId, amount, new FastItemPatch(
        patch.name(), patch.lore(), patch.customModelData(),
        maxStackSize, patch.unbreakable(),
        patch.tooltipStyleKey(), patch.itemModelKey(),
        patch.enchantments(), patch.attributes(), patch.flags(),
        patch.hideTooltip()
    ));
  }

  public FastItemStack withUnbreakable(Boolean unbreakable) {
    return new FastItemStack(baseId, amount, new FastItemPatch(
        patch.name(), patch.lore(), patch.customModelData(),
        patch.maxStackSize(), unbreakable,
        patch.tooltipStyleKey(), patch.itemModelKey(),
        patch.enchantments(), patch.attributes(), patch.flags(),
        patch.hideTooltip()
    ));
  }

  public FastItemStack withTooltipStyleKey(String tooltipStyleKey) {
    return new FastItemStack(baseId, amount, new FastItemPatch(
        patch.name(), patch.lore(), patch.customModelData(),
        patch.maxStackSize(), patch.unbreakable(),
        tooltipStyleKey, patch.itemModelKey(),
        patch.enchantments(), patch.attributes(), patch.flags(),
        patch.hideTooltip()
    ));
  }

  public FastItemStack withItemModelKey(String itemModelKey) {
    return new FastItemStack(baseId, amount, new FastItemPatch(
        patch.name(), patch.lore(), patch.customModelData(),
        patch.maxStackSize(), patch.unbreakable(),
        patch.tooltipStyleKey(), itemModelKey,
        patch.enchantments(), patch.attributes(), patch.flags(),
        patch.hideTooltip()
    ));
  }

  public FastItemStack withAttributes(List<FastAttributeSpec> attributes) {
    List<FastAttributeSpec> frozen = attributes == null ? null : Collections.unmodifiableList(new ArrayList<>(attributes));
    return new FastItemStack(baseId, amount, new FastItemPatch(
        patch.name(), patch.lore(), patch.customModelData(),
        patch.maxStackSize(), patch.unbreakable(),
        patch.tooltipStyleKey(), patch.itemModelKey(),
        patch.enchantments(), frozen, patch.flags(),
        patch.hideTooltip()
    ));
  }

  public FastItemStack withEnchantments(Map<String, Integer> enchantments) {
    return new FastItemStack(baseId, amount, new FastItemPatch(
        patch.name(), patch.lore(), patch.customModelData(),
        patch.maxStackSize(), patch.unbreakable(),
        patch.tooltipStyleKey(), patch.itemModelKey(),
        immutableEnchantments(enchantments), patch.attributes(), patch.flags(),
        patch.hideTooltip()
    ));
  }

  public FastItemStack addEnchantment(String enchantmentKey, int level) {
    Objects.requireNonNull(enchantmentKey, "enchantmentKey must not be null");
    if (level <= 0) throw new IllegalArgumentException("level must be > 0");

    Map<String, Integer> map = new HashMap<>(patch.enchantments() == null ? Map.of() : patch.enchantments());
    map.put(enchantmentKey, level);
    return withEnchantments(map);
  }

  public FastItemStack addFlag(FastItemFlag flag) {
    Objects.requireNonNull(flag, "flag must not be null");
    Set<FastItemFlag> next = new HashSet<>(patch.flags() == null ? Set.of() : patch.flags());
    next.add(flag);
    return withFlags(next);
  }

  public FastItemStack withFlags(Set<FastItemFlag> flags) {
    Set<FastItemFlag> frozen = flags == null ? null : Collections.unmodifiableSet(new HashSet<>(flags));
    return new FastItemStack(baseId, amount, new FastItemPatch(
        patch.name(), patch.lore(), patch.customModelData(),
        patch.maxStackSize(), patch.unbreakable(),
        patch.tooltipStyleKey(), patch.itemModelKey(),
        patch.enchantments(), patch.attributes(), frozen,
        patch.hideTooltip()
    ));
  }

  public FastItemStack hideTooltip(boolean hide) {
    return new FastItemStack(baseId, amount, new FastItemPatch(
        patch.name(), patch.lore(), patch.customModelData(),
        patch.maxStackSize(), patch.unbreakable(),
        patch.tooltipStyleKey(), patch.itemModelKey(),
        patch.enchantments(), patch.attributes(), patch.flags(),
        hide
    ));
  }

  public FastItemStack clearLore() {
    return new FastItemStack(baseId, amount, new FastItemPatch(
        patch.name(), null, patch.customModelData(),
        patch.maxStackSize(), patch.unbreakable(),
        patch.tooltipStyleKey(), patch.itemModelKey(),
        patch.enchantments(), patch.attributes(), patch.flags(),
        patch.hideTooltip()
    ));
  }

  public FastItemStack addLoreLine(Component line) {
    Objects.requireNonNull(line, "line must not be null");
    List<Component> lore = mutableLore();
    lore.add(FastItemComponents.normalizeLoreLine(line));
    return withLore(lore);
  }

  public List<Component> loreOrEmpty() {
    return patch.lore() == null ? List.of() : patch.lore();
  }

  private List<Component> mutableLore() {
    return new ArrayList<>(loreOrEmpty());
  }

  private static List<Component> normalizeLore(List<Component> lore) {
    if (lore == null) return null;
    List<Component> copy = new ArrayList<>(lore.size());
    for (Component line : lore) {
      copy.add(FastItemComponents.normalizeLoreLine(Objects.requireNonNull(line, "lore line must not be null")));
    }
    return Collections.unmodifiableList(copy);
  }

  private static Map<String, Integer> immutableEnchantments(Map<String, Integer> enchantments) {
    if (enchantments == null) return null;
    Map<String, Integer> copy = new HashMap<>();
    for (Map.Entry<String, Integer> e : enchantments.entrySet()) {
      String key = Objects.requireNonNull(e.getKey(), "enchantment key must not be null");
      Integer lvl = Objects.requireNonNull(e.getValue(), "enchantment level must not be null");
      if (lvl <= 0) throw new IllegalArgumentException("Enchantment level must be > 0 for " + key);
      copy.put(key, lvl);
    }
    return Collections.unmodifiableMap(copy);
  }
}