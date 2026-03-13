package io.nexstudios.itemservice.bukkit.service;

import io.nexstudios.itemservice.bukkit.api.BukkitItemService;
import io.nexstudios.itemservice.common.api.ItemService;
import io.nexstudios.serviceregistry.di.ServiceAccessor;
import io.nexstudios.serviceregistry.di.ServiceModule;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

public final class BukkitItemServiceModule implements ServiceModule {

  private final Plugin plugin;

  public BukkitItemServiceModule(Plugin plugin) {
    this.plugin = Objects.requireNonNull(plugin, "plugin must not be null");
  }

  @Override
  public void install(ServiceAccessor services) {
    Objects.requireNonNull(services, "services must not be null");

    BukkitItemServiceImpl service = new BukkitItemServiceImpl();

    services.register(ItemService.class, service);
    services.register(BukkitItemService.class, service);

    Objects.requireNonNull(plugin, "plugin must not be null");
  }
}