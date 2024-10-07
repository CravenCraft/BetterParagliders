package net.cravencraft.betterparagliders.events;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import net.cravencraft.betterparagliders.BetterParaglidersMod;
import net.cravencraft.betterparagliders.capabilities.StaminaOverride;
import net.cravencraft.betterparagliders.config.ServerConfig;
import net.cravencraft.betterparagliders.utils.CalculateStaminaUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tictim.paraglider.forge.capability.PlayerMovementProvider;
import tictim.paraglider.impl.movement.PlayerMovement;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = BetterParaglidersMod.MOD_ID)
public final class BetterParaglidersEventHandler {

    private BetterParaglidersEventHandler() {}

    /**
     * Reads datapacks & saves stamina overrides for items in a Map object, which will be used to override
     * the stamina cost of a weapon when the player attacks in the CalculateStaminaUtils class.
     *
     * @param event
     * @throws IOException
     */
    @SubscribeEvent
    public static void loadStaminaOverrides(ServerStartedEvent event) throws IOException {
        ResourceManager resourceManager = event.getServer().getResourceManager();
        Iterator var3 = resourceManager.listResourceStacks("stamina_cost", (fileName) -> fileName.getPath().endsWith(".json")).entrySet().iterator();

        while (var3.hasNext()) {
            Map.Entry<ResourceLocation, List<Resource>> entry = (Map.Entry)var3.next();
            ResourceLocation identifier = entry.getKey();
            for (Resource resource : entry.getValue()) {
                JsonReader staminaReader = new JsonReader(new InputStreamReader(resource.open()));
                try {
                    JsonObject weaponAttributes = JsonParser.parseReader(staminaReader).getAsJsonObject();

                    if (weaponAttributes.has("stamina_cost")) {
                        String type = (weaponAttributes.has("type")) ? weaponAttributes.get("type").getAsString() : "placeholder";
                        double staminaCost = weaponAttributes.get("stamina_cost").getAsDouble();
                        String itemId = identifier.toString()
                                .replace("stamina_cost/", "")
                                .replace(":", ".")
                                .replace(".json", "");

                        CalculateStaminaUtils.addDatapackStaminaOverride(type, itemId, staminaCost);
                    }
                }
                catch (IllegalStateException e) {
                    BetterParaglidersMod.LOGGER.error("ERROR: " + entry.getKey() + ". The JSON object isn't properly configured");
                }

                staminaReader.close();
            }
        }
    }

    /**
     * Detects if an entity is trying to attack the player, and if the player is blocking then the stamina will be
     * drained for the amount specified via the damage and the shield the player is using.
     *
     * @param event
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void shieldBlockEvent(LivingAttackEvent event) {

        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            blockEventWork(serverPlayer, event.getAmount());
        }
    }

    /**
     * Detects if the player blocks a projectile with a shield. If so, then stamina will be drained for the amount
     * based on the configured base projectile stamina drain and the shield being used.
     *
     * @param event
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void shieldBlockProjectileEvent(ProjectileImpactEvent event) {


        if (event.getRayTraceResult() instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof ServerPlayer serverPlayer) {
            blockEventWork(serverPlayer, (float) ServerConfig.projectileStaminaConsumption());
        }
    }

    /**
     * If the player's stamina is depleted, then cancel the bow/crossbow draw event.
     *
     * @param event
     */
    @SubscribeEvent
    public static void cancelBowDraw(PlayerInteractEvent event) {
        if ((event.getItemStack().getItem() instanceof  ProjectileWeaponItem
                || CalculateStaminaUtils.DATAPACK_RANGED_STAMINA_OVERRIDES.containsKey(event.getEntity().getUseItem().getItem().getDescriptionId().replace("item.", "")))
                && PlayerMovementProvider.of(event.getEntity()).stamina().isDepleted()
                && !event.getEntity().isCreative())
        {

            if (CrossbowItem.isCharged(event.getItemStack())) {
                event.setCanceled(false);
            }
            else {
                event.setCanceled(true);
            }
        }
    }

    /**
     * A simple method that eliminates redundant calls for both block event methods.
     *
     * @param serverPlayer
     * @param amount
     */
    private static void blockEventWork(ServerPlayer serverPlayer, float amount) {
        if (serverPlayer.getUseItem().getItem().getDescriptionId().contains("shield")) {
            PlayerMovement playerMovement = PlayerMovementProvider.of(serverPlayer);

            if (playerMovement != null && !playerMovement.stamina().isDepleted()) {
                ((StaminaOverride) playerMovement.stamina()).calculateBlockStaminaCostServerSide(amount);
            }
        }
    }
}