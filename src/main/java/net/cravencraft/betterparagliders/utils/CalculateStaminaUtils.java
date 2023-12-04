package net.cravencraft.betterparagliders.utils;

import net.bettercombat.api.AttackHand;
import net.bettercombat.logic.PlayerAttackHelper;
import net.combatroll.api.EntityAttributes_CombatRoll;
import net.cravencraft.betterparagliders.BetterParaglidersMod;
import net.cravencraft.betterparagliders.attributes.BetterParaglidersAttributes;
import net.cravencraft.betterparagliders.config.ConfigManager;
import net.cravencraft.betterparagliders.config.ServerConfig;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;

import static net.combatroll.api.EntityAttributes_CombatRoll.Type.COUNT;

public class CalculateStaminaUtils {

    private static final int baseRangeStaminaCost = 10;
    private static final int baseRollStaminaCost = 10;

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

        double totalStaminaDrain = (weaponAttackDamage + reachFactor)
                        * player.getAttributeValue(BetterParaglidersAttributes.BASE_MELEE_STAMINA_REDUCTION.get())
                        * serverConfig.meleeStaminaConsumption();

        if (isTwoHanded) {
            totalStaminaDrain *= player.getAttributeValue(BetterParaglidersAttributes.TWO_HANDED_STAMINA_REDUCTION.get())
                        * serverConfig.twoHandedStaminaConsumption();
        }
        else {
            totalStaminaDrain *= player.getAttributeValue(BetterParaglidersAttributes.ONE_HANDED_STAMINA_REDUCTION.get())
                        * serverConfig.oneHandedStaminaConsumption();
        }

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
        return (int) ((baseRangeStaminaCost * player.getAttributeValue(BetterParaglidersAttributes.RANGE_STAMINA_REDUCTION.get())) * ConfigManager.SERVER_CONFIG.rangeStaminaConsumption());
    }

    /**
     * TODO: Work on this so that blocks drain more stamina, and that they're also drained based on a player's
     *       current armor value. Higher armor value = less stamina used to block, but more used to roll.
     *       Add config support for decreasing/increasing stamina consumption.
     *
     * @param serverPlayer
     * @param amount
     * @return
     */
    public static int calculateBlockStaminaCost(ServerPlayer serverPlayer, float amount) {
        return (int) ((amount * serverPlayer.getAttributeValue(BetterParaglidersAttributes.BLOCK_STAMINA_REDUCTION.get())) * ConfigManager.SERVER_CONFIG.blockStaminaConsumption());
    }

    /**
     * TODO: Maybe set that baseCost for rolling to be a static final field in this class.
     *       Add config support for decreasing/increasing stamina consumption.
     *
     * Calculates the amount of stamina a roll will cost for the player based on a set base cost,
     * particular enchantments, and the player's current armor value. Higher values = more stamina consumption.
     *
     * @param player
     * @return
     */
    public static int calculateRollStaminaCost(LocalPlayer player) {
        double enchantmentStaminaReduction = (1 - (EntityAttributes_CombatRoll.getAttributeValue(player, COUNT) * 0.05));
        int armorCost = (player.getArmorValue() > baseRollStaminaCost) ? player.getArmorValue() : baseRollStaminaCost;
        return (int) ((armorCost * enchantmentStaminaReduction * player.getAttributeValue(BetterParaglidersAttributes.ROLL_STAMINA_REDUCTION.get())) * ConfigManager.SERVER_CONFIG.rollStaminaConsumption());
    }
}