package net.cravencraft.betterparagliders.utils;

import com.google.common.collect.Multimap;
import net.bettercombat.api.AttackHand;
import net.bettercombat.logic.PlayerAttackHelper;
import net.cravencraft.betterparagliders.BetterParaglidersMod;
import net.cravencraft.betterparagliders.attributes.BetterParaglidersAttributes;
import net.cravencraft.betterparagliders.config.ServerConfig;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import tictim.paraglider.capabilities.PlayerMovement;
import tictim.paraglider.capabilities.PlayerState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalculateStaminaUtils {

    public static Map<String, Double> DATAPACK_MELEE_STAMINA_OVERRIDES = new HashMap<>();
    public static Map<String, Double> DATAPACK_RANGED_STAMINA_OVERRIDES = new HashMap<>();
    public static Map<String, Double> DATAPACK_SHIELD_STAMINA_OVERRIDES = new HashMap<>();

    private static final int BASE_RANGE_STAMINA_COST = 10;
    private static final int BASE_BLOCK_STAMINA_COST = 10;

    /**
     * Populates a hashmap that will contain overrides for ranged weapons, melee weapons, and shields.
     */
    public static void addDatapackStaminaOverride(String type, String itemStack, double staminaCost) {

        switch (type) {
            case "shield" -> DATAPACK_SHIELD_STAMINA_OVERRIDES.put(itemStack, staminaCost);
            case "ranged_weapon" -> DATAPACK_RANGED_STAMINA_OVERRIDES.put(itemStack, staminaCost);
            case "melee_weapon" -> DATAPACK_MELEE_STAMINA_OVERRIDES.put(itemStack, staminaCost);
        }
    }

    /**
     * TODO: Integrate support for ranged weapons as well.
     * TODO: Test on a server.
     *
     * Drains stamina based on the player's weapon. It's damage, tier, and reach.
     * As well, attributes and the config can determine how much stamina is drained.
     *
     * @param player
     * @param currentCombo
     * @return
     */
    public static int calculateMeleeStaminaCost(Player player, int currentCombo) {
        double totalStaminaConsumption;
        AttackHand attackHand = PlayerAttackHelper.getCurrentAttack(player, currentCombo);
        boolean isTwoHanded = attackHand.attributes().isTwoHanded();
        String attackingItemId = attackHand.itemStack().getItem().getDescriptionId().replace("item.", "");

        if (DATAPACK_MELEE_STAMINA_OVERRIDES.containsKey(attackingItemId)) {
            totalStaminaConsumption = DATAPACK_MELEE_STAMINA_OVERRIDES.get(attackingItemId).intValue() * ServerConfig.meleeStaminaConsumption();

            if (isTwoHanded) {
                totalStaminaConsumption -= player.getAttributeValue(BetterParaglidersAttributes.TWO_HANDED_STAMINA_REDUCTION.get());
            }
            else {
                totalStaminaConsumption -= player.getAttributeValue(BetterParaglidersAttributes.ONE_HANDED_STAMINA_REDUCTION.get());
            }
        }
        else {
            double weaponAttackDamage = 0;
            double reachFactor = attackHand.attributes().attackRange();

            Multimap<Attribute, AttributeModifier> itemStackAttributes = attackHand.itemStack().getAttributeModifiers(EquipmentSlot.MAINHAND);
            try {
                for (AttributeModifier attributeModifier : itemStackAttributes.get(Attributes.ATTACK_DAMAGE)) {
                    weaponAttackDamage += attributeModifier.getAmount();
                }
            }
            catch (NullPointerException e) {
                BetterParaglidersMod.LOGGER.error("Error: {} in retrieving attack damage attributes.", e.getMessage());
            }

            totalStaminaConsumption = (weaponAttackDamage + reachFactor) * ServerConfig.meleeStaminaConsumption();
        }

        if (isTwoHanded) {
            totalStaminaConsumption = (totalStaminaConsumption * ServerConfig.twoHandedStaminaConsumption()) - player.getAttributeValue(BetterParaglidersAttributes.TWO_HANDED_STAMINA_REDUCTION.get());
        }
        else {
            totalStaminaConsumption = (totalStaminaConsumption * ServerConfig.oneHandedStaminaConsumption()) - player.getAttributeValue(BetterParaglidersAttributes.ONE_HANDED_STAMINA_REDUCTION.get());
        }

        totalStaminaConsumption -= player.getAttributeValue(BetterParaglidersAttributes.BASE_MELEE_STAMINA_REDUCTION.get());

        return (int) Math.ceil(totalStaminaConsumption);
    }

    /**
     * Will drain stamina based on the amount either the default configured amount for the bow being used,
     * or based on a datapack value overriding that amount.
     */
    public static int calculateRangeStaminaCost(Player player) {
        double totalStaminaConsumption = ServerConfig.rangeStaminaConsumption();
        String bowItem = player.getUseItem().getItem().getDescriptionId().replace("item.", "");

        if (DATAPACK_RANGED_STAMINA_OVERRIDES.containsKey(bowItem)) {
            totalStaminaConsumption += DATAPACK_RANGED_STAMINA_OVERRIDES.get(bowItem).intValue();
        }

        return (int) (totalStaminaConsumption - player.getAttributeValue(BetterParaglidersAttributes.RANGE_STAMINA_REDUCTION.get()));
    }

    /**
     * Will drain stamina based on the amount either the default configured amount for the shield being used,
     * or based on a datapack value overriding that amount.
     */
    public static int calculateBlockStaminaCost(Player player, float blockedDamage) {
        int totalStaminaConsumption = (int) (ServerConfig.blockStaminaConsumption() + blockedDamage);
        String shieldItem = player.getUseItem().getItem().getDescriptionId().replace("item.", "");

        if (DATAPACK_SHIELD_STAMINA_OVERRIDES.containsKey(shieldItem)) {
            totalStaminaConsumption += DATAPACK_SHIELD_STAMINA_OVERRIDES.get(shieldItem).intValue();
        }

        return Math.round((float)(totalStaminaConsumption - player.getAttributeValue(BetterParaglidersAttributes.BLOCK_STAMINA_REDUCTION.get())));
    }

    /**
     * Modifies the stamina drain for the current states below based on the attribute values
     * for the given player.
     */
    public static int getModifiedStateChange(Player player, PlayerState playerState) {
        int originalStaminaDelta = playerState.change();
        String playerStateId = playerState.id;

        int modifiedStaminaDelta = (int) switch(playerStateId) {
            case "idle" -> originalStaminaDelta + player.getAttributeValue(BetterParaglidersAttributes.IDLE_STAMINA_REGEN.get());
            case "running" -> originalStaminaDelta + player.getAttributeValue(BetterParaglidersAttributes.SPRINTING_STAMINA_REDUCTION.get());
            case "swimming" -> originalStaminaDelta + player.getAttributeValue(BetterParaglidersAttributes.SWIMMING_STAMINA_REDUCTION.get());
            case "underwater" -> originalStaminaDelta + player.getAttributeValue(BetterParaglidersAttributes.SUBMERGED_STAMINA_REGEN.get());
            case "breathing_underwater" -> originalStaminaDelta + player.getAttributeValue(BetterParaglidersAttributes.WATER_BREATHING_STAMINA_REGEN.get());
            default -> originalStaminaDelta;
        };

        // Ensure that attributes can never make a player state that isn't supposed to give stamina give it.
        if (originalStaminaDelta >= 0) {
            return Math.max(modifiedStaminaDelta, originalStaminaDelta);
        }
        else {
            return Math.min(0, modifiedStaminaDelta);
        }
    }

    /**
     * Ensure that the player is not a creative or spectator.
     * Ensure that the entity has a PlayerMovement class attached to it (it is a player entity), and that its stamina is depleted.
     */
    public static boolean basicPlayerStaminaDepletionChecks(Player player) {
        if (!player.isCreative() && !player.isSpectator()) {

            // Ensure that the entity has a PlayerMovement class attached to it (it is a player entity), and that its stamina is depleted.
            PlayerMovement playerMovement = PlayerMovement.of(player);
            return playerMovement != null && playerMovement.isDepleted();
        }

        return false;
    }
}
