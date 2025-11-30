package com.boingboing;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod(BounceConstants.MOD_ID)
public class BoingBoingItem {

    public BoingBoingItem() {
        BounceConfig.register();
        MinecraftForge.EVENT_BUS.register(new ItemBounceHandler());
    }
}
