package io.nexstudios.itemservice.bukkit.convert;

import io.nexstudios.itemservice.common.item.FastColor;
import io.nexstudios.itemservice.common.item.FastCustomModelData;
import io.nexstudios.itemservice.common.item.FastItemStack;
import io.nexstudios.itemservice.common.item.ItemId;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

public final class BukkitFastItemStackConverter {

  /**
   * Converts a Bukkit ItemStack to a FastItemStack using Paper's DataComponents only.
   */
  @SuppressWarnings("UnstableApiUsage")
  public FastItemStack fromItemStack(ItemStack stack) {
    Objects.requireNonNull(stack, "stack must not be null");

    NamespacedKey key = stack.getType().getKey();
    ItemId baseId = new ItemId(key.getNamespace(), key.getKey());

    Component name = stack.getData(DataComponentTypes.CUSTOM_NAME);

    ItemLore loreComponent = stack.getData(DataComponentTypes.LORE);
    List<Component> lore = loreComponent == null ? null : loreComponent.lines();

    CustomModelData cmd = stack.getData(DataComponentTypes.CUSTOM_MODEL_DATA);
    FastCustomModelData fastCmd = null;
    if (cmd != null) {
      FastCustomModelData tmp = FastCustomModelData.empty();

      if (!cmd.floats().isEmpty()) tmp = new FastCustomModelDataBuilder(tmp).addFloats(cmd.floats()).build();
      if (!cmd.flags().isEmpty()) tmp = new FastCustomModelDataBuilder(tmp).addFlags(cmd.flags()).build();
      if (!cmd.strings().isEmpty()) tmp = new FastCustomModelDataBuilder(tmp).addStrings(cmd.strings()).build();

      if (!cmd.colors().isEmpty()) {
        FastCustomModelDataBuilder b = new FastCustomModelDataBuilder(tmp);
        cmd.colors().forEach(color -> b.addColor(FastColor.rgb(color.getRed(), color.getGreen(), color.getBlue())));
        tmp = b.build();
      }

      if (!tmp.isEmpty()) fastCmd = tmp;
    }

    FastItemStack fast = FastItemStack.of(baseId).withAmount(stack.getAmount());
    if (name != null) fast = fast.withName(name);
    if (lore != null) fast = fast.withLore(lore);
    if (fastCmd != null) fast = fast.withCustomModelData(fastCmd);

    return fast;
  }

  /**
   * Local builder to avoid exposing mutability in FastCustomModelData.
   */
  private static final class FastCustomModelDataBuilder {
    private FastCustomModelData current;

    private FastCustomModelDataBuilder(FastCustomModelData start) {
      this.current = start;
    }

    private FastCustomModelDataBuilder addFloats(List<Float> floats) {
      for (Float f : floats) current = current.addFloat(f);
      return this;
    }

    private FastCustomModelDataBuilder addFlags(List<Boolean> flags) {
      for (Boolean b : flags) current = current.addFlag(b);
      return this;
    }

    private FastCustomModelDataBuilder addStrings(List<String> strings) {
      for (String s : strings) current = current.addString(s);
      return this;
    }

    private FastCustomModelDataBuilder addColor(FastColor color) {
      current = current.addColor(color);
      return this;
    }

    private FastCustomModelData build() {
      return current;
    }
  }
}