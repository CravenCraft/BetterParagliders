package net.cravencraft.betterparagliders.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;


public class ClientConfig {

    public ClientConfig(ForgeConfigSpec.Builder client) {
        client.push("gui");

        client.pop();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, client.build());
    }
}
