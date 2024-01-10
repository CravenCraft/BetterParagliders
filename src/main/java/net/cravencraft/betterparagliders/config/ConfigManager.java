package net.cravencraft.betterparagliders.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigManager {
    public static ClientConfig CLIENT_CONFIG;
    public static ServerConfig SERVER_CONFIG;

    public static void registerConfigs() {
        ForgeConfigSpec.Builder client = new ForgeConfigSpec.Builder();
        ForgeConfigSpec.Builder server = new ForgeConfigSpec.Builder();

        CLIENT_CONFIG = new ClientConfig(client);
        SERVER_CONFIG = new ServerConfig(server);
    }
}