package net.cravencraft.betterparagliders.mixins.paragliders.utils;

import net.cravencraft.betterparagliders.config.ConfigManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tictim.paraglider.ParagliderUtils;

import java.util.List;

@Mixin(ParagliderUtils.class)
public class ParagliderUtilsMixin {

    @Inject(at = @At("HEAD"), remap = false, method = "addExhaustion")
    private static void addMoreExhaustionEffects(LivingEntity entity, CallbackInfo ci) {
        List<Integer> effects = ConfigManager.SERVER_CONFIG.depletionEffectList();
        List<Integer> effectStrengths = ConfigManager.SERVER_CONFIG.depletionEffectStrengthList();

        for (int i=0; i < effects.size(); i++) {
            int effectStrength;
            if (i >= effectStrengths.size()) {
                effectStrength = 0;
            }
            else {
                effectStrength = effectStrengths.get(i) - 1;
            }

            if (MobEffect.byId(effects.get(i)) != null) {
                entity.addEffect(new MobEffectInstance(MobEffect.byId(effects.get(i)), 0, effectStrength));
            }
            else {
                if (entity instanceof ServerPlayer serverPlayer) {
                    serverPlayer.displayClientMessage(Component.literal("Effect with ID " + effects.get(i) + " does not exist."), true);
                }
            }

        }
    }

    @Inject(at = @At("HEAD"), remap = false, method = "removeExhaustion")
    private static void removeAllExhaustionEffects(LivingEntity entity, CallbackInfo ci) {
        List<Integer> effects = ConfigManager.SERVER_CONFIG.depletionEffectList();

        for (int i=0; i < effects.size(); i++) {
            MobEffect effect = MobEffect.byId(effects.get(i));

            if (entity.hasEffect(effect)) {
                entity.removeEffect(effect);
            }
        }
    }
}
