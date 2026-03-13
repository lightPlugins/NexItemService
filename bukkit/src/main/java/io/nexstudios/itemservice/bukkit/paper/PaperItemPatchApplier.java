package io.nexstudios.itemservice.bukkit.paper;

import io.nexstudios.itemservice.common.item.FastAttributeOperation;
import io.nexstudios.itemservice.common.item.FastAttributeSpec;
import io.nexstudios.itemservice.common.item.FastColor;
import io.nexstudios.itemservice.common.item.FastCustomModelData;
import io.nexstudios.itemservice.common.item.FastItemFlag;
import io.nexstudios.itemservice.common.item.FastItemPatch;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import io.papermc.paper.datacomponent.item.attribute.AttributeModifierDisplay;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public final class PaperItemPatchApplier {

  @SuppressWarnings("UnstableApiUsage")
  public ItemStack apply(ItemStack stack, FastItemPatch patch) {
    Objects.requireNonNull(stack, "stack must not be null");
    Objects.requireNonNull(patch, "patch must not be null");

    if (patch.maxStackSize() != null) {
      stack.setData(DataComponentTypes.MAX_STACK_SIZE, patch.maxStackSize());
    }

    if (patch.name() != null) {
      stack.setData(DataComponentTypes.CUSTOM_NAME, patch.name());
    }

    if (patch.lore() != null) {
      ItemLore.Builder lb = ItemLore.lore();
      for (Component line : patch.lore()) lb.addLine(line == null ? Component.empty() : line);
      stack.setData(DataComponentTypes.LORE, lb.build());
    }

    if (patch.unbreakable() != null && patch.unbreakable()) {
      stack.setData(DataComponentTypes.UNBREAKABLE);
    }

    FastCustomModelData customModelData = patch.customModelData();
    if (customModelData != null && !customModelData.isEmpty()) {
      CustomModelData.Builder cmd = CustomModelData.customModelData();
      if (!customModelData.floats().isEmpty()) cmd.addFloats(customModelData.floats());
      if (!customModelData.flags().isEmpty()) cmd.addFlags(customModelData.flags());
      if (!customModelData.strings().isEmpty()) cmd.addStrings(customModelData.strings());
      if (!customModelData.colors().isEmpty()) {
        for (FastColor c : customModelData.colors()) {
          cmd.addColor(Color.fromRGB(c.red(), c.green(), c.blue()));
        }
      }
      stack.setData(DataComponentTypes.CUSTOM_MODEL_DATA, cmd.build());
    }

    if (patch.tooltipStyleKey() != null) {
      NamespacedKey key = NamespacedKey.fromString(patch.tooltipStyleKey());
      if (key != null) stack.setData(DataComponentTypes.TOOLTIP_STYLE, key);
    }

    if (patch.itemModelKey() != null) {
      NamespacedKey key = NamespacedKey.fromString(patch.itemModelKey());
      if (key != null) stack.setData(DataComponentTypes.ITEM_MODEL, key);
    }

    if (patch.enchantments() != null && !patch.enchantments().isEmpty()) {
      Registry<@NotNull Enchantment> enchRegistry =
          RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);

      ItemEnchantments.Builder eb = ItemEnchantments.itemEnchantments();
      for (Map.Entry<String, Integer> e : patch.enchantments().entrySet()) {
        NamespacedKey enchKey = NamespacedKey.fromString(e.getKey());
        if (enchKey == null) continue;

        Enchantment ench = enchRegistry.get(enchKey);
        if (ench == null) continue;

        int lvl = Math.max(1, e.getValue() == null ? 1 : e.getValue());
        eb.add(ench, lvl);
      }
      stack.setData(DataComponentTypes.ENCHANTMENTS, eb.build());
    }

    if (patch.attributes() != null && !patch.attributes().isEmpty()) {
      ItemAttributeModifiers.Builder ab = ItemAttributeModifiers.itemAttributes();

      for (FastAttributeSpec a : patch.attributes()) {
        if (a == null) continue;

        Attribute bukkitAttr = resolveBukkitAttributeKey(a.attributeKey());
        if (bukkitAttr == null) continue;

        AttributeModifier.Operation op = switch (Objects.requireNonNullElse(a.operation(), FastAttributeOperation.ADD_NUMBER)) {
          case ADD_SCALAR -> AttributeModifier.Operation.ADD_SCALAR;
          case MULTIPLY_SCALAR_1 -> AttributeModifier.Operation.MULTIPLY_SCALAR_1;
          default -> AttributeModifier.Operation.ADD_NUMBER;
        };

        String attrKeySafe = a.attributeKey();
        String rawId = (attrKeySafe + "_" + (a.name() == null ? "" : a.name())).toLowerCase(Locale.ROOT);
        String safeId = rawId.replaceAll("[^a-z0-9._-]", "_");

        NamespacedKey baseKey = NamespacedKey.fromString(attrKeySafe);
        if (baseKey == null) baseKey = NamespacedKey.minecraft("attr");

        if (a.slots() == null || a.slots().isEmpty()) {
          NamespacedKey key = new NamespacedKey(baseKey.getNamespace(), safeId);
          AttributeModifier mod = new AttributeModifier(key, a.amount(), op);
          ab.addModifier(bukkitAttr, mod);
        } else {
          for (String s : a.slots()) {
            if (s == null) continue;
            EquipmentSlot slot;
            try {
              slot = EquipmentSlot.valueOf(s);
            } catch (IllegalArgumentException ex) {
              continue;
            }
            String perSlotId = safeId + "_" + slot.name().toLowerCase(Locale.ROOT);
            NamespacedKey key = new NamespacedKey(baseKey.getNamespace(), perSlotId);
            AttributeModifier mod = new AttributeModifier(key, a.amount(), op);
            ab.addModifier(bukkitAttr, mod, slot.getGroup());
          }
        }
      }

      stack.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, ab.build());
    }

    if (patch.flags() != null && patch.flags().contains(FastItemFlag.HIDE_ATTRIBUTES)) {
      ItemAttributeModifiers current = stack.getData(DataComponentTypes.ATTRIBUTE_MODIFIERS);
      if (current != null) {
        ItemAttributeModifiers.Builder hidden = ItemAttributeModifiers.itemAttributes();
        for (ItemAttributeModifiers.Entry entry : current.modifiers()) {
          EquipmentSlotGroup group = entry.getGroup();
          hidden.addModifier(
              entry.attribute(),
              entry.modifier(),
              group,
              AttributeModifierDisplay.hidden()
          );
        }
        stack.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, hidden.build());
      }
    }

    // Build TooltipDisplay once to avoid overwriting previous settings
    TooltipDisplay.Builder tooltip = null;

    if (patch.flags() != null && patch.flags().contains(FastItemFlag.HIDE_ENCHANTS) && stack.hasData(DataComponentTypes.ENCHANTMENTS)) {
      tooltip = TooltipDisplay.tooltipDisplay()
          .addHiddenComponents(DataComponentTypes.ENCHANTMENTS);
    }

    if (patch.hideTooltip()) {
      tooltip = (tooltip == null ? TooltipDisplay.tooltipDisplay() : tooltip).hideTooltip(true);
    }

    if (tooltip != null) {
      stack.setData(DataComponentTypes.TOOLTIP_DISPLAY, tooltip.build());
    }

    return stack;
  }

  private static Attribute resolveBukkitAttributeKey(String attributeKey) {
    if (attributeKey == null) return null;
    String full = attributeKey.toLowerCase(Locale.ROOT);

    if (full.endsWith("attack_damage")) return Attribute.ATTACK_DAMAGE;
    if (full.endsWith("attack_speed")) return Attribute.ATTACK_SPEED;
    if (full.endsWith("armor")) return Attribute.ARMOR;
    if (full.endsWith("armor_toughness")) return Attribute.ARMOR_TOUGHNESS;
    if (full.endsWith("max_health")) return Attribute.MAX_HEALTH;
    if (full.endsWith("movement_speed")) return Attribute.MOVEMENT_SPEED;
    if (full.endsWith("knockback_resistance")) return Attribute.KNOCKBACK_RESISTANCE;
    if (full.endsWith("luck")) return Attribute.LUCK;
    if (full.endsWith("attack_knockback")) return Attribute.ATTACK_KNOCKBACK;
    if (full.endsWith("step_height")) return Attribute.STEP_HEIGHT;
    if (full.endsWith("fall_damage_multiplier")) return Attribute.FALL_DAMAGE_MULTIPLIER;
    if (full.endsWith("jump_strength")) return Attribute.JUMP_STRENGTH;
    if (full.endsWith("gravity")) return Attribute.GRAVITY;
    if (full.endsWith("spawn_reinforcements")) return Attribute.SPAWN_REINFORCEMENTS;

    return null;
  }
}