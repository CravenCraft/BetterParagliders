package net.cravencraft.betterparagliders.utils;

import net.bettercombat.api.AttackHand;
import net.bettercombat.logic.PlayerAttackHelper;
import net.cravencraft.betterparagliders.BetterParaglidersMod;
import net.cravencraft.betterparagliders.attributes.BetterParaglidersAttributes;
import net.cravencraft.betterparagliders.config.ConfigManager;
import net.cravencraft.betterparagliders.config.ServerConfig;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import tictim.paraglider.capabilities.PlayerState;

import java.util.HashMap;
import java.util.Map;

public class CalculateStaminaUtils {

    private static Map<String, Double> datapackStaminaOverrides = new HashMap();

    private static final int baseRangeStaminaCost = 10;

    /**
     * TODO: Write description
     *
     * @param itemStack
     * @param staminaCost
     */
    public static void addDatapackStaminaOverride(String itemStack, double staminaCost) {
        BetterParaglidersMod.LOGGER.info("ADDING TO STAMINA OVERRIDE: " + itemStack + " + " + staminaCost);
        datapackStaminaOverrides.put(itemStack, staminaCost);
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
        ServerConfig serverConfig = ConfigManager.SERVER_CONFIG;
        AttackHand attackHand = PlayerAttackHelper.getCurrentAttack(player, currentCombo);
        boolean isTwoHanded = attackHand.attributes().isTwoHanded();

        for (Map.Entry<String, Double> entry : datapackStaminaOverrides.entrySet()) {
            BetterParaglidersMod.LOGGER.info("DATAPACK OVERRIDE INFO: " + entry.getKey() + " + " + entry.getValue());
        }

        BetterParaglidersMod.LOGGER.info("CURRENT ATTACKING WEAPON: " + attackHand.itemStack().getItem().toString());
        if (datapackStaminaOverrides.containsKey(attackHand.itemStack().getItem().toString())) {
            double staminaOverride = datapackStaminaOverrides.get(attackHand.itemStack().getItem().toString());
            BetterParaglidersMod.LOGGER.info("STAMINA OVERRIDE FOR " + attackHand.itemStack() + " IS " + staminaOverride);
            totalStaminaDrain = staminaOverride * serverConfig.meleeStaminaConsumption();

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

            totalStaminaDrain = (weaponAttackDamage + reachFactor) * serverConfig.meleeStaminaConsumption();

            if (isTwoHanded) {
                totalStaminaDrain = (totalStaminaDrain * serverConfig.twoHandedStaminaConsumption()) - player.getAttributeValue(BetterParaglidersAttributes.TWO_HANDED_STAMINA_REDUCTION.get());
            }
            else {
                totalStaminaDrain = (totalStaminaDrain * serverConfig.oneHandedStaminaConsumption()) - player.getAttributeValue(BetterParaglidersAttributes.ONE_HANDED_STAMINA_REDUCTION.get());
            }
        }

        totalStaminaDrain -= player.getAttributeValue(BetterParaglidersAttributes.BASE_MELEE_STAMINA_REDUCTION.get());

        return (int) Math.ceil(totalStaminaDrain);
    }

    /**
     * TODO: Want to flesh this out a little more. Maybe set a high initial cost to draw the weapon back,
     *       then a steady drain from there if it's a bow, or no drain at all if it's a crossbow.
     *       Add config support for decreasing/increasing stamina consumption.
     *
     * @param player
     * @return
     */
    public static int calculateRangeStaminaCost(Player player) {
        return (int) ((baseRangeStaminaCost * ConfigManager.SERVER_CONFIG.rangeStaminaConsumption()) - player.getAttributeValue(BetterParaglidersAttributes.RANGE_STAMINA_REDUCTION.get()));
    }

    /**
     * Modifies the stamina drain for the current states below based on the attribute values
     * for the given player.
     *
     * @return
     */
    public static int getModifiedStateChange(Player player, PlayerState playerState) {
        switch (playerState) {
            case IDLE:
                return (int) (playerState.change() + player.getAttributeValue(BetterParaglidersAttributes.IDLE_STAMINA_REGEN.get()));
            case RUNNING:
                return (int) (playerState.change() + player.getAttributeValue(BetterParaglidersAttributes.SPRINTING_STAMINA_REDUCTION.get()));
            case SWIMMING:
                return (int) (playerState.change() + player.getAttributeValue(BetterParaglidersAttributes.SWIMMING_STAMINA_REDUCTION.get()));
            case UNDERWATER:
                return (int) (playerState.change() + player.getAttributeValue(BetterParaglidersAttributes.SUBMERGED_STAMINA_REGEN.get()));
            case BREATHING_UNDERWATER:
                return (int) (playerState.change() + player.getAttributeValue(BetterParaglidersAttributes.WATER_BREATHING_STAMINA_REGEN.get()));
            default:
                return playerState.change();
        }
    }
}