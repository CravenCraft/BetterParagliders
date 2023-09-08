package net.cravencraft.betterparagliders.config;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;

import java.util.*;

import static net.cravencraft.betterparagliders.BetterParaglidersMod.MOD_ID;

/**
 * Updated mod config will override the original Paragliders paragliding and running stamina consumption attributes
 * as they need to be in order for this mod's stamina system to properly render. Will also contain various configs
 * for modifying how much stamina is consumed for certain weapon attacks.
 */
@Mod.EventBusSubscriber(modid = MOD_ID, bus = Bus.MOD)
public final class UpdatedModCfg {
	private UpdatedModCfg(){}

	private static ForgeConfigSpec.DoubleValue MELEE_STAMINA_CONSUMPTION;
	private static ForgeConfigSpec.DoubleValue TWO_HANDED_STAMINA_CONSUMPTION;
	private static ForgeConfigSpec.DoubleValue ONE_HANDED_STAMINA_CONSUMPTION;
	private static ForgeConfigSpec.DoubleValue RANGE_STAMINA_CONSUMPTION;
	private static ForgeConfigSpec.DoubleValue BLOCK_STAMINA_CONSUMPTION;
	private static ForgeConfigSpec.DoubleValue ROLL_STAMINA_CONSUMPTION;
	private static ForgeConfigSpec.ConfigValue<List<? extends Integer>> DEPLETION_EFFECT_LIST;
	private static ForgeConfigSpec.ConfigValue<List<? extends Integer>> DEPLETION_EFFECT_STRENGTH_LIST;

	public static double meleeStaminaConsumption() {
		return MELEE_STAMINA_CONSUMPTION.get();
	}
	public static double twoHandedStaminaConsumption() { return TWO_HANDED_STAMINA_CONSUMPTION.get(); }
	public static double oneHandedStaminaConsumption() { return ONE_HANDED_STAMINA_CONSUMPTION.get(); }
	public static double rangeStaminaConsumption() { return RANGE_STAMINA_CONSUMPTION.get(); }
	public static double blockStaminaConsumption() {
		return BLOCK_STAMINA_CONSUMPTION.get();
	}
	public static double rollStaminaConsumption() {
		return ROLL_STAMINA_CONSUMPTION.get();
	}
	public static List<Integer> depletionEffectList() { return (List<Integer>) DEPLETION_EFFECT_LIST.get(); }
	public static List<Integer> depletionEffectStrengthList() { return (List<Integer>) DEPLETION_EFFECT_STRENGTH_LIST.get(); }


	public static void init() {
		Builder server = new Builder();
		server.push("Stamina");
		MELEE_STAMINA_CONSUMPTION = server.comment("How much more/less stamina is consumed from melee attacks.")
				.defineInRange("meleeStaminaConsumption", 1.0, 0.0, 2.0);
		TWO_HANDED_STAMINA_CONSUMPTION = server.comment("How much more/less stamina is consumed from two handed attacks (stacks with MELEE_STAMINA_CONSUMPTION).")
				.defineInRange("twoHandedStaminaConsumption", 1.0, 0.0, 2.0);
		ONE_HANDED_STAMINA_CONSUMPTION = server.comment("How much more/less stamina is consumed from one handed (and dual wielding) attacks (stacks with MELEE_STAMINA_CONSUMPTION).")
				.defineInRange("oneHandedStaminaConsumption", 1.0, 0.0, 2.0);
		RANGE_STAMINA_CONSUMPTION = server.comment("The base amount of stamina a range attack will cost.")
				.defineInRange("rangeStaminaConsumption", 1.0, 0.0, 2.0);
		BLOCK_STAMINA_CONSUMPTION = server.comment("The base amount of stamina a range attack will cost.")
				.defineInRange("blockStaminaConsumption", 1.0, 0.0, 2.0);
		ROLL_STAMINA_CONSUMPTION = server.comment("The base amount of stamina a range attack will cost.")
				.defineInRange("rollStaminaConsumption", 1.0, 0.0, 2.0);
		DEPLETION_EFFECT_LIST = server
				.comment("The effect ID that will be applied when a player runs out of stamina (default is Mining Fatigue and Weakness respectively).")
				.comment("Refer to https://minecraft.fandom.com/wiki/Effect#Effect_list for a list of the effects and their corresponding IDs")
				.defineListAllowEmpty(Collections.singletonList("effects"), () -> ImmutableList.of(4, 18), o -> true);
		DEPLETION_EFFECT_STRENGTH_LIST = server
				.comment("The strength applied to the depletion effect above (The default is 2 and 7. A value such as 4 would apply Weakness IV).")
				.comment("If no value is set here, and an extra effect is added above, then the effect strength will default to 1.")
				.defineListAllowEmpty(Collections.singletonList("effects_strength"), () -> ImmutableList.of(5, 1), o -> true);

		server.pop();
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, server.build());
	}
}
