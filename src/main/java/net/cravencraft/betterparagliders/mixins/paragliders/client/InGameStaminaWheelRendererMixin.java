package net.cravencraft.betterparagliders.mixins.paragliders.client;

import net.cravencraft.betterparagliders.capabilities.StaminaOverride;
import net.cravencraft.betterparagliders.utils.CalculateStaminaUtils;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tictim.paraglider.ParagliderUtils;
import tictim.paraglider.api.movement.Movement;
import tictim.paraglider.api.stamina.Stamina;
import tictim.paraglider.client.render.InGameStaminaWheelRenderer;
import tictim.paraglider.client.render.StaminaWheelConstants;
import tictim.paraglider.client.render.StaminaWheelRenderer;
import tictim.paraglider.forge.capability.PlayerMovementProvider;
import tictim.paraglider.impl.movement.PlayerMovement;
import tictim.paraglider.impl.stamina.BotWStamina;

@Mixin(InGameStaminaWheelRenderer.class)
public abstract class InGameStaminaWheelRendererMixin extends StaminaWheelRenderer {
    @Shadow private boolean full;
    @Shadow protected abstract void makeFullWheel(@NotNull Wheel wheel, int stamina);

    @Inject(at = @At("HEAD"), remap = false, cancellable = true, method = "makeWheel")
    private void betterCombatStaminaWheelSupport(Player player, Wheel wheel, CallbackInfo ci) {
        if (PlayerMovementProvider.of(player) != null) {
            BotWStamina botWStamina = ((BotWStamina) PlayerMovementProvider.of(player).stamina());
            Stamina s = Stamina.get(player);
            int maxStamina = s.maxStamina();
            int stamina = Math.min(maxStamina, s.stamina());
            if (stamina >= maxStamina) {
                this.makeFullWheel(wheel, stamina);
            } else {
                this.full = false;
                boolean depleted = s.isDepleted();
                Movement movement = Movement.get(player);
                int totalActionStaminaCost = ((StaminaOverride) botWStamina).getTotalActionStaminaCost();
                int staminaDelta = CalculateStaminaUtils.getModifiedStateChange((PlayerMovement) movement);

                if (CalculateStaminaUtils.getAdditionalMovementStaminaCost(movement.state().id().getPath())) {
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
}
