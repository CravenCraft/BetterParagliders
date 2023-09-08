package net.cravencraft.betterparagliders.mixins;

import net.cravencraft.betterparagliders.capabilities.PlayerMovementInterface;
import net.cravencraft.betterparagliders.config.UpdatedModCfg;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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
import java.util.List;

@Mixin(PlayerMovement.class)
public abstract class PlayerMovementMixin implements PlayerMovementInterface {
    @Shadow private PlayerState state;
    @Shadow private int recoveryDelay;
    @Shadow private boolean depleted;
    @Shadow private int stamina;
    @Shadow public abstract int getMaxStamina();
    @Shadow @Final public Player player;
    public int totalActionStaminaCost;


    /**
     * TODO: Double check everything, but I think we have mixins working. DO make the updates
     *       to the static instances of the updated client and server players movements classes.
     *       Once you have that done, then start the clean up.
     *
     * @param ci
     */
    @Inject(method = "updateStamina", at = @At("HEAD"), cancellable = true, remap = false)
    public void updateStamina(CallbackInfo ci) {
        if (this.totalActionStaminaCost != 0 || this.state.isConsume()) {
            this.recoveryDelay = 10;
            int stateChange = (state.isConsume()) ? state.change() - this.totalActionStaminaCost : -this.totalActionStaminaCost;

            if (!this.depleted && ((state.isParagliding()
                    ? ModCfg.paraglidingConsumesStamina()
                    : ModCfg.runningConsumesStamina()) || this.totalActionStaminaCost != 0)) {
                this.stamina = Math.max(0, this.stamina + stateChange);
            }
        }
        else if (this.recoveryDelay > 0) {
            --this.recoveryDelay;
        }
        else if (this.state.change() > 0) {
            this.stamina = Math.min(this.getMaxStamina(), this.stamina + this.state.change());
        }

        if (this.totalActionStaminaCost > 0) {
            this.totalActionStaminaCost--;
        }

        if (this.player instanceof ServerPlayer) {
            this.setTotalActionStaminaCostServerSide(this.totalActionStaminaCost);
        }
        else if (this.player instanceof LocalPlayer) {
            this.setTotalActionStaminaCostClientSide(this.totalActionStaminaCost);
        }

        addEffects();
        ci.cancel();
    }

    @Override
    public int getTotalActionStaminaCost() {
        return this.totalActionStaminaCost;
    }

    @Override
    public void setTotalActionStaminaCost(int totalActionStaminaCost) {
        this.totalActionStaminaCost = totalActionStaminaCost;
    }

    /**
     * Adds all the effects to be applied whenever the player's stamina is depleted.
     */
    protected void addEffects() {
        if(!this.player.isCreative() && this.depleted) {
            List<Integer> effects = UpdatedModCfg.depletionEffectList();
            List<Integer> effectStrengths = UpdatedModCfg.depletionEffectStrengthList();

            for (int i=0; i < effects.size(); i++) {
                int effectStrength;
                if (i >= effectStrengths.size()) {
                    effectStrength = 0;
                }
                else {
                    effectStrength = effectStrengths.get(i) - 1;
                }

                if (MobEffect.byId(effects.get(i)) != null) {
                    this.player.addEffect(new MobEffectInstance(MobEffect.byId(effects.get(i)), 0, effectStrength));
                }
                else {
                    if (this.player instanceof ServerPlayer serverPlayer) {
                        serverPlayer.displayClientMessage(Component.literal("Effect with ID " + effects.get(i) + " does not exist."), true);
                    }
                }

            }
        }
    }
}
