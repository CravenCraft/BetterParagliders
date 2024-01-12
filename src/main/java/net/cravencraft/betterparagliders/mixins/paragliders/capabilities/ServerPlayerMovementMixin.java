package net.cravencraft.betterparagliders.mixins.paragliders.capabilities;

import net.cravencraft.betterparagliders.capabilities.PlayerMovementInterface;
import net.cravencraft.betterparagliders.config.ServerConfig;
import net.cravencraft.betterparagliders.network.ModNet;
import net.cravencraft.betterparagliders.network.SyncActionToClientMsg;
import net.cravencraft.betterparagliders.utils.CalculateStaminaUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
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
import tictim.paraglider.capabilities.PlayerState;
import tictim.paraglider.capabilities.ServerPlayerMovement;

import java.util.List;

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

        addEffects();
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
     *
     * @param blockedDamage
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

    /**
     * Checks if the player is currently holding a shield item. If so, then the modifyShieldCooldown method is called
     * to determine what to do with the shield.
     */
    private void checkShieldDisable() {

        //TODO: Just make an OR?
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
    private void modifyShieldCooldown(Item shieldItem) {
        if (this.player.getOffhandItem().getItem().getDescriptionId().contains("shield")) {
            int recoveryRate = PlayerState.IDLE.change();
            int currentRecoveredAmount = this.getStamina();
            float cooldownPercentage = player.getCooldowns().getCooldownPercent(shieldItem, 0.0F);
            int shieldRecoveryDelay = (int) (this.getMaxStamina() * (1 - cooldownPercentage));
            if (shieldRecoveryDelay > currentRecoveredAmount) {
                player.getCooldowns().addCooldown(shieldItem, (this.getMaxStamina() - currentRecoveredAmount) / recoveryRate);
            }
        }

    }

    /**
     * Adds all the effects to be applied whenever the player's stamina is depleted.
     */
    protected void addEffects() {
        if(!this.player.isCreative() && this.isDepleted()) {
            checkShieldDisable();
            List<Integer> effects = ServerConfig.depletionEffectList();
            List<Integer> effectStrengths = ServerConfig.depletionEffectStrengthList();

            for (int i=0; i < effects.size(); i++) {
                int effectStrength;
                if (i >= effectStrengths.size()) {
                    effectStrength = 0;
                }
                else {
                    effectStrength = effectStrengths.get(i) - 1;
                }

                if (MobEffect.byId(effects.get(i)) != null) {
                    this.player.addEffect(new MobEffectInstance(MobEffect.byId(effects.get(i)), 0, effectStrength));
                }
                else {
                    if (this.player instanceof ServerPlayer serverPlayer) {
                        serverPlayer.displayClientMessage(Component.literal("Effect with ID " + effects.get(i) + " does not exist."), true);
                    }
                }

            }
        }
    }
}