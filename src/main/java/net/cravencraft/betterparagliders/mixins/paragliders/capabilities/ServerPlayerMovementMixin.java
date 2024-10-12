package net.cravencraft.betterparagliders.mixins.paragliders.capabilities;

import net.cravencraft.betterparagliders.capabilities.PlayerMovementInterface;
import net.cravencraft.betterparagliders.network.ModNet;
import net.cravencraft.betterparagliders.network.SyncActionToClientMsg;
import net.cravencraft.betterparagliders.utils.CalculateStaminaUtils;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tictim.paraglider.capabilities.PlayerMovement;
import tictim.paraglider.capabilities.ServerPlayerMovement;

@Mixin(ServerPlayerMovement.class)
public abstract class ServerPlayerMovementMixin extends PlayerMovement implements PlayerMovementInterface {
    @Shadow @Final private ServerPlayer serverPlayer;
    private int totalActionStaminaCost;
    private boolean syncActionStamina;

    public ServerPlayerMovementMixin(Player player) { super(player); }

    @Inject(method = "update", at = @At(value = "HEAD"),  remap=false)
    public void update(CallbackInfo ci) {

        if(player.getUseItem().getItem() instanceof ProjectileWeaponItem ||
                CalculateStaminaUtils.DATAPACK_RANGED_STAMINA_OVERRIDES.containsKey(player.getUseItem().getItem().getDescriptionId().replace("item.", ""))) {
            calculateRangedStaminaCost();
        }

        if (syncActionStamina) {
            ModNet.NET.send(PacketDistributor.PLAYER.with(() -> this.serverPlayer), new SyncActionToClientMsg(this.totalActionStaminaCost));
            this.syncActionStamina = false;
        }

        this.setTotalActionStaminaCost(this.totalActionStaminaCost);
    }

    public void setTotalActionStaminaCostServerSide(int totalActionStaminaCost) {
        this.totalActionStaminaCost = totalActionStaminaCost;
    }

    /**
     * Calculates the total amount of stamina to drain if the player is performing a melee attack. First checks if the
     * player is holding a Better Combat compatible weapon, then checks the current combo state of the weapon to see
     * if the stamina needs to be updated.
     */
    public void calculateMeleeStaminaCostServerSide(int comboCount) {
        this.totalActionStaminaCost = CalculateStaminaUtils.calculateMeleeStaminaCost(this.player, comboCount);
        this.syncActionStamina = true;
    }

    /**
     * Calculates the amount of stamina blocking an attack will cost.
     */
    public void calculateBlockStaminaCostServerSide(float blockedDamage) {
        this.totalActionStaminaCost = CalculateStaminaUtils.calculateBlockStaminaCost(this.player, blockedDamage);
        this.syncActionStamina = true;
    }

    /**
     * Calculates the amount of stamina Shooting a bow or crossbow will cost.
     */
    public void calculateRangedStaminaCost() {
        this.totalActionStaminaCost = CalculateStaminaUtils.calculateRangeStaminaCost(this.player);
        this.syncActionStamina = true;
    }
}