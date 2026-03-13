package io.nexstudios.itemservice.bukkit.service;

import io.nexstudios.itemservice.bukkit.api.BukkitFastItemBuilder;
import io.nexstudios.itemservice.bukkit.api.BukkitItemService;
import io.nexstudios.itemservice.bukkit.convert.BukkitFastItemStackConverter;
import io.nexstudios.itemservice.bukkit.paper.PaperItemPatchApplier;
import io.nexstudios.itemservice.bukkit.provider.ItemBaseProviderRegistry;
import io.nexstudios.itemservice.bukkit.provider.MinecraftItemBaseProvider;
import io.nexstudios.itemservice.bukkit.render.BukkitFastItemRenderer;
import io.nexstudios.itemservice.common.api.FastItemBuilder;
import io.nexstudios.itemservice.common.item.FastItemStack;
import io.nexstudios.itemservice.common.impl.DefaultFastItemBuilder;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public final class BukkitItemServiceImpl implements BukkitItemService {

  private final BukkitFastItemRenderer renderer;
  private final BukkitFastItemStackConverter converter;

  public BukkitItemServiceImpl() {
    ItemBaseProviderRegistry providers = new ItemBaseProviderRegistry();
    providers.register(new MinecraftItemBaseProvider());

    this.renderer = new BukkitFastItemRenderer(providers, new PaperItemPatchApplier());
    this.converter = new BukkitFastItemStackConverter();
  }

  @Override
  public FastItemBuilder builder(String baseId) {
    Objects.requireNonNull(baseId, "baseId must not be null");
    return new DefaultFastItemBuilder(FastItemStack.of(baseId));
  }

  @Override
  public FastItemBuilder builder(FastItemStack base) {
    return new DefaultFastItemBuilder(base);
  }

  @Override
  public BukkitFastItemBuilder bukkitBuilder(String baseId) {
    Objects.requireNonNull(baseId, "baseId must not be null");
    return new BukkitFastItemBuilderImpl(FastItemStack.of(baseId));
  }

  @Override
  public BukkitFastItemBuilder bukkitBuilder(FastItemStack base) {
    return new BukkitFastItemBuilderImpl(base);
  }

  @Override
  public ItemStack render(FastItemStack fast) {
    return renderer.render(fast);
  }

  @Override
  public FastItemStack fromItemStack(ItemStack stack) {
    return converter.fromItemStack(stack);
  }
}