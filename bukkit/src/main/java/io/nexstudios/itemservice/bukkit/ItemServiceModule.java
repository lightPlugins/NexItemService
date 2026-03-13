package io.nexstudios.itemservice.bukkit;

import io.nexstudios.itemservice.bukkit.service.item.ItemService;
import io.nexstudios.itemservice.bukkit.service.item.DefaultItemService;
import io.nexstudios.serviceregistry.di.ServiceAccessor;
import io.nexstudios.serviceregistry.di.ServiceModule;
import org.bukkit.plugin.Plugin;

/**
 * Service module entry point. This is installed via ServiceAccessor#install(...).
 */
public final class ItemServiceModule implements ServiceModule {

  private final Plugin plugin;

  public ItemServiceModule(Plugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public void install(ServiceAccessor accessor) {
    accessor.register(ItemService.class, new DefaultItemService(plugin));
  }
}