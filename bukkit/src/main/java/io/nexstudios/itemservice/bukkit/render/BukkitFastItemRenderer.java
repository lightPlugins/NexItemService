package io.nexstudios.itemservice.bukkit.render;

import io.nexstudios.itemservice.bukkit.paper.PaperItemPatchApplier;
import io.nexstudios.itemservice.bukkit.provider.ItemBaseProvider;
import io.nexstudios.itemservice.bukkit.provider.ItemBaseProviderRegistry;
import io.nexstudios.itemservice.common.item.FastItemStack;
import io.nexstudios.itemservice.common.item.ItemId;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public final class BukkitFastItemRenderer {

  private final ItemBaseProviderRegistry providers;
  private final PaperItemPatchApplier patchApplier;

  public BukkitFastItemRenderer(ItemBaseProviderRegistry providers, PaperItemPatchApplier patchApplier) {
    this.providers = Objects.requireNonNull(providers, "providers must not be null");
    this.patchApplier = Objects.requireNonNull(patchApplier, "patchApplier must not be null");
  }

  public ItemStack render(FastItemStack fast) {
    Objects.requireNonNull(fast, "fast must not be null");

    ItemId id = fast.baseId();
    ItemBaseProvider provider = providers.find(id.namespace())
        .orElseThrow(() -> new IllegalArgumentException("No provider for namespace: " + id.namespace()));

    ItemStack stack = provider.createBase(id);
    stack.setAmount(fast.amount());

    patchApplier.apply(stack, fast.patch());
    return stack;
  }
}