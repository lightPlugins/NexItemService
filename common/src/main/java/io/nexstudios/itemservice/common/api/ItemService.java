package io.nexstudios.itemservice.common.api;

import io.nexstudios.itemservice.common.item.FastItemStack;
import io.nexstudios.serviceregistry.di.Service;

public interface ItemService extends Service {

  FastItemBuilder builder(String baseId);

  FastItemBuilder builder(FastItemStack base);
}