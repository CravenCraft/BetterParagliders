package net.cravencraft.betterparagliders.mixins.paragliders.stamina;

import net.cravencraft.betterparagliders.capabilities.StaminaOverride;
import net.cravencraft.betterparagliders.network.ModNet;
import net.cravencraft.betterparagliders.network.SyncActionToClientMsg;
import net.cravencraft.betterparagliders.utils.CalculateStaminaUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tictim.paraglider.api.movement.Movement;
import tictim.paraglider.api.movement.ParagliderPlayerStates;
import tictim.paraglider.api.vessel.VesselContainer;
import tictim.paraglider.impl.movement.ServerPlayerMovement;
import tictim.paraglider.impl.stamina.BotWStamina;
import tictim.paraglider.impl.stamina.ServerBotWStamina;

@Mixin(ServerBotWStamina.class)
public abstract class ServerBotWStaminaMixin extends BotWStamina implements StaminaOverride {
    private Player player;
    private ServerPlayerMovement serverPlayerMovement;

    public ServerBotWStaminaMixin(@NotNull VesselContainer vessels) {
        super(vessels);
    }

    /**
     * Updates the server side player by doing a few things. If the player is attacking, then the amount of
     * stamina to drain will be calculated. If there is an overlap between a skill being used (e.g., rolling)
     * and an attack (e.g., rolling immediately after an attack to cancel the animation), then that will be
     * accounted for in the total stamina cost so that there isn't any uneven amount of stamina drained.
     *
     * @param movement
     * @param ci
     */
    @Inject(at = @At("HEAD"), remap = false, cancellable = true, method = "update")
    private void updateServerSideMovement(Movement movement, CallbackInfo ci) {
        if (movement instanceof ServerPlayerMovement serverPlayerMovement) {

            this.serverPlayerMovement = serverPlayerMovement;
            this.player = serverPlayerMovement.player();

            if(player.getUseItem().getItem() instanceof ProjectileWeaponItem ||
                    CalculateStaminaUtils.DATAPACK_RANGED_STAMINA_OVERRIDES.containsKey(player.getUseItem().getItem().getDescriptionId().replace("item.", ""))) {
                calculateRangedStaminaCost();
            }
        }
    }

    /**
     * Calculates the total amount of stamina to drain if the player is performing a melee attack. First checks if the
     * player is holding a Better Combat compatible weapon, then checks the current combo state of the weapon to see
     * if the stamina needs to be updated.
     */
    public void calculateMeleeStaminaCostServerSide(int comboCount) {
        syncActionStamina(CalculateStaminaUtils.calculateMeleeStaminaCost(this.player, comboCount));
    }

    /**
     * Calculates the amount of stamina blocking an attack will cost.
     *
     * @param blockedDamage
     */
    public void calculateBlockStaminaCostServerSide(float blockedDamage) {
        syncActionStamina(CalculateStaminaUtils.calculateBlockStaminaCost(this.player, blockedDamage));
    }

    /**
     * Calculates the amount of stamina Shooting a bow or crossbow will cost.
     */
    public void calculateRangedStaminaCost() {
        syncActionStamina(CalculateStaminaUtils.calculateRangeStaminaCost(this.player));
    }

    private void syncActionStamina(int actionStaminaCost) {
        this.setTotalActionStaminaCost(actionStaminaCost);
        ModNet.NET.send(PacketDistributor.PLAYER.with(() -> serverPlayerMovement.player()), new SyncActionToClientMsg(actionStaminaCost));
    }

    /**
     * Checks if the player is currently holding a shield item. If so, then the modifyShieldCooldown method is called
     * to determine what to do with the shield.
     */
    private void checkShieldDisable() {

        if (this.player.getOffhandItem().getItem().getDescriptionId().contains("shield")) {
            modifyShieldCooldown(this.player.getOffhandItem().getItem());
        }
        else if (this.player.getMainHandItem().getItem().getDescriptionId().contains("shield")) {
            modifyShieldCooldown(this.player.getMainHandItem().getItem());
        }
    }

    /**
     * Disables the shield cooldown UNLESS the player's current stamina is fully depleted. If the stamina is depleted,
     * then the cooldown is set to the amount of ticks remaining until the player's stamina is fully replenished. Has
     * some additional checks to ensure the shield cooldown time stays in sync with the stamina replenish time as well.
     *
     * @param shieldItem A main hand or offhand shield being held by the player
     */
    private void modifyShieldCooldown( Item shieldItem) {
        if (this.player.getOffhandItem().getItem().getDescriptionId().contains("shield")) {
            int recoveryRate = ParagliderPlayerStates.RECOVERY_DELAY;
            int currentRecoveredAmount = this.serverPlayerMovement.stamina().stamina();
            float cooldownPercentage = player.getCooldowns().getCooldownPercent(shieldItem, 0.0F);
            int shieldRecoveryDelay = (int) (this.serverPlayerMovement.stamina().maxStamina() * (1 - cooldownPercentage));

            if (shieldRecoveryDelay > currentRecoveredAmount) {
                player.getCooldowns().addCooldown(shieldItem, (this.serverPlayerMovement.stamina().maxStamina() - currentRecoveredAmount) / recoveryRate);
            }
        }

    }
}