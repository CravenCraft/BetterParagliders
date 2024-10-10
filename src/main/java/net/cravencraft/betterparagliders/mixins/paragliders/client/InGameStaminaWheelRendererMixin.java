package net.cravencraft.betterparagliders.mixins.paragliders.client;

import net.cravencraft.betterparagliders.capabilities.StaminaOverride;
import net.cravencraft.betterparagliders.utils.CalculateStaminaUtils;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tictim.paraglider.ParagliderUtils;
import tictim.paraglider.api.stamina.Stamina;
import tictim.paraglider.client.render.InGameStaminaWheelRenderer;
import tictim.paraglider.client.render.StaminaWheelConstants;
import tictim.paraglider.client.render.StaminaWheelRenderer;
import tictim.paraglider.forge.capability.PlayerMovementProvider;
import tictim.paraglider.impl.movement.PlayerMovement;
import tictim.paraglider.impl.stamina.BotWStamina;

import static tictim.paraglider.ParagliderUtils.ms;
import static tictim.paraglider.client.render.StaminaWheelConstants.FADE_END;
import static tictim.paraglider.client.render.StaminaWheelConstants.getGlowAndFadeColor;

@Mixin(InGameStaminaWheelRenderer.class)
public abstract class InGameStaminaWheelRendererMixin extends StaminaWheelRenderer {
    @Shadow private boolean full;

    @Shadow private long prevFullTime;

    @Shadow private long fullDuration;

    @Inject(at = @At("HEAD"), remap = false, cancellable = true, method = "makeWheel")
    private void betterCombatStaminaWheelSupport(Player player, Wheel wheel, CallbackInfo ci) {
        PlayerMovement playerMovement = PlayerMovementProvider.of(player);
        if (playerMovement != null && Stamina.get(player) instanceof BotWStamina botWStamina) {
            int maxStamina = botWStamina.maxStamina();
            int stamina = Math.min(maxStamina, botWStamina.stamina());
            if(stamina>=maxStamina){
                makeFullWheelOverride(wheel, stamina);
            }
            else {
                this.full = false;
                boolean depleted = botWStamina.isDepleted();
                int totalActionStaminaCost = ((StaminaOverride) botWStamina).getTotalActionStaminaCost();
                int staminaDelta = CalculateStaminaUtils.getModifiedStateChange(playerMovement);

                if (CalculateStaminaUtils.getAdditionalMovementStaminaCost(playerMovement.state().id().getPath())) {
                    staminaDelta = 0;
                }

                staminaDelta = (staminaDelta < 0) ? staminaDelta - totalActionStaminaCost : -totalActionStaminaCost;
                wheel.fill(0, maxStamina, StaminaWheelConstants.EMPTY);
                if (depleted) {
                    wheel.fill(0, stamina, StaminaWheelConstants.getBlinkColor(ParagliderUtils.ms(), true));
                } else {
                    wheel.fill(0, stamina, StaminaWheelConstants.IDLE);
                    if (staminaDelta < 0) {
                        wheel.fill(stamina + staminaDelta * 10, stamina, StaminaWheelConstants.getBlinkColor(ParagliderUtils.ms(), false));
                    }
                }
            }
        }

        ci.cancel();
    }

    @Unique
    private void makeFullWheelOverride(@NotNull Wheel wheel, int stamina) {

        long time = ms();
        long timeDiff;
        if (!this.full) {
            this.full = true;
            this.fullDuration = 0;
        }
        else if (this.fullDuration < FADE_END) {
            timeDiff = time-this.prevFullTime;
            this.fullDuration = Math.min(this.fullDuration+timeDiff, FADE_END);
        } else return;

        int color = getGlowAndFadeColor(fullDuration);
        if(FastColor.ARGB32.alpha(color)<=0) return;
        wheel.fill(0, stamina, color);

        this.prevFullTime = time;
    }
}
