package io.nexstudios.itemservice.bukkit.fast;

import io.nexstudios.itemservice.bukkit.builder.lore.NexLoreBuilder;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import io.papermc.paper.datacomponent.item.attribute.AttributeModifierDisplay;
import net.kyori.adventure.text.Component;
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

@SuppressWarnings({"UnstableApiUsage", "UnusedReturnValue"})
public final class FastItemStack {

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

  private final NexLoreBuilder loreBuilder = new NexLoreBuilder();
  private boolean hasLore = false;
  private boolean loreSeededFromStack = false;

  private final List<PendingAttribute> pendingAttributes = new ArrayList<>();
  private final Set<ItemFlag> flags = EnumSet.noneOf(ItemFlag.class);

  private boolean hideTooltips = false;
  private boolean hideEnchants = false;

  private Boolean unbreakable;
  private NamespacedKey tooltipStyleKey;
  private NamespacedKey itemModelKey;

  private Integer maxStackSize;

  private FastItemStack(Plugin plugin, ItemStack stack) {
    this.plugin = Objects.requireNonNull(plugin, "plugin must not be null");
    this.stack = Objects.requireNonNull(stack, "stack must not be null");
  }

  public static FastItemStack wrap(Plugin plugin, ItemStack stack) {
    return new FastItemStack(plugin, stack);
  }

  public ItemStack getItemStack() {
    apply();
    return stack;
  }

  public FastItemStack apply() {
    applySimpleComponents();
    applyLoreIfPresent();
    applyAttributesIfPresent();
    applyHideAttributesIfRequested();
    applyTooltipDisplayIfNeeded();
    clampAmountToMaxStackSize();
    return this;
  }

  public FastItemStack amount(int amount) {
    stack.setAmount(Math.max(1, amount));
    clampAmountToMaxStackSize();
    return this;
  }

  public FastItemStack maxStackSize(int maxStackSize) {
    if (maxStackSize < 1) {
      throw new IllegalArgumentException("maxStackSize must be >= 1");
    }
    if (maxStackSize > 99) {
      throw new IllegalArgumentException("maxStackSize must be <= 99");
    }
    this.maxStackSize = maxStackSize;
    stack.setData(DataComponentTypes.MAX_STACK_SIZE, maxStackSize);
    clampAmountToMaxStackSize();
    return this;
  }

  public FastItemStack mark(NamespacedKey key) {
    Objects.requireNonNull(key, "key must not be null");
    stack.editPersistentDataContainer(pdc -> pdc.set(key, PDC_BOOL, (byte) 1));
    return this;
  }

  public FastItemStack pdcString(NamespacedKey key, String value) {
    Objects.requireNonNull(key, "key must not be null");
    Objects.requireNonNull(value, "value must not be null");
    stack.editPersistentDataContainer(pdc -> pdc.set(key, PersistentDataType.STRING, value));
    return this;
  }

  public FastItemStack name(Component name) {
    Objects.requireNonNull(name, "name must not be null");
    stack.setData(DataComponentTypes.CUSTOM_NAME, name);
    return this;
  }

  public FastItemStack lore(Consumer<NexLoreBuilder> editor) {
    Objects.requireNonNull(editor, "editor must not be null");
    seedLoreFromStackIfNeeded();
    editor.accept(loreBuilder);
    hasLore = true;
    applyLoreIfPresent();
    return this;
  }

  public FastItemStack lore(Component... lines) {
    Objects.requireNonNull(lines, "lines must not be null");
    loreBuilder.setLines(lines);
    loreSeededFromStack = true;
    hasLore = true;
    applyLoreIfPresent();
    return this;
  }

  public FastItemStack customModelData(int cmd) {
    stack.setData(
        DataComponentTypes.CUSTOM_MODEL_DATA,
        CustomModelData.customModelData()
            .addFloat((float) cmd)
            .build()
    );
    return this;
  }

  public FastItemStack enchant(Enchantment enchantment, int level) {
    Objects.requireNonNull(enchantment, "enchantment must not be null");
    if (level < 1) throw new IllegalArgumentException("level must be >= 1");

    stack.setData(
        DataComponentTypes.ENCHANTMENTS,
        ItemEnchantments.itemEnchantments()
            .add(enchantment, level)
            .build()
    );
    applyTooltipDisplayIfNeeded();
    return this;
  }

  public FastItemStack flags(ItemFlag... flags) {
    Objects.requireNonNull(flags, "flags must not be null");
    for (ItemFlag f : flags) {
      if (f != null) this.flags.add(f);
    }
    applyHideAttributesIfRequested();
    applyTooltipDisplayIfNeeded();
    return this;
  }

  public FastItemStack hideToolTips(boolean hide) {
    this.hideTooltips = hide;
    applyTooltipDisplayIfNeeded();
    return this;
  }

  public FastItemStack hideEnchants(boolean hide) {
    this.hideEnchants = hide;
    applyTooltipDisplayIfNeeded();
    return this;
  }

  public FastItemStack unbreakable(boolean unbreakable) {
    this.unbreakable = unbreakable;
    applySimpleComponents();
    applyTooltipDisplayIfNeeded();
    return this;
  }

  public FastItemStack tooltipStyle(NamespacedKey styleKey) {
    this.tooltipStyleKey = styleKey;
    applySimpleComponents();
    return this;
  }

  public FastItemStack itemModel(NamespacedKey modelKey) {
    this.itemModelKey = modelKey;
    applySimpleComponents();
    return this;
  }

  public FastItemStack attribute(Attribute attribute,
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
    applyAttributesIfPresent();
    applyHideAttributesIfRequested();
    applyTooltipDisplayIfNeeded();
    return this;
  }

  public FastItemStack attribute(Attribute attribute,
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
    applyAttributesIfPresent();
    applyHideAttributesIfRequested();
    applyTooltipDisplayIfNeeded();
    return this;
  }

  public FastItemStack edit(Consumer<ItemStack> editor) {
    Objects.requireNonNull(editor, "editor must not be null");
    editor.accept(stack);
    return this;
  }

  private void applySimpleComponents() {
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
  }

  private void applyLoreIfPresent() {
    if (!hasLore) return;

    ItemLore.Builder lb = ItemLore.lore();
    for (Component line : loreBuilder.build()) {
      lb.addLine(line == null ? Component.empty() : line);
    }
    stack.setData(DataComponentTypes.LORE, lb.build());
  }

  private void seedLoreFromStackIfNeeded() {
    if (loreSeededFromStack) return;
    loreSeededFromStack = true;

    ItemLore existingLore = stack.getData(DataComponentTypes.LORE);
    if (existingLore == null) return;

    loreBuilder.importRenderedAsTemplates(existingLore.lines());
  }

  private void applyAttributesIfPresent() {
    if (pendingAttributes.isEmpty()) return;

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

  private void applyHideAttributesIfRequested() {
    if (!flags.contains(ItemFlag.HIDE_ATTRIBUTES)) return;
    if (!stack.hasData(DataComponentTypes.ATTRIBUTE_MODIFIERS)) return;

    ItemAttributeModifiers current = stack.getData(DataComponentTypes.ATTRIBUTE_MODIFIERS);
    if (current == null) return;

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

  private void applyTooltipDisplayIfNeeded() {
    TooltipDisplay.Builder tb = null;

    // Map flags -> hidden components
    for (ItemFlag f : flags) {
      if (f == null) continue;
      tb = applyHiddenComponentForFlag(tb, f);
    }

    boolean effectiveHideEnchants = hideEnchants || flags.contains(ItemFlag.HIDE_ENCHANTS);
    boolean effectiveHideTooltips = hideTooltips;

    if (effectiveHideEnchants && stack.hasData(DataComponentTypes.ENCHANTMENTS)) {
      if (tb == null) tb = TooltipDisplay.tooltipDisplay();
      tb.addHiddenComponents(DataComponentTypes.ENCHANTMENTS);
    }

    if (effectiveHideTooltips) {
      if (tb == null) tb = TooltipDisplay.tooltipDisplay();

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
  }

  private TooltipDisplay.Builder applyHiddenComponentForFlag(TooltipDisplay.Builder tb, ItemFlag flag) {
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
      default -> tb;
    };
  }

  private void clampAmountToMaxStackSize() {
    if (maxStackSize == null) return;
    int current = stack.getAmount();
    if (current > maxStackSize) {
      stack.setAmount(maxStackSize);
    }
  }
}