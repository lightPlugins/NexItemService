package io.nexstudios.itemservice.bukkit.builder;

import io.nexstudios.itemservice.bukkit.builder.lore.NexLoreBuilder;
import io.nexstudios.itemservice.bukkit.fast.FastItemStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public final class NexItemBuilder {

  private static final MiniMessage MINI = MiniMessage.miniMessage();

  private final Plugin plugin;
  private final FastItemStack fast;

  public NexItemBuilder(Plugin plugin, Material material) {
    this(plugin, ItemStack.of(Objects.requireNonNull(material, "material must not be null")));
  }

  public NexItemBuilder(Plugin plugin, ItemStack stack) {
    this.plugin = Objects.requireNonNull(plugin, "plugin must not be null");
    this.fast = FastItemStack.wrap(this.plugin, Objects.requireNonNull(stack, "stack must not be null"));
  }

  public NexItemBuilder of(ItemStack stack) {
    return new NexItemBuilder(plugin, stack);
  }

  public NexItemBuilder of(Optional<ItemStack> stack) {
    Objects.requireNonNull(stack, "stack must not be null");
    return stack.map(this::of).orElse(this);
  }

  public NexItemBuilder amount(int amount) {
    fast.amount(amount);
    return this;
  }

  public NexItemBuilder maxStackSize(int maxStackSize) {
    fast.maxStackSize(maxStackSize);
    return this;
  }

  public NexItemBuilder mark(NamespacedKey key) {
    fast.mark(key);
    return this;
  }

  public NexItemBuilder pdcString(NamespacedKey key, String value) {
    fast.pdcString(key, value);
    return this;
  }

  public NexItemBuilder name(Component name) {
    fast.name(name.decoration(TextDecoration.ITALIC, false));
    return this;
  }

  public NexItemBuilder name(String miniMessage) {
    Objects.requireNonNull(miniMessage, "miniMessage must not be null");
    return name(MINI.deserialize(miniMessage));
  }

  public NexItemBuilder name(String miniMessage, TagResolver resolver) {
    Objects.requireNonNull(miniMessage, "miniMessage must not be null");
    Objects.requireNonNull(resolver, "resolver must not be null");
    return name(MINI.deserialize(miniMessage, resolver));
  }

  public NexItemBuilder lore(Component... lines) {
    fast.lore(lines);
    return this;
  }

  public NexItemBuilder lore(Consumer<NexLoreBuilder> editor) {
    fast.lore(editor);
    return this;
  }

  public NexItemBuilder customModelData(int cmd) {
    fast.customModelData(cmd);
    return this;
  }

  public NexItemBuilder enchant(Enchantment enchantment, int level) {
    fast.enchant(enchantment, level);
    return this;
  }

  public NexItemBuilder flags(ItemFlag... flags) {
    fast.flags(flags);
    return this;
  }

  public NexItemBuilder hideToolTips(boolean hide) {
    fast.hideToolTips(hide);
    return this;
  }

  public NexItemBuilder hideEnchants(boolean hide) {
    fast.hideEnchants(hide);
    return this;
  }

  public NexItemBuilder unbreakable(boolean unbreakable) {
    fast.unbreakable(unbreakable);
    return this;
  }

  public NexItemBuilder tooltipStyle(NamespacedKey styleKey) {
    fast.tooltipStyle(styleKey);
    return this;
  }

  public NexItemBuilder itemModel(NamespacedKey modelKey) {
    fast.itemModel(modelKey);
    return this;
  }

  public NexItemBuilder attribute(Attribute attribute,
                                  double amount,
                                  AttributeModifier.Operation operation,
                                  EquipmentSlotGroup slotGroup) {
    fast.attribute(attribute, amount, operation, slotGroup);
    return this;
  }

  public NexItemBuilder attribute(Attribute attribute,
                                  String suffix,
                                  double amount,
                                  AttributeModifier.Operation operation,
                                  EquipmentSlotGroup slotGroup) {
    fast.attribute(attribute, suffix, amount, operation, slotGroup);
    return this;
  }

  public NexItemBuilder edit(Consumer<ItemStack> editor) {
    fast.edit(editor);
    return this;
  }

  public ItemStack build() {
    return fast.getItemStack();
  }
}