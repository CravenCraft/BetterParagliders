package net.cravencraft.betterparagliders.events;

import net.cravencraft.betterparagliders.BetterParaglidersMod;
import net.cravencraft.betterparagliders.network.ModNet;
import net.cravencraft.betterparagliders.network.SyncActionToClientMsg;
import net.cravencraft.betterparagliders.utils.CalculateStaminaUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import tictim.paraglider.ModCfg;
import tictim.paraglider.ParagliderMod;

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
            BetterParaglidersMod.LOGGER.info("DAMAGE SOURCE IS PROJECTILE: " + event.getDamageSource().isProjectile());
            int blockCost = CalculateStaminaUtils.calculateBlockStaminaCost(player, event.getBlockedDamage());
            BetterParaglidersMod.LOGGER.info("ORIGINAL BLOCKED DAMAGE: " + blockCost);

            SyncActionToClientMsg msg = new SyncActionToClientMsg(blockCost, true);
            if(ModCfg.traceMovementPacket()) ParagliderMod.LOGGER.debug("Sending packet {} to player {}", msg, player);
            ModNet.NET.send(PacketDistributor.PLAYER.with(() -> player), msg);
        }
    }
}
