package net.cravencraft.betterparagliders.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;

import static net.cravencraft.betterparagliders.BetterParaglidersMod.MOD_ID;

/**
 * Updated mod config will override the original Paragliders paragliding and running stamina consumption attributes
 * as they need to be in order for this mod's stamina system to properly render. Will also contain various configs
 * for modifying how much stamina is consumed for certain weapon attacks.
 */
@Mod.EventBusSubscriber(modid = MOD_ID, bus = Bus.MOD)
public final class UpdatedModCfg {
	private UpdatedModCfg(){}
	private static ForgeConfigSpec.BooleanValue PARAGLIDING_CONSUMES_STAMINA;
	private static ForgeConfigSpec.BooleanValue RUNNING_CONSUMES_STAMINA;
	private static ForgeConfigSpec.IntValue BASE_MELEE_STAMINA_COST;
	private static ForgeConfigSpec.IntValue BASE_RANGE_STAMINA_COST;
	private static ForgeConfigSpec.DoubleValue TIER_PENALTY;
	private static ForgeConfigSpec.DoubleValue TWO_HANDED_PENALTY;


	public static boolean paraglidingConsumesStamina() {
		return PARAGLIDING_CONSUMES_STAMINA.get();}
	public static boolean runningConsumesStamina(){
		return RUNNING_CONSUMES_STAMINA.get();
	}

	public static int baseMeleeStaminaCost() { return BASE_MELEE_STAMINA_COST.get(); }

	public static int baseRangeStaminaCost() { return BASE_RANGE_STAMINA_COST.get(); }
	public static double tierPenalty() { return TIER_PENALTY.get(); }
	public static double twoHandedPenalty() { return TWO_HANDED_PENALTY.get(); }


	public static void init(){
		Builder server = new Builder();
		server.push("stamina");
		PARAGLIDING_CONSUMES_STAMINA = server.comment("Paragliding and ascending will consume stamina.").define("paraglidingConsumesStamina", true);
		RUNNING_CONSUMES_STAMINA = server.comment("Actions other than paragliding or ascending will consume stamina.").define("runningAndSwimmingConsumesStamina", true);
		BASE_MELEE_STAMINA_COST = server.comment("The base amount of stamina a melee attack will cost (1 - 100).").defineInRange("baseMeleeStaminaCost", 1, 0, 100);
		BASE_RANGE_STAMINA_COST = server.comment("The base amount of stamina a range attack will cost (1 - 100).").defineInRange("baseRangeStaminaCost", 5, 0, 100);
		TIER_PENALTY = server.comment("How much of a percentage penalty using a two handed weapon will cost (1.0 - 10.0).").defineInRange("tierPenalty", 2.0, 1.0, 10.0);
		TWO_HANDED_PENALTY = server.comment("How much of a percentage penalty using a two handed weapon will cost (1.0 - 10.0).").defineInRange("twoHandedPenalty", 1.15, 1.0, 10.0);

		//TODO: Maybe add the updated state to include config options for your mod
//		server.push("consumptions");
//		for(PlayerState state : PlayerState.values()){
//			state.setConfig(server.defineInRange(state.id+"StaminaConsumption", state.defaultChange, Integer.MIN_VALUE, Integer.MAX_VALUE));
//		}
//		server.pop();
		server.pop();
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, server.build());
	}
}
