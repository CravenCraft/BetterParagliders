package net.cravencraft.betterparagliders.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import net.combatroll.client.CombatRollClient;
import net.combatroll.client.gui.HudRenderHelper;
import net.cravencraft.betterparagliders.BetterParaglidersMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HudRenderHelper.class)
public abstract class HudRenderHelperMixin {

    @Inject(method = "render", at = @At(value = "HEAD"), cancellable = true, remap=false)
    private static void render(PoseStack matrixStack, float tickDelta, CallbackInfo ci) {
        var config = CombatRollClient.config;
//        BetterParaglidersMod.LOGGER.info("MIXIN ok");
        ci.cancel();
        if (!config.showHUDInCreative) {
            return;
        }
    }
}
