package net.cravencraft.betterparagliders.mixins.combatroll;

import net.combatroll.CombatRoll;
import net.combatroll.api.EntityAttributes_CombatRoll;
import net.combatroll.client.CombatRollClient;
import net.combatroll.client.RollManager;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RollManager.class)
public abstract class RollManagerMixin {

    @Shadow
    private int maxRolls;
    @Shadow
    private int availableRolls;
    @Shadow
    private int timeSinceLastRoll;
    @Shadow
    private int currentCooldownLength;
    @Shadow
    private int currentCooldownProgress;

    /**
     * A simple mixin that sets the max number of rolls to be a static 1. Multiple rolls are no longer necessary as
     * the total number of available rolls depends solely on the amount of stamina a player has.
     *
     * @param player
     * @param ci
     */
    @Inject(method = "tick", at = @At(value = "HEAD"), cancellable = true, remap=false)
    public void tick(LocalPlayer player, CallbackInfo ci) {
        maxRolls = 1;
        timeSinceLastRoll += 1;
        if (availableRolls < maxRolls) {
            currentCooldownProgress += 1;
            if (currentCooldownProgress >= currentCooldownLength) {
                rechargeRoll(player);
            }
        }
        if (availableRolls == maxRolls) {
            currentCooldownProgress = 0;
        }
        if (availableRolls > maxRolls) {
            availableRolls = maxRolls;
        }
        ci.cancel();
    }

    private void rechargeRoll(LocalPlayer player) {
        ++this.availableRolls;
        this.currentCooldownProgress = 0;
        this.updateCooldownLength(player);
        if (CombatRollClient.config.playCooldownSound) {
            SoundEvent cooldownReady = (SoundEvent) Registry.SOUND_EVENT.get(new ResourceLocation("combatroll:roll_cooldown_ready"));
            if (cooldownReady != null) {
                player.level.playLocalSound(player.getX(), player.getY(), player.getZ(), cooldownReady, SoundSource.PLAYERS, 1.0F, 1.0F, false);
            }
        }

    }

    /**
     * Method is modified from its original to make the default cooldown around half a second, and to factor in
     * armor value to the cooldown of a roll (think of fat rolling in Dark Souls).
     *
     * @param player
     */
    private void updateCooldownLength(LocalPlayer player) {
        int currentArmorValue = player.getArmorValue();
        float duration = CombatRoll.config.roll_cooldown * 0.125F; // Baseline of one roll every half second now.
        this.currentCooldownLength = (int)Math.round((double)(duration * 20.0F + currentArmorValue) * (20.0 / EntityAttributes_CombatRoll.getAttributeValue(player, EntityAttributes_CombatRoll.Type.RECHARGE)));
    }

}
