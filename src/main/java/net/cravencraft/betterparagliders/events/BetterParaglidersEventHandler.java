package net.cravencraft.betterparagliders.events;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tictim.paraglider.forge.capability.PlayerMovementProvider;
import tictim.paraglider.impl.movement.PlayerMovement;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = BetterParaglidersMod.MOD_ID)
public final class BetterParaglidersEventHandler {

    private BetterParaglidersEventHandler() {}

    /**
     * Reads datapacks & saves stamina overrides for items in a Map object, which will be used to override
     * the stamina cost of a weapon when the player attacks in the CalculateStaminaUtils class.
     */
    @SubscribeEvent
    public static void loadStaminaOverrides(ServerStartedEvent event) throws IOException {
        ResourceManager resourceManager = event.getServer().getResourceManager();

        for (Map.Entry<ResourceLocation, List<Resource>> resourceLocationListEntry : resourceManager.listResourceStacks("stamina_cost", (fileName) -> fileName.getPath().endsWith(".json")).entrySet()) {

            String namespace = resourceLocationListEntry.getKey().getNamespace();
            for (Resource resource : resourceLocationListEntry.getValue()) {
                JsonReader staminaReader = new JsonReader(new InputStreamReader(resource.open()));
                try {
                    JsonArray weapons = JsonParser.parseReader(staminaReader).getAsJsonArray();
                    for (JsonElement weapon : weapons) {
                        if (weapon.isJsonObject()) {
                            JsonObject weaponAttributes = weapon.getAsJsonObject();
                            if (weaponAttributes.has("stamina_cost")) {
                                String type = (weaponAttributes.has("type")) ? weaponAttributes.get("type").getAsString() : "placeholder";
                                String itemId = (weaponAttributes.has("name")) ? weaponAttributes.get("name").getAsString() : "placeholder";
                                double staminaCost = (weaponAttributes.has("stamina_cost")) ? weaponAttributes.get("stamina_cost").getAsDouble() : 0;

                                itemId = namespace.concat(".").concat(itemId);

                                CalculateStaminaUtils.addDatapackStaminaOverride(type, itemId, staminaCost);
                            }
                        }
                    }
                } catch (IllegalStateException e) {
                    BetterParaglidersMod.LOGGER.error("ERROR: " + resourceLocationListEntry.getKey() + ". The JSON object isn't properly configured");
                }

                staminaReader.close();
            }
        }
    }

    /**
     * Detects if an entity is trying to attack the player, and if the player is blocking then the stamina will be
     * drained for the amount specified via the damage and the shield the player is using.
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
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void shieldBlockProjectileEvent(ProjectileImpactEvent event) {
        if (event.getRayTraceResult() instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof ServerPlayer serverPlayer) {
            blockEventWork(serverPlayer, (float) ServerConfig.projectileStaminaConsumption());
        }
    }

    /**
     * Cancels the vanilla attack event if the player is currently out of stamina.
     */
    @SubscribeEvent
    public static void cancelVanillaAttack(AttackEntityEvent event) {
        Player player = event.getEntity();
        if (event.isCancelable() && event.getTarget() != null && basicPlayerStaminaDepletionChecks(player)) {
            event.setCanceled(true);
        }
    }

    /**
     * If the player's stamina is depleted, then cancel the bow/crossbow draw and the shield block event.
     */
    @SubscribeEvent
    public static void cancelUseItemsRequiringStamina(LivingEntityUseItemEvent event) {

        if (event.getEntity() instanceof Player player && basicPlayerStaminaDepletionChecks(player)) {
            String itemDescId = player.getUseItem().getItem().getDescriptionId().replace("item.", "");

            if (event.getItem().getItem() instanceof CrossbowItem || CalculateStaminaUtils.DATAPACK_RANGED_STAMINA_OVERRIDES.containsKey(itemDescId)) {
                // Only stop the crossbow if it isn't fully charged.
                if (!CrossbowItem.isCharged(event.getItem()) && player.isUsingItem()) {
                    player.stopUsingItem();
                }
            }
            else if ((event.getItem().getItem() instanceof BowItem || CalculateStaminaUtils.DATAPACK_RANGED_STAMINA_OVERRIDES.containsKey(itemDescId)) && player.isUsingItem()) {
                player.stopUsingItem();
            }
            else if ((event.getItem().getItem() instanceof ShieldItem || CalculateStaminaUtils.DATAPACK_RANGED_STAMINA_OVERRIDES.containsKey(itemDescId)) && player.isUsingItem()) {
                player.stopUsingItem();
            }
        }
    }

    /**
     * A simple method that eliminates redundant calls for both block event methods.
     */
    private static void blockEventWork(ServerPlayer serverPlayer, float amount) {
        if (serverPlayer.getUseItem().getItem().getDescriptionId().contains("shield")) {
            PlayerMovement playerMovement = PlayerMovementProvider.of(serverPlayer);

            if (playerMovement != null && !playerMovement.stamina().isDepleted()) {
                ((StaminaOverride) playerMovement.stamina()).calculateBlockStaminaCostServerSide(amount);
            }
        }
    }

    /**
     * Ensure that the player is not a creative or spectator.
     * Ensure that the entity has a PlayerMovement class attached to it (it is a player entity), and that its stamina is depleted.
     */
    private static boolean basicPlayerStaminaDepletionChecks(Player player) {
            if (!player.isCreative() && !player.isSpectator()) {

                // Ensure that the entity has a PlayerMovement class attached to it (it is a player entity), and that its stamina is depleted.
                PlayerMovement playerMovement = PlayerMovementProvider.of(player);
                return playerMovement != null && playerMovement.stamina().isDepleted();
            }

        return false;
    }
}