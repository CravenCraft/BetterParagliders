package net.cravencraft.betterparagliders.mixins;

import net.cravencraft.betterparagliders.BetterParaglidersMod;
import net.cravencraft.betterparagliders.capabilities.PlayerMovementInterface;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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
    public int totalActionStaminaCost;

    @Shadow public abstract int getMaxStamina();

    @Shadow @Final public Player player;

    /**
     * TODO: Double check everything, but I think we have mixins working. DO make the updates
     *       to the static instances of the updated client and server players movements classes.
     *       Once you have that done, then start the clean up.
     *
     * @param ci
     */
    @Inject(method = "updateStamina", at = @At("HEAD"), cancellable = true, remap = false)
    public void updateStamina(CallbackInfo ci) {
//        this.totalActionStaminaCost = UpdatedPlayerMovement.instance.totalActionStaminaCost;
        BetterParaglidersMod.LOGGER.info("PLAYER MOVEMENT MIXIN TOTAL STAMINA COST: " + totalActionStaminaCost);
        if (this.totalActionStaminaCost != 0 || this.state.isConsume()) {
            this.recoveryDelay = 10;
            int stateChange = (state.isConsume()) ? state.change() : -this.totalActionStaminaCost;
            //TODO: Could change this to still drain if these don't both equal false. This fixes the need for
            //      the reflection method call!
            if (!this.depleted) {
                if (this.state.isParagliding()) {
                    if (!ModCfg.paraglidingConsumesStamina()) {
                        return;
                    }
                } else if (!ModCfg.runningConsumesStamina()) {
                    return;
                }

                this.stamina = Math.max(0, this.stamina + stateChange);

            }
        }
        else if (this.recoveryDelay > 0) {
            --this.recoveryDelay;
        }
        else if (this.state.change() > 0) {
            this.stamina = Math.min(this.getMaxStamina(), this.stamina + this.state.change());
        }
        addEffects();
        ci.cancel();
    }

    public int getTotalActionStaminaCost() {
        return this.totalActionStaminaCost;
    }

    public void setTotalActionStaminaCost(int totalActionStaminaCost) {
        this.totalActionStaminaCost = totalActionStaminaCost;
    }


    /**
     * Adds all the effects to be applied whenever the player's stamina is depleted.
     */
    protected void addEffects() {
        if(!this.player.isCreative() && this.depleted) {
            this.player.addEffect(new MobEffectInstance(MobEffect.byId(18))); // Adds weakness
        }
    }
}
