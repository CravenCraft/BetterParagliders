package net.cravencraft.betterparagliders.attributes;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static net.cravencraft.betterparagliders.BetterParaglidersMod.MOD_ID;

public class BetterParaglidersAttributes {

    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, MOD_ID);

    public static final RegistryObject<Attribute> STRENGTH_PENALTY = ATTRIBUTES.register("strength_penalty", () -> new RangedAttribute("strength_penalty", 0.0, 0.0, 2.0).setSyncable(true));

    //TODO: I'm worried this will set the strength value for ALL players in a given server if just ONE dies.
    //      I NEED to test this.
    public static double currentStrengthPenalty;

   public static void registerEventHandlers(IEventBus eventBus) {
       ATTRIBUTES.register(eventBus);
   }

   public static void setAttribute(AttributeInstance attribute, double value) {
        //TODO: Just for testing. Will remove the hard-coded str value after
       currentStrengthPenalty = value;
       attribute.setBaseValue(value);
   }
}
