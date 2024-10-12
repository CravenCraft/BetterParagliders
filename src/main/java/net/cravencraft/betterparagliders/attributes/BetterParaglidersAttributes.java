package net.cravencraft.betterparagliders.attributes;

import net.cravencraft.betterparagliders.BetterParaglidersMod;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static net.cravencraft.betterparagliders.BetterParaglidersMod.MOD_ID;

public class BetterParaglidersAttributes {

    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, MOD_ID);

    /**
     * Mobility Attributes
     */

    public static final RegistryObject<Attribute> SPRINTING_STAMINA_REDUCTION = ATTRIBUTES.register("sprinting_stamina_reduction", () -> new RangedAttribute("attribute.name." + BetterParaglidersMod.MOD_ID + ".sprinting_stamina_reduction", 0.0, 0.0, 100.0).setSyncable(true));
    public static final RegistryObject<Attribute> SWIMMING_STAMINA_REDUCTION = ATTRIBUTES.register("swimming_stamina_reduction", () -> new RangedAttribute("attribute.name." + BetterParaglidersMod.MOD_ID + ".swimming_stamina_reduction", 0.0, 0.0, 100.0).setSyncable(true));
    public static final RegistryObject<Attribute> IDLE_STAMINA_REGEN = ATTRIBUTES.register("idle_stamina_regen", () -> new RangedAttribute("attribute.name." + BetterParaglidersMod.MOD_ID + ".idle_stamina_regen", 0.0, 0.0, 100.0).setSyncable(true));

    public static final RegistryObject<Attribute> SUBMERGED_STAMINA_REGEN = ATTRIBUTES.register("submerged_stamina_regen", () -> new RangedAttribute("attribute.name." + BetterParaglidersMod.MOD_ID + ".submerged_stamina_regen", 0.0, 0.0, 100.0).setSyncable(true));
    public static final RegistryObject<Attribute> WATER_BREATHING_STAMINA_REGEN = ATTRIBUTES.register("water_breathing_stamina_regen", () -> new RangedAttribute("attribute.name." + BetterParaglidersMod.MOD_ID + ".water_breathing_stamina_regen", 0.0, 0.0, 100.0).setSyncable(true));

    /**
     * ParCool Support. Mobility Attributes
     */
    public static final RegistryObject<Attribute> FAST_RUNNING_STAMINA_REDUCTION = ATTRIBUTES.register("fast_running_stamina_reduction", () -> new RangedAttribute("attribute.name." + BetterParaglidersMod.MOD_ID + ".fast_running_stamina_reduction", 0.0, 0.0, 100.0).setSyncable(true));
    public static final RegistryObject<Attribute> FAST_SWIMMING_STAMINA_REDUCTION = ATTRIBUTES.register("fast_swimming_stamina_reduction", () -> new RangedAttribute("attribute.name." + BetterParaglidersMod.MOD_ID + ".fast_swimming_stamina_reduction", 0.0, 0.0, 100.0).setSyncable(true));
    public static final RegistryObject<Attribute> CLING_TO_CLIFF_STAMINA_REDUCTION = ATTRIBUTES.register("cling_to_cliff_stamina_reduction", () -> new RangedAttribute("attribute.name." + BetterParaglidersMod.MOD_ID + ".cling_to_cliff_stamina_reduction", 0.0, 0.0, 100.0).setSyncable(true));
    public static final RegistryObject<Attribute> HORIZONTAL_WALL_RUN_STAMINA_REDUCTION = ATTRIBUTES.register("horizontal_wall_run_stamina_reduction", () -> new RangedAttribute("attribute.name." + BetterParaglidersMod.MOD_ID + ".horizontal_wall_run_stamina_reduction", 0.0, 0.0, 100.0).setSyncable(true));
    public static final RegistryObject<Attribute> DODGE_STAMINA_REDUCTION = ATTRIBUTES.register("dodge_stamina_reduction", () -> new RangedAttribute("attribute.name." + BetterParaglidersMod.MOD_ID + ".dodge_stamina_reduction", 0.0, 0.0, 100.0).setSyncable(true));
    public static final RegistryObject<Attribute> ROLL_STAMINA_REDUCTION = ATTRIBUTES.register("roll_stamina_reduction", () -> new RangedAttribute("attribute.name." + BetterParaglidersMod.MOD_ID + ".roll_stamina_reduction", 0.0, 0.0, 100.0).setSyncable(true));
    public static final RegistryObject<Attribute> BREAKFALL_STAMINA_REDUCTION = ATTRIBUTES.register("breakfall_stamina_reduction", () -> new RangedAttribute("attribute.name." + BetterParaglidersMod.MOD_ID + ".breakfall_stamina_reduction", 0.0, 0.0, 100.0).setSyncable(true));
    public static final RegistryObject<Attribute> VAULT_STAMINA_REDUCTION = ATTRIBUTES.register("vault_stamina_reduction", () -> new RangedAttribute("attribute.name." + BetterParaglidersMod.MOD_ID + ".vault_stamina_reduction", 0.0, 0.0, 100.0).setSyncable(true));
    public static final RegistryObject<Attribute> CLIMB_UP_STAMINA_REDUCTION = ATTRIBUTES.register("climb_up_stamina_reduction", () -> new RangedAttribute("attribute.name." + BetterParaglidersMod.MOD_ID + ".climb_up_stamina_reduction", 0.0, 0.0, 100.0).setSyncable(true));
    public static final RegistryObject<Attribute> VERTICAL_WALL_RUN_STAMINA_REDUCTION = ATTRIBUTES.register("vertical_wall_run_stamina_reduction", () -> new RangedAttribute("attribute.name." + BetterParaglidersMod.MOD_ID + ".vertical_wall_run_stamina_reduction", 0.0, 0.0, 100.0).setSyncable(true));
    public static final RegistryObject<Attribute> CAT_LEAP_STAMINA_REDUCTION = ATTRIBUTES.register("cat_leap_stamina_reduction", () -> new RangedAttribute("attribute.name." + BetterParaglidersMod.MOD_ID + ".cat_leap_stamina_reduction", 0.0, 0.0, 100.0).setSyncable(true));
    public static final RegistryObject<Attribute> CHARGE_JUMP_STAMINA_REDUCTION = ATTRIBUTES.register("charge_jump_stamina_reduction", () -> new RangedAttribute("attribute.name." + BetterParaglidersMod.MOD_ID + ".charge_jump_stamina_reduction", 0.0, 0.0, 100.0).setSyncable(true));

    /**
     * Combat Attributes
     */
    public static final RegistryObject<Attribute> BASE_MELEE_STAMINA_REDUCTION = ATTRIBUTES.register("base_melee_stamina_reduction", () -> new RangedAttribute("attribute.name." + BetterParaglidersMod.MOD_ID + ".base_melee_stamina_reduction", 0.0, 0.0, 100.0).setSyncable(true));
    public static final RegistryObject<Attribute> TWO_HANDED_STAMINA_REDUCTION = ATTRIBUTES.register("two_handed_stamina_reduction", () -> new RangedAttribute("attribute.name." + BetterParaglidersMod.MOD_ID + ".two_handed_stamina_reduction", 0.0, 0.0, 100.0).setSyncable(true));
    public static final RegistryObject<Attribute> ONE_HANDED_STAMINA_REDUCTION = ATTRIBUTES.register("one_handed_stamina_reduction", () -> new RangedAttribute("attribute.name." + BetterParaglidersMod.MOD_ID + ".one_handed_stamina_reduction", 0.0, 0.0, 100.0).setSyncable(true));
    public static final RegistryObject<Attribute> RANGE_STAMINA_REDUCTION = ATTRIBUTES.register("range_stamina_reduction", () -> new RangedAttribute("attribute.name." + BetterParaglidersMod.MOD_ID + ".range_stamina_reduction", 0.0, 0.0, 100.0).setSyncable(true));
    public static final RegistryObject<Attribute> BLOCK_STAMINA_REDUCTION = ATTRIBUTES.register("block_stamina_reduction", () -> new RangedAttribute("attribute.name." + BetterParaglidersMod.MOD_ID + ".block_stamina_reduction", 0.0, 0.0, 100.0).setSyncable(true));

    public static void registerEventHandlers(IEventBus eventBus) {
       ATTRIBUTES.register(eventBus);
   }
}
