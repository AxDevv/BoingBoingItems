package com.boingboing;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;

@Mod(BounceConstants.MOD_ID)
public class BoingBoingItem {

    public BoingBoingItem(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, BounceConfig.SPEC);
        NeoForge.EVENT_BUS.register(new ItemBounceHandler());
    }
}
