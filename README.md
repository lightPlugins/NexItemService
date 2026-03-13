# ✨ NexItemService

> A lightweight item service for **Paper 1.21+** built around the **DataComponents API** — completely **without `ItemMeta`** — plus **PersistentDataContainer (PDC)** support for reliable item identification.

---

## 🚀 Features

A quick overview of what this module gives you out of the box.

- **Fluent item creation** via `NexItemBuilder`
- **Fast patching** of existing `ItemStack`s via `FastItemStack`
- **PDC helpers** like `mark(...)` and `pdcString(...)` for robust item identification
- **Testable matchers** such as `PdcMarkerTestable` and `PdcStringEqualsTestable`
- **Composable matcher utilities** via `Testables.and(...)`, `or(...)`, `not(...)`, ...
- **Lore DSL** with `NexLoreBuilder`
    - raw `Component` lines
    - MiniMessage + `TagResolver`
    - legacy `&` color code support
    - token replacement like `#rewards#`

> [!TIP]
> This library is especially useful if you want a clean and modern item API for Paper 1.21+ without falling back to old `ItemMeta` patterns.

---

## 📦 Installation (ServiceRegistry)

Use the service module to register the `ItemService` in your `ServiceAccessor`.

- `ItemServiceModule` registers the implementation
- access the service with `services.getService(ItemService.class)`

### Install this module

Small bootstrap example for registering the module inside your plugin.

```java
public final class Bootstrap {

  public static ItemService install(Plugin plugin, ServiceAccessor services) {
    // Register the item service module in the service registry
    services.install(new ItemServiceModule(plugin));

    // Retrieve and return the ItemService implementation
    return services.getService(ItemService.class);
  }
}
```

---

## 🧭 API Overview

A short summary of the most important entry points.

### Create new items

Create a fresh item with `ItemService#builder(Material)` and finalize it with `build()`.

### Patch existing items

Modify an existing `ItemStack` with `ItemService#fast(ItemStack)` and get the result via `getItemStack()`.

### Identification

Use PDC-based helpers to attach custom identifiers to items.

- `mark(key)` → writes a boolean marker to PDC (`BYTE=1`)
- `pdcString(key, value)` → writes a string to PDC

### Matching

Use `Testable` implementations directly or combine them through `Testables` utilities.

---

## 📝 Examples

Below are a few practical examples to get started quickly.

### Minimal Example

The smallest possible example: create a simple `ItemStack` with the builder API.

```java
public final class MinimalExample {

  public static ItemStack build(ItemService items) {
    // Create a new diamond item with default settings
    return items.builder(Material.DIAMOND).build();
  }
}
```

### Full Example

A more complete example showing name, lore, PDC data, attributes, enchantments and flags.

```java
public final class FullExample {

  public static ItemStack build(ItemService items, Player player) {
    // Create reusable keys for item identification in the PDC
    NamespacedKey markerKey = items.key("marker");
    NamespacedKey itemIdKey = items.key("item_id");

    // Build a MiniMessage resolver with dynamic placeholders
    TagResolver resolver = TagResolver.resolver(
        Placeholder.unparsed("player", player.getName()),
        Placeholder.unparsed("id", "blade_of_kings")
    );

    return items.builder(Material.DIAMOND_SWORD)
        // Basic stack settings
        .amount(1)
        .maxStackSize(1)

        // Mark and identify the item via PDC
        .mark(markerKey)
        .pdcString(itemIdKey, "blade_of_kings")

        // Set the display name
        .name(Component.text("Blade of Kings"))

        // Build rich lore with placeholders and token replacement
        .lore(l -> l
            .tagResolver(resolver)
            .line("&7A relic of old times.")
            .line("&8ID: &f<id>")
            .line("<gold>Owner:</gold> <yellow><player></yellow>")
            .line("#rewards#")
            .replaceToken("#rewards#", List.of(
                Component.text("Rewards:"),
                Component.text("- 500 coins"),
                Component.text("- 1x Mystery Key")
            ))
        )

        // Apply custom model data and make the item unbreakable
        .customModelData(9001)
        .unbreakable(true)

        // Add enchantments
        .enchant(Enchantment.SHARPNESS, 6)

        // Add attribute modifiers for combat behavior
        .attribute(Attribute.ATTACK_DAMAGE, 13.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND)
        .attribute(Attribute.ATTACK_SPEED, -2.2, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND)

        // Hide selected tooltip information
        .flags(
            ItemFlag.HIDE_ENCHANTS,
            ItemFlag.HIDE_ATTRIBUTES,
            ItemFlag.HIDE_UNBREAKABLE,
            ItemFlag.HIDE_DYE,
            ItemFlag.HIDE_ARMOR_TRIM
        )

        // Build and return the final item
        .build();
  }
}
```

### Patch an existing ItemStack

Use the fast patching API when you want to update an already existing item.

```java
public final class PatchExample {

  public static ItemStack patch(ItemService items, ItemStack existing) {
    return items.fast(existing)
        // Change the display name of the existing item
        .name(Component.text("Patched Name"))

        // Hide enchantments in the tooltip
        .hideEnchants(true)

        // Return the patched item stack
        .getItemStack();
  }
}
```

### Check with Testables

Example for checking whether an item matches a custom PDC-based identity.

```java
public static boolean isBlade(ItemService items, ItemStack stack) {
  // PDC key used to store the logical item id
  NamespacedKey idKey = items.key("item_id");

  // Combine multiple checks into a single matcher
  Testable matcher = Testables.and(
      items.testableMarker("marker"),
      items.testableString(idKey, "blade_of_kings")
  );

  // Return true only if all matcher conditions are met
  return matcher.test(stack);
}
```

---

## ⚠️ Notes

Important implementation details and current limitations.

> [!IMPORTANT]
> The **DataComponents API is version-specific**. Breaking changes between Minecraft/Paper versions are possible.

> [!WARNING]
> Enchantments currently **overwrite** the `ENCHANTMENTS` component instead of merging with existing values.

> [!NOTE]
> Some Bukkit `ItemFlag`s are mapped to tooltip hiding through `TooltipDisplay`, where applicable.

---

## ✅ Summary

`NexItemService` is a small but powerful utility for building and identifying items on modern Paper versions using the newer component-based approach.

If you're building custom item systems, RPG mechanics, crates, shops, or unique identifiers, this should make the workflow a lot cleaner.
