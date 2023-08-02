package net.cravencraft.betterparagliders.events;

import net.cravencraft.betterparagliders.BetterParaglidersMod;
import net.cravencraft.betterparagliders.capabilities.PlayerMovementInterface;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tictim.paraglider.capabilities.PlayerMovement;

@Mod.EventBusSubscriber(modid = BetterParaglidersMod.MOD_ID)
public final class BetterParaglidersEventHandler {

    private BetterParaglidersEventHandler() {}

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
