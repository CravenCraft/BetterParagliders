package net.cravencraft.betterparagliders.events;

import net.cravencraft.betterparagliders.BetterParaglidersMod;
import net.cravencraft.betterparagliders.capabilities.PlayerMovementInterface;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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
            ((PlayerMovementInterface) PlayerMovement.of(player)).calculateBlockStaminaCostServerSide(event.getBlockedDamage());
        }
    }

    /**
     * If the player's stamina is depleted, then cancel the bow/crossbow draw event.
     *
     * @param event
     */
    @SubscribeEvent
    public static void cancelBowDraw(PlayerInteractEvent event) {
        if (event.getItemStack().getItem() instanceof  ProjectileWeaponItem && PlayerMovement.of(event.getEntity()).isDepleted() && !event.getEntity().isCreative()) {
            if (CrossbowItem.isCharged(event.getItemStack())) {
                event.setCanceled(false);
            }
            else {
                event.setCanceled(true);
            }
        }
    }
}