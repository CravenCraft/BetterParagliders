package net.cravencraft.betterparagliders.mixins.paragliders.client;

import net.cravencraft.betterparagliders.capabilities.StaminaOverride;
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

    @Shadow private long fullTime;

    @Shadow private boolean full;

    @Inject(at = @At("HEAD"), remap = false, cancellable = true, method = "makeWheel")
    private void betterCombatStaminaWheelSupport(Player player, Wheel wheel, CallbackInfo ci) {
        if (PlayerMovementProvider.of(player) != null) {
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
    }
}
