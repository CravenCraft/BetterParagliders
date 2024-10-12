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
