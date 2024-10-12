package net.cravencraft.betterparagliders.mixins.paragliders.capabilities;

import net.bettercombat.api.MinecraftClient_BetterCombat;
import net.bettercombat.logic.PlayerAttackProperties;
import net.cravencraft.betterparagliders.capabilities.PlayerMovementInterface;
import net.cravencraft.betterparagliders.network.ModNet;
import net.cravencraft.betterparagliders.network.SyncActionToServerMsg;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tictim.paraglider.capabilities.ClientPlayerMovement;
import tictim.paraglider.capabilities.PlayerMovement;

@Mixin(ClientPlayerMovement.class)
public abstract class ClientPlayerMovementMixin extends PlayerMovement implements PlayerMovementInterface {
    private int totalActionStaminaCost;
    private int comboCount = 0;

    public ClientPlayerMovementMixin(Player player) {
        super(player);
    }

    @Inject(method = "update", at = @At(value = "HEAD"), remap=false)
    public void update(CallbackInfo ci) {

        if (!this.player.isCreative() && !this.player.isSpectator() && this.isDepleted()) {
            ((MinecraftClient_BetterCombat) Minecraft.getInstance()).cancelUpswing();
        }
        else {
            calculateMeleeStaminaCost();
            this.setTotalActionStaminaCost(this.totalActionStaminaCost);
        }
    }

    @Override
    public void setTotalActionStaminaCostClientSide(int totalActionStaminaCost) {
        this.totalActionStaminaCost = totalActionStaminaCost;
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
            ModNet.NET.sendToServer(new SyncActionToServerMsg(this.comboCount));
            this.comboCount = currentCombo;
        }
    }
}