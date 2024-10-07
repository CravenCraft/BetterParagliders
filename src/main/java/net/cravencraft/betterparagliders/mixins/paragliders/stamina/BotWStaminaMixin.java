package net.cravencraft.betterparagliders.mixins.paragliders.stamina;

import net.cravencraft.betterparagliders.BetterParaglidersMod;
import net.cravencraft.betterparagliders.attributes.BetterParaglidersAttributes;
import net.cravencraft.betterparagliders.capabilities.StaminaOverride;
import net.cravencraft.betterparagliders.utils.CalculateStaminaUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tictim.paraglider.api.Copy;
import tictim.paraglider.api.ParagliderAPI;
import tictim.paraglider.api.Serde;
import tictim.paraglider.api.movement.Movement;
import tictim.paraglider.api.movement.ParagliderPlayerStates;
import tictim.paraglider.api.movement.PlayerState;
import tictim.paraglider.api.stamina.Stamina;
import tictim.paraglider.impl.movement.PlayerMovement;
import tictim.paraglider.impl.stamina.BotWStamina;

import static tictim.paraglider.api.movement.ParagliderPlayerStates.*;

@Mixin(BotWStamina.class)
public abstract class BotWStaminaMixin implements Stamina, Copy, Serde, StaminaOverride {

    @Shadow public abstract boolean isDepleted();

    @Shadow public abstract int giveStamina(int amount, boolean simulate);

    @Unique
    private int totalActionStaminaCost;
    @Unique
    private String currentPlayerState = "";

    @Override
    public int getTotalActionStaminaCost() {
        return this.totalActionStaminaCost;
    }

    @Override
    public void setTotalActionStaminaCost(int totalActionStaminaCost) {
        this.totalActionStaminaCost = totalActionStaminaCost;
    }

    @Inject(at = @At("HEAD"), remap = false, cancellable = true, method = "update")
    private void injectStaminaValues(@NotNull Movement movement, CallbackInfo ci) {
        PlayerState state = movement.state();
        int recoveryDelay = movement.recoveryDelay();
        int newRecoveryDelay = recoveryDelay;
        BetterParaglidersMod.LOGGER.info("PLAYER STATE: {}", state.id().getPath());
        BetterParaglidersMod.LOGGER.info("CURRENT PLAYER STATE FOR MIXIN: {}", this.currentPlayerState);

        if (state.staminaDelta() < 0 || this.totalActionStaminaCost != 0) {

            if (!isDepleted()) {
                //TODO: The total stamina cost is stacking for the parcool states. Just make the states cost a small amount.
                //      See if it pairs well with the attribute modifications too. Cling to cliff needs to be added as well.
                int staminaDelta = CalculateStaminaUtils.getModifiedStateChange((PlayerMovement) movement);

                BetterParaglidersMod.LOGGER.info("STAMINA DELTA: {}", staminaDelta);
                if (CalculateStaminaUtils.getAdditionalMovementStaminaCost(state.id().getPath())) {
                    BetterParaglidersMod.LOGGER.info("IS AN ADDITIONAL MOVEMENT STAMINA STATE. TOTAL COST NOW: {} STAMINA DELTA NOW: {}", this.totalActionStaminaCost, staminaDelta);

                    if (!this.currentPlayerState.equals(state.id().getPath())) {
                        this.totalActionStaminaCost -= staminaDelta;
                    }
                    staminaDelta = 0;
                }

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

        if (!this.currentPlayerState.equals(state.id().getPath())) {
            this.currentPlayerState = state.id().getPath();
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

        //noinspection DataFlowIssue
        newRecoveryDelay = Math.max(newRecoveryDelay, state.recoveryDelay());
        if (recoveryDelay != newRecoveryDelay) {
            movement.setRecoveryDelay(newRecoveryDelay);
        }

        ci.cancel();
    }
}