package net.cravencraft.betterparagliders.mixins;

import net.bettercombat.logic.PlayerAttackProperties;
import net.combatroll.client.MinecraftClientExtension;
import net.combatroll.client.RollManager;
import net.cravencraft.betterparagliders.capabilities.PlayerMovementInterface;
import net.cravencraft.betterparagliders.network.ModNet;
import net.cravencraft.betterparagliders.network.SyncActionToServerMsg;
import net.cravencraft.betterparagliders.utils.CalculateStaminaUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ProjectileWeaponItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tictim.paraglider.ParagliderMod;
import tictim.paraglider.capabilities.ClientPlayerMovement;
import tictim.paraglider.capabilities.PlayerMovement;

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

    public int getTotalActionStaminaCost() {
        return this.totalActionStaminaCost;
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
        int currentCombo = ((PlayerAttackProperties) player).getComboCount();
        this.comboCount = (currentCombo == 0) ? currentCombo : this.comboCount;

        if (currentCombo > 0 && currentCombo != this.comboCount) {
            this.comboCount = currentCombo;
            this.totalActionStaminaCost = CalculateStaminaUtils.calculateMeleeStaminaCost((LocalPlayer) this.player, currentCombo);
        }
    }

    private void calculateRangeStaminaCost() {
        //TODO: Maybe I want to pass in the projectileWeaponItem instead of LocalPlayer?
        if (player.getUseItem().getItem() instanceof  ProjectileWeaponItem projectileWeaponItem) {
            this.totalActionStaminaCost = CalculateStaminaUtils.calculateRangeStaminaCost((LocalPlayer) this.player);
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
        //TODO: Put fire out after x amount of rolls?
        if (this.rollManager.isRolling()) {
            this.totalActionStaminaCost = CalculateStaminaUtils.calculateRollStaminaCost((LocalPlayer) this.player);
        }
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
