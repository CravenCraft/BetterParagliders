package net.cravencraft.betterparagliders.mixins.combatroll;

import com.mojang.blaze3d.vertex.PoseStack;
import net.combatroll.client.gui.HudRenderHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HudRenderHelper.class)
public abstract class HudRenderHelperMixin {

    @Inject(method = "render", at = @At(value = "HEAD"), cancellable = true, remap=false)
    private static void render(PoseStack matrixStack, float tickDelta, CallbackInfo ci) {
        ci.cancel();
    }
}
