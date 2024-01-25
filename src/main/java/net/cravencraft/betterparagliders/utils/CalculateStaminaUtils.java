package net.cravencraft.betterparagliders.utils;

import net.bettercombat.api.AttackHand;
import net.bettercombat.logic.PlayerAttackHelper;
import net.cravencraft.betterparagliders.attributes.BetterParaglidersAttributes;
import net.cravencraft.betterparagliders.config.ServerConfig;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import tictim.paraglider.api.movement.PlayerState;

import java.util.HashMap;
import java.util.Map;

import static tictim.paraglider.api.movement.ParagliderPlayerStates.*;

public class CalculateStaminaUtils {

    public static Map<String, Double> DATAPACK_MELEE_STAMINA_OVERRIDES = new HashMap();
    public static Map<String, Double> DATAPACK_RANGED_STAMINA_OVERRIDES = new HashMap();
    public static Map<String, Double> DATAPACK_SHIELD_STAMINA_OVERRIDES = new HashMap();

    private static final int baseRangeStaminaCost = 10;

    /**
     * TODO: Write description
     *
     * @param itemStack
     * @param staminaCost
     */
    public static void addDatapackStaminaOverride(String type, String itemStack, double staminaCost) {

        switch (type) {
            case "shield":
                DATAPACK_SHIELD_STAMINA_OVERRIDES.put(itemStack, staminaCost);
                break;
            case "ranged_weapon":
                DATAPACK_RANGED_STAMINA_OVERRIDES.put(itemStack, staminaCost);
                break;
            case "melee_weapon":
            default:
                DATAPACK_MELEE_STAMINA_OVERRIDES.put(itemStack, staminaCost);
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
        double totalStaminaDrain;
        AttackHand attackHand = PlayerAttackHelper.getCurrentAttack(player, currentCombo);
        boolean isTwoHanded = attackHand.attributes().isTwoHanded();
        String attackingItemId = attackHand.itemStack().getItem().getDescriptionId().replace("item.", "");

        if (DATAPACK_MELEE_STAMINA_OVERRIDES.containsKey(attackingItemId)) {
            totalStaminaDrain = DATAPACK_MELEE_STAMINA_OVERRIDES.get(attackingItemId).intValue();

            if (isTwoHanded) {
                totalStaminaDrain -= player.getAttributeValue(BetterParaglidersAttributes.TWO_HANDED_STAMINA_REDUCTION.get());
            }
            else {
                totalStaminaDrain -= player.getAttributeValue(BetterParaglidersAttributes.ONE_HANDED_STAMINA_REDUCTION.get());
            }
        }
        else {
            double reachFactor = attackHand.attributes().attackRange();


            double weaponAttackDamage = attackHand.itemStack().getItem().getAttributeModifiers(EquipmentSlot.MAINHAND, attackHand.itemStack())
                    .get(Attributes.ATTACK_DAMAGE).stream()
                    .filter(attributeModifier -> attributeModifier.getName().contains("Weapon") || attributeModifier.getName().contains("Tool"))
                    .findFirst().get().getAmount();

            totalStaminaDrain = (weaponAttackDamage + reachFactor) * ServerConfig.meleeStaminaConsumption();

            if (isTwoHanded) {
                totalStaminaDrain = (totalStaminaDrain * ServerConfig.twoHandedStaminaConsumption()) - player.getAttributeValue(BetterParaglidersAttributes.TWO_HANDED_STAMINA_REDUCTION.get());
            }
            else {
                totalStaminaDrain = (totalStaminaDrain * ServerConfig.oneHandedStaminaConsumption()) - player.getAttributeValue(BetterParaglidersAttributes.ONE_HANDED_STAMINA_REDUCTION.get());
            }
        }

        totalStaminaDrain -= player.getAttributeValue(BetterParaglidersAttributes.BASE_MELEE_STAMINA_REDUCTION.get());

        return (int) Math.ceil(totalStaminaDrain);
    }

    /**
     * Will drain stamina based on the amount either the default configured amount for the bow being used,
     * or based on a datapack value overriding that amount.
     *
     * @param player
     * @return
     */
    public static int calculateRangeStaminaCost(Player player) {
        int totalStaminaConsumption;
        String bowItem = player.getUseItem().getItem().getDescriptionId().replace("item.", "");
        if (DATAPACK_RANGED_STAMINA_OVERRIDES.containsKey(bowItem)) {
            totalStaminaConsumption = DATAPACK_RANGED_STAMINA_OVERRIDES.get(bowItem).intValue();
        }
        else {
            totalStaminaConsumption = (int) (baseRangeStaminaCost * ServerConfig.rangeStaminaConsumption());
        }
        return (int) (totalStaminaConsumption - player.getAttributeValue(BetterParaglidersAttributes.RANGE_STAMINA_REDUCTION.get()));
    }

    /**
     * Will drain stamina based on the amount either the default configured amount for the shield being used,
     * or based on a datapack value overriding that amount.
     *
     * @param player
     * @param blockedDamage
     * @return
     */
    public static int calculateBlockStaminaCost(Player player, float blockedDamage) {
        int totalStaminaConsumption = (int) blockedDamage;
        String shieldItem = player.getUseItem().getItem().getDescriptionId().replace("item.", "");

        if (DATAPACK_SHIELD_STAMINA_OVERRIDES.containsKey(shieldItem)) {
            totalStaminaConsumption += DATAPACK_SHIELD_STAMINA_OVERRIDES.get(shieldItem).intValue();
        }
        else {
            totalStaminaConsumption += (int) (ServerConfig.blockStaminaConsumption());
        }

        return Math.round((float)(totalStaminaConsumption - player.getAttributeValue(BetterParaglidersAttributes.BLOCK_STAMINA_REDUCTION.get())));
    }

    /**
     * Modifies the stamina drain for the current states below based on the attribute values
     * for the given player.
     *
     * @return
     */
    public static int getModifiedStateChange(Player player, PlayerState playerState) {
        if (IDLE.equals(playerState)) {
            return (int) (playerState.staminaDelta() + player.getAttributeValue(BetterParaglidersAttributes.IDLE_STAMINA_REGEN.get()));
        } else if (RUNNING.equals(playerState)) {
            return (int) (playerState.staminaDelta() + player.getAttributeValue(BetterParaglidersAttributes.SPRINTING_STAMINA_REDUCTION.get()));
        } else if (SWIMMING.equals(playerState)) {
            return (int) (playerState.staminaDelta() + player.getAttributeValue(BetterParaglidersAttributes.SWIMMING_STAMINA_REDUCTION.get()));
        } else if (UNDERWATER.equals(playerState)) {
            return (int) (playerState.staminaDelta() + player.getAttributeValue(BetterParaglidersAttributes.SUBMERGED_STAMINA_REGEN.get()));
        } else if (BREATHING_UNDERWATER.equals(playerState)) {
            return (int) (playerState.staminaDelta() + player.getAttributeValue(BetterParaglidersAttributes.WATER_BREATHING_STAMINA_REGEN.get()));
        }
        return playerState.staminaDelta();
    }
}