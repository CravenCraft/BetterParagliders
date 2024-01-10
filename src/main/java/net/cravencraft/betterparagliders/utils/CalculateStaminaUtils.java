package net.cravencraft.betterparagliders.utils;

import net.bettercombat.api.AttackHand;
import net.bettercombat.logic.PlayerAttackHelper;
import net.cravencraft.betterparagliders.attributes.BetterParaglidersAttributes;
import net.cravencraft.betterparagliders.config.ConfigManager;
import net.cravencraft.betterparagliders.config.ServerConfig;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import tictim.paraglider.capabilities.PlayerState;

public class CalculateStaminaUtils {

    private static final int baseRangeStaminaCost = 10;

    /**
     * Drains stamina based on the player's weapon. It's damage, tier, and reach.
     * As well, attributes and the config can determine how much stamina is drained.
     *
     * @param player
     * @param currentCombo
     * @return
     */
    public static int calculateMeleeStaminaCost(LocalPlayer player, int currentCombo) {
        ServerConfig serverConfig = ConfigManager.SERVER_CONFIG;
        AttackHand attackHand = PlayerAttackHelper.getCurrentAttack(player, currentCombo);
        double reachFactor = attackHand.attributes().attackRange();
        boolean isTwoHanded = attackHand.attributes().isTwoHanded();

        double weaponAttackDamage = attackHand.itemStack().getItem().getAttributeModifiers(EquipmentSlot.MAINHAND, attackHand.itemStack())
                .get(Attributes.ATTACK_DAMAGE).stream()
                .filter(attributeModifier -> attributeModifier.getName().contains("Weapon") || attributeModifier.getName().contains("Tool"))
                .findFirst().get().getAmount();

        double totalStaminaDrain = (weaponAttackDamage + reachFactor) * serverConfig.meleeStaminaConsumption();

        if (isTwoHanded) {
            totalStaminaDrain = (totalStaminaDrain * serverConfig.twoHandedStaminaConsumption()) - player.getAttributeValue(BetterParaglidersAttributes.TWO_HANDED_STAMINA_REDUCTION.get());
        }
        else {
            totalStaminaDrain = (totalStaminaDrain * serverConfig.oneHandedStaminaConsumption()) - player.getAttributeValue(BetterParaglidersAttributes.ONE_HANDED_STAMINA_REDUCTION.get());
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
    public static int calculateRangeStaminaCost(LocalPlayer player) {
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