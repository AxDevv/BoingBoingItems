package com.boingboing;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class BoingBoingItemFabric implements ModInitializer {

    private final ItemBounceHandler bounceHandler = new ItemBounceHandler();

    @Override
    public void onInitialize() {
        BounceConfigFabric.load();

        ServerTickEvents.END_WORLD_TICK.register(world -> {
            bounceHandler.onWorldTick(world);
        });
    }
}
