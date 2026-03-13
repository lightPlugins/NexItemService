package io.nexstudios.itemservice.bukkit.service.item;

import io.nexstudios.itemservice.bukkit.builder.NexItemBuilder;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

/**
 * Default implementation.
 */
public final class DefaultItemService implements ItemService {

  private final Plugin plugin;

  public DefaultItemService(Plugin plugin) {
    this.plugin = Objects.requireNonNull(plugin, "plugin must not be null");
  }

  @Override
  public Plugin plugin() {
    return plugin;
  }

  @Override
  public NexItemBuilder builder(Material material) {
    return new NexItemBuilder(plugin, material);
  }
}