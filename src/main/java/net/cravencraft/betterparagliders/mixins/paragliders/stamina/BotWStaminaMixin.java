package net.cravencraft.betterparagliders.mixins.paragliders.stamina;

import net.bettercombat.logic.PlayerAttackProperties;
import net.cravencraft.betterparagliders.attributes.BetterParaglidersAttributes;
import net.cravencraft.betterparagliders.capabilities.StaminaOverride;
import net.cravencraft.betterparagliders.network.ModNet;
import net.cravencraft.betterparagliders.network.SyncActionToServerMsg;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tictim.paraglider.api.Copy;
import tictim.paraglider.api.Serde;
import tictim.paraglider.api.movement.Movement;
import tictim.paraglider.api.movement.PlayerState;
import tictim.paraglider.api.stamina.Stamina;
import tictim.paraglider.impl.movement.ClientPlayerMovement;
import tictim.paraglider.impl.movement.PlayerMovement;
import tictim.paraglider.impl.movement.ServerPlayerMovement;
import tictim.paraglider.impl.stamina.BotWStamina;

import static tictim.paraglider.api.movement.ParagliderPlayerStates.*;

@Mixin(BotWStamina.class)
public abstract class BotWStaminaMixin implements Stamina, Copy, Serde, StaminaOverride {

    @Shadow public abstract boolean isDepleted();

    @Shadow public abstract int giveStamina(int amount, boolean simulate);

    private int totalActionStaminaCost;
    private int eldenStaminaDelay;
    private int comboCount;

    @Override
    public int getTotalActionStaminaCost() {
        return this.totalActionStaminaCost;
    }

    @Override
    public void setTotalActionStaminaCost(int totalActionStaminaCost) {
        this.totalActionStaminaCost = totalActionStaminaCost;
    }

    @Override
    public void setTotalActionStaminaCostClientSide(int totalActionStaminaCost) {
        this.totalActionStaminaCost = totalActionStaminaCost;
    }

    @Inject(at = @At("HEAD"), remap = false, cancellable = true, method = "update")
    private void injectEpicFightStaminaValues(@NotNull Movement movement, CallbackInfo ci) {
        PlayerMovement playerMovement = (PlayerMovement) movement;
        Player player = playerMovement.player();

        PlayerState state = movement.state();
        int recoveryDelay = movement.recoveryDelay();
        int newRecoveryDelay = recoveryDelay;
        int staminaDelta;
        PlayerState playerState = movement.state();
        if (IDLE.equals(playerState)) {
            staminaDelta = (int) (movement.state().staminaDelta() + player.getAttributeValue(BetterParaglidersAttributes.IDLE_STAMINA_REGEN.get()));
        } else if (RUNNING.equals(playerState)) {
            staminaDelta = (int) (movement.state().staminaDelta() + player.getAttributeValue(BetterParaglidersAttributes.SPRINTING_STAMINA_REDUCTION.get()));
        } else if (SWIMMING.equals(playerState)) {
            staminaDelta = (int) (movement.state().staminaDelta() + player.getAttributeValue(BetterParaglidersAttributes.SWIMMING_STAMINA_REDUCTION.get()));
        } else if (UNDERWATER.equals(playerState)) {
            staminaDelta = (int) (movement.state().staminaDelta() + player.getAttributeValue(BetterParaglidersAttributes.SUBMERGED_STAMINA_REGEN.get()));
        } else if (BREATHING_UNDERWATER.equals(playerState)) {
            staminaDelta = (int) (movement.state().staminaDelta() + player.getAttributeValue(BetterParaglidersAttributes.WATER_BREATHING_STAMINA_REGEN.get()));
        } else {
            staminaDelta = movement.state().staminaDelta();
        }

        if (state.staminaDelta() < 0 || this.totalActionStaminaCost != 0) {

            if (!isDepleted()) {
                //TODO: This probably needs to be redone with the triangular numbers formula
                staminaDelta = (staminaDelta < 0) ? staminaDelta - this.totalActionStaminaCost : -this.totalActionStaminaCost;
                if (staminaDelta > 0) {
                    giveStamina(staminaDelta, false);
                }
                else {
                    takeStamina(-staminaDelta, false, false);
                }

            }
        }
        else {
            if (recoveryDelay > 0) {
                newRecoveryDelay--;
            }
            else if (state.staminaDelta() > 0) {
                giveStamina(state.staminaDelta(), false);
            }
        }

        // Check for draining stamina
        if (this.totalActionStaminaCost > 0) {
            movement.setRecoveryDelay(10);
            this.totalActionStaminaCost--;
        }
        // Check for gaining stamina
        else if(this.totalActionStaminaCost < 0) {
            this.totalActionStaminaCost++;
        }

        if (movement instanceof ServerPlayerMovement) {
            this.setTotalActionStaminaCostServerSide(this.totalActionStaminaCost);
        }
        else if (movement instanceof ClientPlayerMovement clientPlayerMovement) {
            //TODO: Maybe client code here vs. in the ClientPlayerMovementMixin?
            //      Since it's only a single method.
            calculateMeleeStaminaCost(player);
        }

        //noinspection DataFlowIssue
        newRecoveryDelay = Math.max(0, Math.max(newRecoveryDelay, state.recoveryDelay()));
        if (recoveryDelay!=newRecoveryDelay) {
            movement.setRecoveryDelay(newRecoveryDelay);
        }

        ci.cancel();
    }

    /**
     * Calculates the total amount of stamina to drain if the player is performing a melee attack. First checks if the
     * player is holding a Better Combat compatible weapon, then checks the current combo state of the weapon to see
     * if the stamina needs to be updated.
     */
    private void calculateMeleeStaminaCost(Player player) {
        int currentCombo = ((PlayerAttackProperties) player).getComboCount();
        this.comboCount = (currentCombo == 0) ? currentCombo : this.comboCount;

        if (currentCombo > 0 && currentCombo != this.comboCount) {
            this.comboCount = currentCombo;
            ModNet.NET.sendToServer(new SyncActionToServerMsg(this.comboCount));
        }
    }
}