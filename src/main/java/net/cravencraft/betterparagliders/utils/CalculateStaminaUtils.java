package net.cravencraft.betterparagliders.utils;

import net.bettercombat.logic.PlayerAttackHelper;
import net.combatroll.api.EntityAttributes_CombatRoll;
import net.cravencraft.betterparagliders.attributes.BetterParaglidersAttributes;
import net.cravencraft.betterparagliders.config.UpdatedModCfg;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.SwordItem;

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
        double tierFactor = 0;
        double attackDamageFactor = 1; // Default hand damage is 1
        double reachFactor = PlayerAttackHelper.getCurrentAttack(player, currentCombo).attributes().attackRange();

        //TODO: Need to address dual wielding weapons. Uses the stamina of the first weapon.
        //      Can be abused with a stronger weapon in the offhand so it won't consume as much stamina.
        if (player.getMainHandItem().getItem() instanceof SwordItem swordItem) {
            attackDamageFactor += swordItem.getDamage();
            tierFactor = swordItem.getTier().getAttackDamageBonus();
        }
        else if (player.getMainHandItem().getItem() instanceof AxeItem axeItem) {
            attackDamageFactor += axeItem.getAttackDamage();
            tierFactor = axeItem.getTier().getAttackDamageBonus();
        }

//        BetterParaglidersMod.LOGGER.info("TIER FACTOR: " + tierFactor);
//        BetterParaglidersMod.LOGGER.info("REACH FACTOR: " + reachFactor);
//        BetterParaglidersMod.LOGGER.info("ATTACK SPEED FACTOR: " + player.getCurrentItemAttackStrengthDelay());
//        BetterParaglidersMod.LOGGER.info("STRENGTH FACTOR: " + player.getAttributeValue(BetterParaglidersAttributes.STRENGTH_FACTOR.get()));
//        BetterParaglidersMod.LOGGER.info("ATTACK DAMAGE FACTOR: " + attackDamageFactor);
//        BetterParaglidersMod.LOGGER.info("TOTAL STAMINA COST: " + ((attackDamageFactor * (tierFactor * 0.1 + 2)) + reachFactor));
//        BetterParaglidersMod.LOGGER.info("TOTAL STAMINA COST ROUNDED: " + Math.ceil((((attackDamageFactor * (tierFactor * 0.1 + 2)) + reachFactor) * player.getAttributeValue(BetterParaglidersAttributes.STRENGTH_FACTOR.get()) * UpdatedModCfg.meleeStaminaConsumption())));

        return (int) Math.ceil((((attackDamageFactor * (tierFactor * 0.1 + 2)) + reachFactor)
                * player.getAttributeValue(BetterParaglidersAttributes.STRENGTH_FACTOR.get())
                * UpdatedModCfg.meleeStaminaConsumption()));
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
        return (int) ((baseRangeStaminaCost * player.getAttributeValue(BetterParaglidersAttributes.RANGE_FACTOR.get())) * UpdatedModCfg.rangeStaminaConsumption());
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
        return (int) ((amount * serverPlayer.getAttributeValue(BetterParaglidersAttributes.BLOCK_FACTOR.get())) * UpdatedModCfg.blockStaminaConsumption());
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
        return (int) ((armorCost * enchantmentStaminaReduction * player.getAttributeValue(BetterParaglidersAttributes.ROLL_FACTOR.get())) * UpdatedModCfg.rollStaminaConsumption());
    }
}