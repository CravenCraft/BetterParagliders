package net.cravencraft.betterparagliders.mixins;

import net.cravencraft.betterparagliders.BetterParaglidersMod;
import net.cravencraft.betterparagliders.capabilities.PlayerMovementInterface;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ShieldItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tictim.paraglider.capabilities.PlayerMovement;
import tictim.paraglider.capabilities.PlayerState;
import tictim.paraglider.capabilities.ServerPlayerMovement;

@Mixin(ServerPlayerMovement.class)
public abstract class ServerPlayerMovementMixin extends PlayerMovement implements PlayerMovementInterface {
    private int totalActionStaminaCost;

    public ServerPlayerMovementMixin(Player player) { super(player); }

    @Inject(method = "update", at = @At(value = "HEAD"),  remap=false)
    public void update(CallbackInfo ci) {

        checkShieldDisable();
        this.setTotalActionStaminaCost(this.totalActionStaminaCost);
    }

    public void setTotalActionStaminaCostServerSide(int totalActionStaminaCost) {
        this.totalActionStaminaCost = totalActionStaminaCost;
    }

    /**
     * Checks if the player is currently holding a shield item. If so, then the modifyShieldCooldown method is called
     * to determine what to do with the shield.
     */
    private void checkShieldDisable() {
        if (player.getOffhandItem().getItem() instanceof ShieldItem offhandShieldItem) {
            modifyShieldCooldown(offhandShieldItem);
        }
        else if (player.getMainHandItem().getItem() instanceof ShieldItem mainHandShieldItem) {
            modifyShieldCooldown(mainHandShieldItem);
        }
    }

    /**
     * Disables the shield cooldown UNLESS the player's current stamina is fully depleted. If the stamina is depleted,
     * then the cooldown is set to the amount of ticks remaining until the player's stamina is fully replenished. Has
     * some additional checks to ensure the shield cooldown time stays in sync with the stamina replenish time as well.
     *
     * @param shieldItem A main hand or offhand shield being held by the player
     */
    private void modifyShieldCooldown(ShieldItem shieldItem) {
        if (player.getCooldowns().isOnCooldown(shieldItem) && !this.isDepleted()) {
            BetterParaglidersMod.LOGGER.info("REMOVING COOLDOWN");
            player.getCooldowns().removeCooldown(shieldItem);
        }
        else if (this.isDepleted()) {
            int recoveryRate = PlayerState.IDLE.change();
            int currentRecoveredAmount = this.getStamina();
            float cooldownPercentage = player.getCooldowns().getCooldownPercent(shieldItem, 0.0F);
            int shieldRecoveryDelay = (int) (this.getMaxStamina() * (1 - cooldownPercentage));
            if (shieldRecoveryDelay > currentRecoveredAmount) {
                player.getCooldowns().addCooldown(shieldItem, (this.getMaxStamina() - currentRecoveredAmount) / recoveryRate);
            }
        }
    }
}