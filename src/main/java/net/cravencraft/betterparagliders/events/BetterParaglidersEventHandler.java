package net.cravencraft.betterparagliders.events;

import net.cravencraft.betterparagliders.BetterParaglidersMod;
import net.cravencraft.betterparagliders.attributes.BetterParaglidersAttributes;
import net.cravencraft.betterparagliders.capabilities.PlayerMovementInterface;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tictim.paraglider.capabilities.PlayerMovement;

@Mod.EventBusSubscriber(modid = BetterParaglidersMod.MOD_ID)
public final class BetterParaglidersEventHandler {

    private BetterParaglidersEventHandler() {}

    @SubscribeEvent
    public static void onPlayerDeathEvent(LivingDeathEvent deathEvent) {
        LivingEntity entity = deathEvent.getEntity();
        //TODO: Probably don't need the null check since this is fired only IF an entity dies.
        if (entity != null && entity instanceof ServerPlayer serverPlayer) {
            AttributeInstance attributeInstance = serverPlayer.getAttribute(BetterParaglidersAttributes.STRENGTH_PENALTY.get());
            BetterParaglidersMod.LOGGER.info("ATTRIBUTE VALUE: " + attributeInstance.getValue());
            BetterParaglidersAttributes.currentStrengthPenalty = attributeInstance.getValue();

        }
    }

    /**
     * Registers if the player blocks any attack from an entity.
     *
     * @param event
     */
    @SubscribeEvent
    public static void ShieldBlockEvent(ShieldBlockEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ((PlayerMovementInterface) PlayerMovement.of(player)).calculateBlockStaminaCost(event.getBlockedDamage());
            BetterParaglidersMod.LOGGER.info("BLOCKED DAMAGE: " + event.getBlockedDamage());
        }
    }
}
