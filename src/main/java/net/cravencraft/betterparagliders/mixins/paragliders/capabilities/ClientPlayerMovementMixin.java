package net.cravencraft.betterparagliders.mixins.paragliders.capabilities;

import net.bettercombat.logic.PlayerAttackProperties;
import net.cravencraft.betterparagliders.capabilities.PlayerMovementInterface;
import net.cravencraft.betterparagliders.network.ModNet;
import net.cravencraft.betterparagliders.network.SyncActionToServerMsg;
import net.cravencraft.betterparagliders.utils.CalculateStaminaUtils;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ProjectileWeaponItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tictim.paraglider.capabilities.ClientPlayerMovement;
import tictim.paraglider.capabilities.PlayerMovement;

@Mixin(ClientPlayerMovement.class)
public abstract class ClientPlayerMovementMixin extends PlayerMovement implements PlayerMovementInterface {

    private boolean syncActionStamina;
    private int totalActionStaminaCost;
    private int comboCount;

    public ClientPlayerMovementMixin(Player player) {
        super(player);
    }

    @Inject(method = "update", at = @At(value = "HEAD"), remap=false)
    public void update(CallbackInfo ci) {
        calculateTotalStaminaCost();
        this.setTotalActionStaminaCost(this.totalActionStaminaCost);
    }

    @Override
    public int getTotalActionStaminaCost() {
        return this.totalActionStaminaCost;
    }

    @Override
    public void setTotalActionStaminaCostClientSide(int totalActionStaminaCost) {
        this.totalActionStaminaCost = totalActionStaminaCost;
    }

    @Override
    public void syncActionStaminaClientSide(boolean syncActionStamina) {
        this.syncActionStamina = syncActionStamina;
    }

    /**
     * Calculates the total stamina cost from all actions (melee and ranged attacks so far). If the action stamina cost
     * is greater than 0, then a message is sent to the server to update the server player.
     */
    private void calculateTotalStaminaCost() {
        // Where all stamina draining methods will go
        calculateMeleeStaminaCost();
        calculateRangeStaminaCost();

        if (this.syncActionStamina) {
            ModNet.NET.sendToServer(new SyncActionToServerMsg(this.totalActionStaminaCost));
            this.syncActionStamina = false;
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
            this.syncActionStamina = true;
        }
    }

    private void calculateRangeStaminaCost() {
        //TODO: Maybe I want to pass in the projectileWeaponItem instead of LocalPlayer?
        if (player.getUseItem().getItem() instanceof  ProjectileWeaponItem projectileWeaponItem) {
            this.totalActionStaminaCost = CalculateStaminaUtils.calculateRangeStaminaCost((LocalPlayer) this.player);
            this.syncActionStamina = true;
        }
    }
}