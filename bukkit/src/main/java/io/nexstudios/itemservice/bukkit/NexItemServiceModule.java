package io.nexstudios.itemservice.bukkit;

import io.nexstudios.itemservice.bukkit.service.item.NexItemService;
import io.nexstudios.itemservice.bukkit.service.item.NexItemServiceImpl;
import io.nexstudios.serviceregistry.di.ServiceAccessor;
import io.nexstudios.serviceregistry.di.ServiceModule;
import org.bukkit.plugin.Plugin;

/**
 * Service module entry point. This is installed via ServiceAccessor#install(...).
 */
public final class NexItemServiceModule implements ServiceModule {

  private final Plugin plugin;

  public NexItemServiceModule(Plugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public void install(ServiceAccessor accessor) {
    accessor.register(NexItemService.class, new NexItemServiceImpl(plugin));
  }
}