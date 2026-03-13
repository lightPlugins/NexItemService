package io.nexstudios.itemservice.bukkit.provider;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class ItemBaseProviderRegistry {

  private final Map<String, ItemBaseProvider> providers = new ConcurrentHashMap<>();

  public void register(ItemBaseProvider provider) {
    Objects.requireNonNull(provider, "provider must not be null");
    providers.put(provider.namespace(), provider);
  }

  public Optional<ItemBaseProvider> find(String namespace) {
    ItemBaseProvider provider = providers.get(namespace);
    if (provider == null) return Optional.empty();
    if (!provider.isAvailable()) return Optional.empty();
    return Optional.of(provider);
  }
}