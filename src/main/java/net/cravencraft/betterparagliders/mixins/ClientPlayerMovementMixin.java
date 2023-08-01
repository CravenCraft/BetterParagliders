package net.cravencraft.betterparagliders.mixins;

import net.bettercombat.api.AttackHand;
import net.bettercombat.logic.PlayerAttackHelper;
import net.bettercombat.logic.PlayerAttackProperties;
import net.combatroll.api.EntityAttributes_CombatRoll;
import net.combatroll.client.MinecraftClientExtension;
import net.combatroll.client.RollManager;
import net.cravencraft.betterparagliders.BetterParaglidersMod;
import net.cravencraft.betterparagliders.attributes.BetterParaglidersAttributes;
import net.cravencraft.betterparagliders.capabilities.PlayerMovementInterface;
import net.cravencraft.betterparagliders.config.UpdatedModCfg;
import net.cravencraft.betterparagliders.network.ModNet;
import net.cravencraft.betterparagliders.network.SyncActionToServerMsg;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.SwordItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tictim.paraglider.ParagliderMod;
import tictim.paraglider.capabilities.ClientPlayerMovement;
import tictim.paraglider.capabilities.PlayerMovement;

import java.lang.reflect.Field;

import static net.combatroll.api.EntityAttributes_CombatRoll.Type.COUNT;

@Mixin(ClientPlayerMovement.class)
public abstract class ClientPlayerMovementMixin extends PlayerMovement implements PlayerMovementInterface {
    private int totalActionStaminaCost;
    private int comboCount;
    private RollManager rollManager;

    public ClientPlayerMovementMixin(Player player) {
        super(player);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void constructorHead(Player player, CallbackInfo ci) {
        this.rollManager = ((MinecraftClientExtension)Minecraft.getInstance()).getRollManager();
    }

    @Inject(method = "update", at = @At(value = "HEAD"), remap=false)
    public void update(CallbackInfo ci) {
        calculateTotalStaminaCost();
        try {
            calculateRollStaminaCost();
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        disableRoll();
        this.setTotalActionStaminaCost(this.totalActionStaminaCost);
    }

    @Override
    public void setTotalActionStaminaCostClientSide(int totalActionStaminaCost) {
        this.totalActionStaminaCost = totalActionStaminaCost;
    }

    /**
     * //TODO: ACCOUNT FOR SPRINTING. Probably need to do this in the UpdatedPlayerMovement class and the stamina wheel render class
     * Calculates the total stamina cost from all actions (melee and ranged attacks so far). If the action stamina cost
     * is greater than 0, then a message is sent to the server to update the server player.
     */
    private void calculateTotalStaminaCost() {
        // Where all stamina draining methods will go
        calculateMeleeStaminaCost();
        calculateRangeStaminaCost();

        if (this.totalActionStaminaCost > 0) {
            this.totalActionStaminaCost--;
            ModNet.NET.sendToServer(new SyncActionToServerMsg(this.totalActionStaminaCost));
        }
    }

    /**
     * Calculates the total amount of stamina to drain if the player is performing a melee attack. First checks if the
     * player is holding a Better Combat compatible weapon, then checks the current combo state of the weapon to see
     * if the stamina needs to be updated.
     */
    private void calculateMeleeStaminaCost() {
//        Player player = this.player;
        int currentCombo = ((PlayerAttackProperties) player).getComboCount();
        AttackHand attackHand = PlayerAttackHelper.getCurrentAttack(player, currentCombo);

        this.comboCount = (currentCombo == 0) ? currentCombo : this.comboCount;

        if (attackHand != null && currentCombo > 0 && currentCombo != this.comboCount) {

            this.comboCount = currentCombo;
            double attackDamage = 1;
            int tierLevel = 0;


            if (player.getMainHandItem().getItem() instanceof SwordItem swordItem) {
                attackDamage = swordItem.getDamage();
                tierLevel = (int) swordItem.getTier().getAttackDamageBonus();
            }
            else if (player.getMainHandItem().getItem() instanceof AxeItem axeItem) {
                attackDamage = axeItem.getAttackDamage();
                tierLevel = (int) axeItem.getTier().getAttackDamageBonus();
            }

            double damageTierFactor;
            switch (tierLevel) {
                case 1:
                case 2:
                    damageTierFactor = 7.0;
                    break;
                case 3:
                    damageTierFactor = 8.0;
                    break;
                case 4:
                    damageTierFactor = 9.0;
                    break;
                case 5:
                    damageTierFactor = 10.0;
                    break;
                default:
                    damageTierFactor = 5.0;

            }


            double damageFactor = attackDamage + 1;
            if (damageFactor < 5) {
                damageFactor = 5;
            }
            else if (damageFactor > damageTierFactor) {
                damageFactor = damageTierFactor;
            }

            double reachFactor = attackHand.attributes().attackRange();
            if (reachFactor > 3) {
                reachFactor = 3;
            }

            BetterParaglidersMod.LOGGER.info("STRENGTH PENALTY: " + this.player.getAttributeValue(BetterParaglidersAttributes.STRENGTH_PENALTY.get()));
            BetterParaglidersMod.LOGGER.info("ORIGINAL STAMINA COST: " + damageFactor * reachFactor);
            //TODO: Uncomment this after you get everything working. Needs the attribute method to actually work.
//            this.totalActionStaminaCost = (int) ((damageFactor * reachFactor * this.player.getAttributeValue(BetterParaglidersAttributes.STRENGTH_PENALTY.get())));
            this.totalActionStaminaCost = (int) ((damageFactor * reachFactor));
            BetterParaglidersMod.LOGGER.info("NEW STAMINA COST: " + this.totalActionStaminaCost);
        }
    }

    private void calculateRangeStaminaCost() {
        Player player = this.player;
        if (player.getUseItem().getItem() instanceof  ProjectileWeaponItem projectileWeaponItem) {
            ParagliderMod.LOGGER.info( (int) player.getCurrentItemAttackStrengthDelay());


            // TODO: Def gonna make some static final vars
            this.totalActionStaminaCost = UpdatedModCfg.baseRangeStaminaCost();
        }
    }

    /**
     * Calculates the amount of stamina a roll will cost. Factors in weight and enchantments as well.
     *
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     */
    private void calculateRollStaminaCost() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException {
        Field availableRollsField = RollManager.class.getDeclaredField("availableRolls");
        availableRollsField.setAccessible(true);
        //TODO: Put fire out after x amount of rolls?
        if (this.rollManager.isRolling()) {
            double enchantmentStaminaReduction = (1 - (EntityAttributes_CombatRoll.getAttributeValue(this.player, COUNT) * 0.05));
            int baseCost = 10;
            int armorCost = (this.player.getArmorValue() > 10) ? this.player.getArmorValue() : baseCost;
            double rawStaminaCost = armorCost * enchantmentStaminaReduction;
            this.totalActionStaminaCost = (int) (rawStaminaCost);
        }
    }

    public int getTotalActionStaminaCost() {
        return this.totalActionStaminaCost;
    }

    //TODO: Make this single line maybe
    public void disableRoll() {
        if(!this.player.isCreative() && this.isDepleted()){
            this.rollManager.isEnabled = false;
        }
        else {
            this.rollManager.isEnabled = true;
        }
    }
}
