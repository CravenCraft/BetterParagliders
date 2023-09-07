package net.cravencraft.betterparagliders.events;

import net.cravencraft.betterparagliders.BetterParaglidersMod;
import net.cravencraft.betterparagliders.network.ModNet;
import net.cravencraft.betterparagliders.network.SyncActionToClientMsg;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
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
            SyncActionToClientMsg msg = new SyncActionToClientMsg((int) event.getBlockedDamage(), true);
            if(ModCfg.traceMovementPacket()) ParagliderMod.LOGGER.debug("Sending packet {} to player {}", msg, player);
            ModNet.NET.send(PacketDistributor.PLAYER.with(() -> player), msg);
        }
    }

//    @SubscribeEvent
//    public static void invulnerableRoll(LivingDamageEvent event) {
//        if (event.getEntity() instanceof Player player) {
//            BetterParaglidersMod.LOGGER.info("IN DAMAGE EVENT");
////            event.
////            event.setCanceled(true);
//            if (player instanceof ServerPlayer){
//                BetterParaglidersMod.LOGGER.info("SERVER PLAYER");
//            }
//            else if (player instanceof LocalPlayer) {
//                BetterParaglidersMod.LOGGER.info("LOCAL PLAYER");
//            }
//        }
//    }
}
