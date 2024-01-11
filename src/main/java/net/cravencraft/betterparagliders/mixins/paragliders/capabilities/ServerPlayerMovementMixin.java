package net.cravencraft.betterparagliders.mixins.paragliders.capabilities;

import net.cravencraft.betterparagliders.BetterParaglidersMod;
import net.cravencraft.betterparagliders.attributes.BetterParaglidersAttributes;
import net.cravencraft.betterparagliders.capabilities.PlayerMovementInterface;
import net.cravencraft.betterparagliders.config.ConfigManager;
import net.cravencraft.betterparagliders.config.ServerConfig;
import net.cravencraft.betterparagliders.network.ModNet;
import net.cravencraft.betterparagliders.network.SyncActionToClientMsg;
import net.cravencraft.betterparagliders.utils.CalculateStaminaUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.ShieldItem;
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

        if(player.getUseItem().getItem() instanceof ProjectileWeaponItem projectileWeaponItem) {
            calculateRangedStaminaCost(projectileWeaponItem);
        }

        checkShieldDisable();

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
        BetterParaglidersMod.LOGGER.info("CURRENT COMBO: " + comboCount);
        this.totalActionStaminaCost = CalculateStaminaUtils.calculateMeleeStaminaCost((Player) this.player, comboCount);
        this.syncActionStamina = true;
    }

    public void calculateBlockStaminaCostServerSide(float blockedDamage) {
        int blockCost = Math.round((float)((blockedDamage * ConfigManager.SERVER_CONFIG.blockStaminaConsumption() + 10) - player.getAttributeValue(BetterParaglidersAttributes.BLOCK_STAMINA_REDUCTION.get())));
        this.totalActionStaminaCost = blockCost;
        this.syncActionStamina = true;
    }

    public void calculateRangedStaminaCost(ProjectileWeaponItem projectileWeaponItem) {
        this.totalActionStaminaCost = CalculateStaminaUtils.calculateRangeStaminaCost(this.player);
        this.syncActionStamina = true;
    }

    /**
     * Checks if the player is currently holding a shield item. If so, then the modifyShieldCooldown method is called
     * to determine what to do with the shield.
     */
    private void checkShieldDisable() {
        this.player.getUseItem().getItem();
        //TODO: Just make an OR?
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

    /**
     * Adds all the effects to be applied whenever the player's stamina is depleted.
     */
    protected void addEffects() {
        if(!this.player.isCreative() && this.isDepleted()) {
            ServerConfig serverConfig = ConfigManager.SERVER_CONFIG;
            List<Integer> effects = serverConfig.depletionEffectList();
            List<Integer> effectStrengths = serverConfig.depletionEffectStrengthList();

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