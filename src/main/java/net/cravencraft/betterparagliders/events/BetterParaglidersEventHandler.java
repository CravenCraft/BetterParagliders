package net.cravencraft.betterparagliders.events;

import net.cravencraft.betterparagliders.BetterParaglidersMod;
import net.cravencraft.betterparagliders.attributes.BetterParaglidersAttributes;
import net.cravencraft.betterparagliders.config.ConfigManager;
import net.cravencraft.betterparagliders.network.ModNet;
import net.cravencraft.betterparagliders.network.SyncActionToClientMsg;
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
            int blockCost = Math.round((float)((event.getBlockedDamage() * ConfigManager.SERVER_CONFIG.blockStaminaConsumption() + 10) - player.getAttributeValue(BetterParaglidersAttributes.BLOCK_STAMINA_REDUCTION.get())));

            SyncActionToClientMsg msg = new SyncActionToClientMsg(blockCost, true);
            if(ModCfg.traceMovementPacket()) ParagliderMod.LOGGER.debug("Sending packet {} to player {}", msg, player);
            ModNet.NET.send(PacketDistributor.PLAYER.with(() -> player), msg);
        }
    }
}
