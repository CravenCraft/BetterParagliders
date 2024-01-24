package net.cravencraft.betterparagliders.mixins.paragliders.client;

import net.cravencraft.betterparagliders.capabilities.PlayerMovementInterface;
import net.cravencraft.betterparagliders.capabilities.StaminaOverride;
import net.cravencraft.betterparagliders.config.ConfigManager;
import net.cravencraft.betterparagliders.utils.CalculateStaminaUtils;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tictim.paraglider.api.movement.Movement;
import tictim.paraglider.api.movement.PlayerState;
import tictim.paraglider.api.stamina.Stamina;
import tictim.paraglider.client.render.InGameStaminaWheelRenderer;
import tictim.paraglider.client.render.StaminaWheelConstants;
import tictim.paraglider.client.render.StaminaWheelRenderer;
import tictim.paraglider.forge.capability.PlayerMovementProvider;
import tictim.paraglider.impl.stamina.BotWStamina;

import static tictim.paraglider.ParagliderUtils.ms;
import static tictim.paraglider.client.render.StaminaWheelConstants.*;
import static tictim.paraglider.client.render.StaminaWheelConstants.IDLE;

@Mixin(InGameStaminaWheelRenderer.class)
public abstract class InGameStaminaWheelRendererMixin extends StaminaWheelRenderer {

//    @Shadow private int prevStamina;

    @Shadow private long fullTime;

    @Shadow private boolean full;

    @Inject(at = @At("HEAD"), remap = false, cancellable = true, method = "makeWheel")
    private void betterCombatStaminaWheelSupport(Player player, Wheel wheel, CallbackInfo ci) {
        BotWStamina botWStamina = ((BotWStamina) PlayerMovementProvider.of(player).stamina());

        Stamina s = Stamina.get(player);
        int maxStamina = s.maxStamina();
        int stamina = Math.min(maxStamina, s.stamina());
        if(stamina>=maxStamina){
            long time = ms();
            long timeDiff;
            if(!this.full){
                this.full = true;
                this.fullTime = time;
                timeDiff = 0;
            }else timeDiff = time - this.fullTime;
            int color = StaminaWheelConstants.getGlowAndFadeColor(timeDiff);
            if(FastColor.ARGB32.alpha(color)<=0) return;
            wheel.fill(0, stamina, color);
        }
        else {
            this.full = false;
            boolean depleted = s.isDepleted();
            int color = FastColor.ARGB32.lerp(cycle(ms(), depleted ? DEPLETED_BLINK : BLINK), DEPLETED_1, DEPLETED_2);

            Movement movement = Movement.get(player);
            PlayerState state = movement.state();

            int totalActionStaminaCost = ((StaminaOverride) botWStamina).getTotalActionStaminaCost();
            int staminaDelta = state.staminaDelta();
            staminaDelta = (staminaDelta < 0) ? staminaDelta - totalActionStaminaCost : -totalActionStaminaCost;

            wheel.fill(0, maxStamina, EMPTY);
            if (depleted) {
                wheel.fill(0, stamina, color);
            }
            else {
                wheel.fill(0, stamina, IDLE);
                if (staminaDelta < 0){
                    wheel.fill(stamina + staminaDelta*10, stamina, color);
                }
            }
        }
        ci.cancel();
    }

//    @Inject(method = "makeWheel", at = @At(value = "HEAD"), remap=false)
//    public void makeWheel(PlayerMovement h, CallbackInfo ci) {
//        int totalActionStaminaCost = ((PlayerMovementInterface) h).getTotalActionStaminaCost();
//        int stamina = h.getStamina();
//
//        int maxStamina = h.getMaxStamina();
//        if (stamina >= maxStamina) {
//            long time = System.currentTimeMillis();
//            long timeDiff;
//            if (this.prevStamina != stamina) {
//                this.prevStamina = stamina;
//                this.fullTime = time;
//                timeDiff = 0L;
//            } else {
//                timeDiff = time - this.fullTime;
//            }
//
//            Color color = StaminaWheelConstants.getGlowAndFadeColor(timeDiff);
//            if (color.alpha <= 0.0F) {
//                return;
//            }
//
//            StaminaWheelRenderer.WheelLevel[] var9 = StaminaWheelRenderer.WheelLevel.values();
//            int var10 = var9.length;
//
//            for(int var11 = 0; var11 < var10; ++var11) {
//                StaminaWheelRenderer.WheelLevel t = var9[var11];
//                this.addWheel(t, 0.0, t.getProportion(stamina), color);
//            }
//        } else {
//            this.prevStamina = stamina;
//            Color color = StaminaWheelConstants.DEPLETED_1.blend(StaminaWheelConstants.DEPLETED_2, StaminaWheelConstants.cycle(System.currentTimeMillis(), h.isDepleted() ? 600L : 300L));
//            PlayerState state = h.getState();
//            int stateChange = CalculateStaminaUtils.getModifiedStateChange(h.player, state);
//            stateChange = (state.isConsume()) ? stateChange - totalActionStaminaCost : -totalActionStaminaCost;
//            StaminaWheelRenderer.WheelLevel[] var14 = StaminaWheelRenderer.WheelLevel.values();
//            int var7 = var14.length;
//
//            for(int var15 = 0; var15 < var7; ++var15) {
//                StaminaWheelRenderer.WheelLevel t = var14[var15];
//                this.addWheel(t, 0.0, t.getProportion(maxStamina), StaminaWheelConstants.EMPTY);
//                if (h.isDepleted()) {
//                    this.addWheel(t, 0.0, t.getProportion(stamina), color);
//                } else {
//                    this.addWheel(t, 0.0, t.getProportion(stamina), StaminaWheelConstants.IDLE);
//                    if (((state.isConsume()
//                            && (state.isParagliding() ? ModCfg.paraglidingConsumesStamina() : ModCfg.runningConsumesStamina())))
//                            || totalActionStaminaCost > 0) {
//                        this.addWheel(t, t.getProportion(stamina + stateChange * 10), t.getProportion(stamina), color);
//                    }
//                }
//            }
//        }
//    }
}
