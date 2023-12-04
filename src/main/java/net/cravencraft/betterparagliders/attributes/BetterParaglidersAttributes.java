package net.cravencraft.betterparagliders.attributes;

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


    /**
     * Combat Attributes
     */
    public static final RegistryObject<Attribute> BASE_MELEE_STAMINA_REDUCTION = ATTRIBUTES.register("melee_factor", () -> new RangedAttribute("melee_factor", 1.0, 0.0, 10.0).setSyncable(true));
    public static final RegistryObject<Attribute> TWO_HANDED_STAMINA_REDUCTION = ATTRIBUTES.register("two_handed_factor", () -> new RangedAttribute("two_handed_factor", 1.0, 0.0, 10.0).setSyncable(true));
    public static final RegistryObject<Attribute> ONE_HANDED_STAMINA_REDUCTION = ATTRIBUTES.register("one_handed_factor", () -> new RangedAttribute("one_handed_factor", 1.0, 0.0, 10.0).setSyncable(true));
    public static final RegistryObject<Attribute> RANGE_STAMINA_REDUCTION = ATTRIBUTES.register("range_factor", () -> new RangedAttribute("range_factor", 1.0, 0.0, 10.0).setSyncable(true));
    public static final RegistryObject<Attribute> BLOCK_STAMINA_REDUCTION = ATTRIBUTES.register("block_factor", () -> new RangedAttribute("block_factor", 1.0, 0.0, 10.0).setSyncable(true));
    public static final RegistryObject<Attribute> ROLL_STAMINA_REDUCTION = ATTRIBUTES.register("roll_factor", () -> new RangedAttribute("roll_factor", 1.0, 0.0, 10.0).setSyncable(true));

    public static void registerEventHandlers(IEventBus eventBus) {
       ATTRIBUTES.register(eventBus);
   }
}
