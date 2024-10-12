package net.cravencraft.betterparagliders.mixins.paragliders.capabilities;

import net.cravencraft.betterparagliders.attributes.BetterParaglidersAttributes;
import net.cravencraft.betterparagliders.capabilities.PlayerMovementInterface;
import net.cravencraft.betterparagliders.utils.CalculateStaminaUtils;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tictim.paraglider.ModCfg;
import tictim.paraglider.capabilities.PlayerMovement;
import tictim.paraglider.capabilities.PlayerState;

@Mixin(PlayerMovement.class)
public abstract class PlayerMovementMixin implements PlayerMovementInterface {
    @Shadow private PlayerState state;
    @Shadow private int recoveryDelay;
    @Shadow private boolean depleted;
    @Shadow private int stamina;
    @Shadow public abstract int getMaxStamina();
    @Shadow @Final public Player player;

    @Shadow public abstract PlayerState getState();

    public int totalActionStaminaCost;

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

    /**
     * TODO: Double check everything, but I think we have mixins working. DO make the updates
     *       to the static instances of the updated client and server players movements classes.
     *       Once you have that done, then start the clean up.
     *
     * @param ci
     */
    @Inject(method = "updateStamina", at = @At("HEAD"), cancellable = true, remap = false)
    public void updateStamina(CallbackInfo ci) {
        int stateChange = CalculateStaminaUtils.getModifiedStateChange(this.player, this.getState());
        if (this.totalActionStaminaCost != 0 || this.state.isConsume()) {
            this.recoveryDelay = 10;

            stateChange = (state.isConsume()) ? stateChange - this.totalActionStaminaCost : -this.totalActionStaminaCost;

            if (!this.depleted && ((state.isParagliding()
                    ? ModCfg.paraglidingConsumesStamina()
                    : ModCfg.runningConsumesStamina()) || this.totalActionStaminaCost != 0)) {
                this.stamina = Math.max(0, this.stamina + stateChange);
            }
        }
        else if (this.recoveryDelay > 0) {
            --this.recoveryDelay;
        }
        else if (stateChange > 0) {
            this.stamina = Math.min(this.getMaxStamina(), this.stamina + stateChange);
        }

        if (this.totalActionStaminaCost > 0) {
            this.totalActionStaminaCost--;

            if (this.player instanceof ServerPlayer) {
                this.setTotalActionStaminaCostServerSide(this.totalActionStaminaCost);
            }
            else if (this.player instanceof LocalPlayer) {
                this.setTotalActionStaminaCostClientSide(this.totalActionStaminaCost);
            }
        }

        ci.cancel();
    }
}