package net.cravencraft.betterparagliders.mixins.paragliders.client;

import net.cravencraft.betterparagliders.capabilities.PlayerMovementInterface;
import net.cravencraft.betterparagliders.utils.CalculateStaminaUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tictim.paraglider.ModCfg;
import tictim.paraglider.capabilities.PlayerMovement;
import tictim.paraglider.capabilities.PlayerState;
import tictim.paraglider.client.InGameStaminaWheelRenderer;
import tictim.paraglider.client.StaminaWheelConstants;
import tictim.paraglider.client.StaminaWheelRenderer;
import tictim.paraglider.utils.Color;

@Mixin(InGameStaminaWheelRenderer.class)
public abstract class InGameStaminaWheelRendererMixin extends StaminaWheelRenderer {

    @Shadow private int prevStamina;

    @Shadow private long fullTime;

    @Inject(method = "makeWheel", at = @At(value = "HEAD"), remap=false)
    public void makeWheel(PlayerMovement h, CallbackInfo ci) {
        int totalActionStaminaCost = ((PlayerMovementInterface) h).getTotalActionStaminaCost();
        int stamina = h.getStamina();

        int maxStamina = h.getMaxStamina();
        if (stamina >= maxStamina) {
            long time = System.currentTimeMillis();
            long timeDiff;
            if (this.prevStamina != stamina) {
                this.prevStamina = stamina;
                this.fullTime = time;
                timeDiff = 0L;
            } else {
                timeDiff = time - this.fullTime;
            }

            Color color = StaminaWheelConstants.getGlowAndFadeColor(timeDiff);
            if (color.alpha <= 0.0F) {
                return;
            }

            StaminaWheelRenderer.WheelLevel[] var9 = StaminaWheelRenderer.WheelLevel.values();

            for (WheelLevel t : var9) {
                this.addWheel(t, 0.0, t.getProportion(stamina), color);
            }
        } else {
            this.prevStamina = stamina;
            Color color = StaminaWheelConstants.DEPLETED_1.blend(StaminaWheelConstants.DEPLETED_2, StaminaWheelConstants.cycle(System.currentTimeMillis(), h.isDepleted() ? 600L : 300L));
            PlayerState state = h.getState();
            int stateChange = CalculateStaminaUtils.getModifiedStateChange(h.player, state);
            stateChange = (state.isConsume()) ? stateChange - totalActionStaminaCost : -totalActionStaminaCost;
            StaminaWheelRenderer.WheelLevel[] var14 = StaminaWheelRenderer.WheelLevel.values();
            int var7 = var14.length;

            for(int var15 = 0; var15 < var7; ++var15) {
                StaminaWheelRenderer.WheelLevel t = var14[var15];
                this.addWheel(t, 0.0, t.getProportion(maxStamina), StaminaWheelConstants.EMPTY);
                if (h.isDepleted()) {
                    this.addWheel(t, 0.0, t.getProportion(stamina), color);
                } else {
                    this.addWheel(t, 0.0, t.getProportion(stamina), StaminaWheelConstants.IDLE);
                    if (((state.isConsume()
                            && (state.isParagliding() ? ModCfg.paraglidingConsumesStamina() : ModCfg.runningConsumesStamina())))
                            || totalActionStaminaCost > 0) {
                        this.addWheel(t, t.getProportion(stamina + stateChange * 10), t.getProportion(stamina), color);
                    }
                }
            }
        }
    }
}
