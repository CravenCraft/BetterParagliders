package net.cravencraft.betterparagliders.config;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

import java.util.Collections;
import java.util.List;

public class ServerConfig {
    private static ForgeConfigSpec.DoubleValue MELEE_STAMINA_CONSUMPTION;
    private static ForgeConfigSpec.DoubleValue TWO_HANDED_STAMINA_CONSUMPTION;
    private static ForgeConfigSpec.DoubleValue ONE_HANDED_STAMINA_CONSUMPTION;
    private static ForgeConfigSpec.DoubleValue RANGE_STAMINA_CONSUMPTION;
    private static ForgeConfigSpec.DoubleValue BLOCK_STAMINA_CONSUMPTION;
    private static ForgeConfigSpec.DoubleValue PROJECTILE_STAMINA_CONSUMPTION;

    public static double meleeStaminaConsumption() {
        return MELEE_STAMINA_CONSUMPTION.get();
    }
    public static double twoHandedStaminaConsumption() { return TWO_HANDED_STAMINA_CONSUMPTION.get(); }
    public static double oneHandedStaminaConsumption() { return ONE_HANDED_STAMINA_CONSUMPTION.get(); }
    public static double rangeStaminaConsumption() { return RANGE_STAMINA_CONSUMPTION.get(); }
    public static double blockStaminaConsumption() { return BLOCK_STAMINA_CONSUMPTION.get(); }
    public static double projectileStaminaConsumption() { return PROJECTILE_STAMINA_CONSUMPTION.get(); }

    public ServerConfig(ForgeConfigSpec.Builder server) {
        server.push("stamina");
        MELEE_STAMINA_CONSUMPTION = server.comment("How much more/less stamina is consumed from melee attacks.")
                .defineInRange("melee_stamina_consumption", 1.0, 0.0, 10.0);
        TWO_HANDED_STAMINA_CONSUMPTION = server.comment("How much more/less stamina is consumed from two handed attacks (stacks with MELEE_STAMINA_CONSUMPTION).")
                .defineInRange("two_handed_stamina_consumption", 2.0, 0.0, 10.0);
        ONE_HANDED_STAMINA_CONSUMPTION = server.comment("How much more/less stamina is consumed from one handed (and dual wielding) attacks (stacks with MELEE_STAMINA_CONSUMPTION).")
                .defineInRange("one_handed_stamina_consumption", 2.5, 0.0, 10.0);
        RANGE_STAMINA_CONSUMPTION = server.comment("The base amount of stamina a range attack will cost.")
                .defineInRange("ranged_stamina_consumption", 1.0, 0.0, 10.0);
        BLOCK_STAMINA_CONSUMPTION = server.comment("The base amount of stamina blocking with a shield will cost.")
                .defineInRange("block_stamina_consumption", 10, 0.0, 100.0);
        PROJECTILE_STAMINA_CONSUMPTION = server.comment("The base amount of stamina blocking a projectile will cost (in addition to blocking cost).")
                .defineInRange("projectile_stamina_consumption", 5, 0.0, 100.0);
        server.pop(); // Pop stamina

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, server.build());
    }
}