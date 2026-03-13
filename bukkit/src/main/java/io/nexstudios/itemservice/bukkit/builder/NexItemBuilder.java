package io.nexstudios.itemservice.bukkit.builder;

import io.nexstudios.itemservice.bukkit.builder.lore.NexLoreBuilder;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import io.papermc.paper.datacomponent.item.attribute.AttributeModifierDisplay;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

@SuppressWarnings("UnstableApiUsage")
public final class NexItemBuilder {

  private static final PersistentDataType<Byte, Byte> PDC_BOOL = PersistentDataType.BYTE;

  private record PendingAttribute(
      Attribute attribute,
      String idSuffix,
      double amount,
      AttributeModifier.Operation operation,
      EquipmentSlotGroup slotGroup
  ) {}

  private final Plugin plugin;
  private final ItemStack stack;

  private final List<PendingAttribute> pendingAttributes = new ArrayList<>();
  private final Set<ItemFlag> flags = EnumSet.noneOf(ItemFlag.class);

  private final NexLoreBuilder loreBuilder = new NexLoreBuilder();
  private boolean hasLore = false;

  private boolean hideTooltips = false;
  private boolean hideEnchants = false;

  private int amount = 1;
  private Integer maxStackSize;

  private Boolean unbreakable;
  private NamespacedKey tooltipStyleKey;
  private NamespacedKey itemModelKey;

  public NexItemBuilder(Plugin plugin, Material material) {
    this.plugin = Objects.requireNonNull(plugin, "plugin must not be null");
    Objects.requireNonNull(material, "material must not be null");
    this.stack = ItemStack.of(material);
  }

  public NexItemBuilder amount(int amount) {
    if (amount < 1) {
      throw new IllegalArgumentException("amount must be >= 1");
    }
    this.amount = amount;
    return this;
  }

  public NexItemBuilder maxStackSize(int maxStackSize) {
    if (maxStackSize < 1) {
      throw new IllegalArgumentException("maxStackSize must be >= 1");
    }
    if (maxStackSize > 99) {
      throw new IllegalArgumentException("maxStackSize must be <= 99");
    }
    this.maxStackSize = maxStackSize;
    stack.setData(DataComponentTypes.MAX_STACK_SIZE, maxStackSize);
    return this;
  }

  public NexItemBuilder mark(NamespacedKey key) {
    Objects.requireNonNull(key, "key must not be null");
    stack.editPersistentDataContainer(pdc -> pdc.set(key, PDC_BOOL, (byte) 1));
    return this;
  }

  public NexItemBuilder pdcString(NamespacedKey key, String value) {
    Objects.requireNonNull(key, "key must not be null");
    Objects.requireNonNull(value, "value must not be null");
    stack.editPersistentDataContainer(pdc -> pdc.set(key, PersistentDataType.STRING, value));
    return this;
  }

  public NexItemBuilder name(Component name) {
    Objects.requireNonNull(name, "name must not be null");
    stack.setData(DataComponentTypes.CUSTOM_NAME, name);
    return this;
  }

  public NexItemBuilder lore(Component... lines) {
    Objects.requireNonNull(lines, "lines must not be null");
    loreBuilder.lines(lines);
    hasLore = true;
    return this;
  }

  public NexItemBuilder lore(Consumer<NexLoreBuilder> editor) {
    Objects.requireNonNull(editor, "editor must not be null");
    editor.accept(loreBuilder);
    hasLore = true;
    return this;
  }

  public NexItemBuilder customModelData(int cmd) {
    stack.setData(
        DataComponentTypes.CUSTOM_MODEL_DATA,
        CustomModelData.customModelData()
            .addFloat((float) cmd)
            .build()
    );
    return this;
  }

  public NexItemBuilder enchant(Enchantment enchantment, int level) {
    Objects.requireNonNull(enchantment, "enchantment must not be null");
    if (level < 1) throw new IllegalArgumentException("level must be >= 1");

    stack.setData(
        DataComponentTypes.ENCHANTMENTS,
        ItemEnchantments.itemEnchantments()
            .add(enchantment, level)
            .build()
    );

    return this;
  }

  public NexItemBuilder flags(ItemFlag... flags) {
    Objects.requireNonNull(flags, "flags must not be null");
    for (ItemFlag f : flags) {
      if (f != null) this.flags.add(f);
    }
    return this;
  }

  public NexItemBuilder hideToolTips(boolean hide) {
    this.hideTooltips = hide;
    return this;
  }

  public NexItemBuilder hideEnchants(boolean hide) {
    this.hideEnchants = hide;
    return this;
  }

  public NexItemBuilder unbreakable(boolean unbreakable) {
    this.unbreakable = unbreakable;
    return this;
  }

  public NexItemBuilder tooltipStyle(NamespacedKey styleKey) {
    this.tooltipStyleKey = styleKey;
    return this;
  }

  public NexItemBuilder itemModel(NamespacedKey modelKey) {
    this.itemModelKey = modelKey;
    return this;
  }

  public NexItemBuilder attribute(Attribute attribute,
                                  double amount,
                                  AttributeModifier.Operation operation,
                                  EquipmentSlotGroup slotGroup) {
    Objects.requireNonNull(attribute, "attribute must not be null");
    Objects.requireNonNull(operation, "operation must not be null");
    Objects.requireNonNull(slotGroup, "slotGroup must not be null");

    String autoSuffix = attribute.key()
        .asMinimalString()
        .toLowerCase(Locale.ROOT)
        .replace(':', '_');

    pendingAttributes.add(new PendingAttribute(attribute, autoSuffix, amount, operation, slotGroup));
    return this;
  }

  public NexItemBuilder attribute(Attribute attribute,
                                  String suffix,
                                  double amount,
                                  AttributeModifier.Operation operation,
                                  EquipmentSlotGroup slotGroup) {
    Objects.requireNonNull(attribute, "attribute must not be null");
    Objects.requireNonNull(suffix, "suffix must not be null");
    Objects.requireNonNull(operation, "operation must not be null");
    Objects.requireNonNull(slotGroup, "slotGroup must not be null");

    String safeSuffix = suffix
        .toLowerCase(Locale.ROOT)
        .replace(':', '_');

    pendingAttributes.add(new PendingAttribute(attribute, safeSuffix, amount, operation, slotGroup));
    return this;
  }

  public NexItemBuilder edit(Consumer<ItemStack> editor) {
    editor.accept(stack);
    return this;
  }

  public ItemStack build() {
    // 0) Simple components
    if (tooltipStyleKey != null) {
      stack.setData(DataComponentTypes.TOOLTIP_STYLE, tooltipStyleKey);
    }

    if (itemModelKey != null) {
      stack.setData(DataComponentTypes.ITEM_MODEL, itemModelKey);
    }

    if (unbreakable != null) {
      if (unbreakable) {
        stack.setData(DataComponentTypes.UNBREAKABLE);
      } else {
        stack.unsetData(DataComponentTypes.UNBREAKABLE);
      }
    }

    if (hasLore) {
      ItemLore.Builder lb = ItemLore.lore();
      for (Component line : loreBuilder.build()) {
        lb.addLine(line == null ? Component.empty() : line);
      }
      stack.setData(DataComponentTypes.LORE, lb.build());
    }

    // 1) Attributes (as patch)
    if (!pendingAttributes.isEmpty()) {
      ItemAttributeModifiers.Builder ab = ItemAttributeModifiers.itemAttributes();

      for (PendingAttribute a : pendingAttributes) {
        String rawId = ("attr_" + a.idSuffix() + "_" + a.slotGroup().toString()).toLowerCase(Locale.ROOT);
        String safeId = rawId.replaceAll("[^a-z0-9._-]", "_");

        NamespacedKey modifierKey = new NamespacedKey(plugin, safeId);
        AttributeModifier mod = new AttributeModifier(modifierKey, a.amount(), a.operation());

        ab.addModifier(a.attribute(), mod, a.slotGroup());
      }

      stack.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, ab.build());
    }

    // 2) Hide attributes via AttributeModifierDisplay.hidden()
    if (flags.contains(ItemFlag.HIDE_ATTRIBUTES) && stack.hasData(DataComponentTypes.ATTRIBUTE_MODIFIERS)) {
      ItemAttributeModifiers current = stack.getData(DataComponentTypes.ATTRIBUTE_MODIFIERS);
      if (current != null) {
        ItemAttributeModifiers.Builder hidden = ItemAttributeModifiers.itemAttributes();
        for (ItemAttributeModifiers.Entry entry : current.modifiers()) {
          hidden.addModifier(
              entry.attribute(),
              entry.modifier(),
              entry.getGroup(),
              AttributeModifierDisplay.hidden()
          );
        }
        stack.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, hidden.build());
      }
    }

    // 3) Tooltip hiding (TOOLTIP_DISPLAY)
    TooltipDisplay.Builder tb = null;

    boolean effectiveHideEnchants = hideEnchants || flags.contains(ItemFlag.HIDE_ENCHANTS);
    boolean effectiveHideTooltips = hideTooltips;

    // Map Bukkit ItemFlags -> hidden components (best-effort, DataComponents-only)
    for (ItemFlag f : flags) {
      if (f == null) continue;
      tb = applyHiddenComponentForFlag(tb, f);
    }

    if (effectiveHideEnchants && stack.hasData(DataComponentTypes.ENCHANTMENTS)) {
      if (tb == null) tb = TooltipDisplay.tooltipDisplay();
      tb.addHiddenComponents(DataComponentTypes.ENCHANTMENTS);
    }

    if (effectiveHideTooltips) {
      if (tb == null) tb = TooltipDisplay.tooltipDisplay();

      // Preset: hide the most common “noisy” components if present
      if (stack.hasData(DataComponentTypes.ENCHANTMENTS)) {
        tb.addHiddenComponents(DataComponentTypes.ENCHANTMENTS);
      }
      if (stack.hasData(DataComponentTypes.ATTRIBUTE_MODIFIERS)) {
        tb.addHiddenComponents(DataComponentTypes.ATTRIBUTE_MODIFIERS);
      }
      if (stack.hasData(DataComponentTypes.UNBREAKABLE)) {
        tb.addHiddenComponents(DataComponentTypes.UNBREAKABLE);
      }
      if (stack.hasData(DataComponentTypes.CAN_BREAK)) {
        tb.addHiddenComponents(DataComponentTypes.CAN_BREAK);
      }
      if (stack.hasData(DataComponentTypes.CAN_PLACE_ON)) {
        tb.addHiddenComponents(DataComponentTypes.CAN_PLACE_ON);
      }
    }

    if (tb != null) {
      stack.setData(DataComponentTypes.TOOLTIP_DISPLAY, tb.build());
    }

    // 4) Amount (clamp to max stack size if set)
    int finalAmount = amount;
    if (maxStackSize != null) {
      finalAmount = Math.min(finalAmount, maxStackSize);
    }
    stack.setAmount(finalAmount);

    return stack;
  }

  private TooltipDisplay.Builder applyHiddenComponentForFlag(TooltipDisplay.Builder tb, ItemFlag flag) {
    Objects.requireNonNull(flag, "flag must not be null");

    return switch (flag) {
      case HIDE_ENCHANTS -> {
        if (stack.hasData(DataComponentTypes.ENCHANTMENTS)) {
          if (tb == null) tb = TooltipDisplay.tooltipDisplay();
          tb.addHiddenComponents(DataComponentTypes.ENCHANTMENTS);
        }
        yield tb;
      }
      case HIDE_ATTRIBUTES -> {
        if (stack.hasData(DataComponentTypes.ATTRIBUTE_MODIFIERS)) {
          if (tb == null) tb = TooltipDisplay.tooltipDisplay();
          tb.addHiddenComponents(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        }
        yield tb;
      }
      case HIDE_UNBREAKABLE -> {
        if (stack.hasData(DataComponentTypes.UNBREAKABLE)) {
          if (tb == null) tb = TooltipDisplay.tooltipDisplay();
          tb.addHiddenComponents(DataComponentTypes.UNBREAKABLE);
        }
        yield tb;
      }
      case HIDE_DESTROYS -> {
        if (stack.hasData(DataComponentTypes.CAN_BREAK)) {
          if (tb == null) tb = TooltipDisplay.tooltipDisplay();
          tb.addHiddenComponents(DataComponentTypes.CAN_BREAK);
        }
        yield tb;
      }
      case HIDE_PLACED_ON -> {
        if (stack.hasData(DataComponentTypes.CAN_PLACE_ON)) {
          if (tb == null) tb = TooltipDisplay.tooltipDisplay();
          tb.addHiddenComponents(DataComponentTypes.CAN_PLACE_ON);
        }
        yield tb;
      }
      case HIDE_DYE -> {
        if (stack.hasData(DataComponentTypes.DYED_COLOR)) {
          if (tb == null) tb = TooltipDisplay.tooltipDisplay();
          tb.addHiddenComponents(DataComponentTypes.DYED_COLOR);
        }
        yield tb;
      }
      case HIDE_ARMOR_TRIM -> {
        if (stack.hasData(DataComponentTypes.TRIM)) {
          if (tb == null) tb = TooltipDisplay.tooltipDisplay();
          tb.addHiddenComponents(DataComponentTypes.TRIM);
        }
        yield tb;
      }
      default -> tb; // Unsupported flag in DataComponents mapping (no-op)
    };
  }
}