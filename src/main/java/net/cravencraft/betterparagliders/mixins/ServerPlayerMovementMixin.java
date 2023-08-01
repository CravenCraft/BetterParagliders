package net.cravencraft.betterparagliders.mixins;

import net.cravencraft.betterparagliders.BetterParaglidersMod;
import net.cravencraft.betterparagliders.attributes.BetterParaglidersAttributes;
import net.cravencraft.betterparagliders.capabilities.PlayerMovementInterface;
import net.cravencraft.betterparagliders.config.UpdatedModCfg;
import net.cravencraft.betterparagliders.network.ModNet;
import net.cravencraft.betterparagliders.network.SyncActionToClientMsg;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ShieldItem;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tictim.paraglider.ModCfg;
import tictim.paraglider.ParagliderMod;
import tictim.paraglider.capabilities.PlayerMovement;
import tictim.paraglider.capabilities.PlayerState;
import tictim.paraglider.capabilities.ServerPlayerMovement;

@Mixin(ServerPlayerMovement.class)
public abstract class ServerPlayerMovementMixin extends PlayerMovement implements PlayerMovementInterface {
    private boolean actionStaminaNeedsSync;
    private int totalActionStaminaCost;

    public ServerPlayerMovementMixin(Player player) {
        super(player);
    }

    @Inject(method = "update", at = @At(value = "HEAD"),  remap=false)
    public void update(CallbackInfo ci) {
//        //TODO: Is this necessary?
        if(!this.player.isCreative() && this.isDepleted()){
            this.player.addEffect(new MobEffectInstance(MobEffect.byId(18))); // Adds weakness
        }

        checkShieldDisable();
        syncActionStamina();
        this.setTotalActionStaminaCost(this.totalActionStaminaCost);
    }

    public void setTotalActionStaminaCostServerSide(int totalActionStaminaCost) {
        this.totalActionStaminaCost = totalActionStaminaCost;
    }

    /**
     * TODO: Tweak this a bit more to drain some more stamina based on enemy attack power.
     *       (Current reference: Zombie ~ 3 and Vindicator ~ 13)
     * Calculates the amount of stamina to drain from blocking an attack.
     *
     * @param amount
     */
    public void calculateBlockStaminaCost(float amount) {
        this.totalActionStaminaCost = (int) amount;
        this.actionStaminaNeedsSync = true;
    }

    /**
     * Syncs the totalActionStaminaCost from the server to the client if the player performs an action that needs
     * to be synced (blocking).
     */
    private void syncActionStamina() {
        if (actionStaminaNeedsSync) {
            if (this.player instanceof ServerPlayer serverPlayer) {
                BetterParaglidersMod.LOGGER.info("INSIDE BLOCK STAMINA COST");
                //TODO: Issue to fix
                SyncActionToClientMsg msg = new SyncActionToClientMsg(this.totalActionStaminaCost);
                if(ModCfg.traceMovementPacket()) ParagliderMod.LOGGER.debug("Sending packet {} to player {}", msg, this.player);
                ModNet.NET.send(PacketDistributor.PLAYER.with(() -> serverPlayer), msg);
                actionStaminaNeedsSync = false;
            }
        }
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

    private void initPlayerAttributes() {
        //TODO: We'll create an ATTRIBUTES LIST AND JUST ITERATE THROUGH THEM.
        AttributeInstance strengthAttribute = this.player.getAttribute(BetterParaglidersAttributes.STRENGTH_PENALTY.get());
        BetterParaglidersMod.LOGGER.info("STRENGTH ATTRIBUTE ON RESPAWN: " + strengthAttribute.getValue());
        BetterParaglidersMod.LOGGER.info("BASE VALUE: " + strengthAttribute.getBaseValue());
        BetterParaglidersMod.LOGGER.info("DEFAULT VALUE: " + strengthAttribute.getAttribute().getDefaultValue());
        BetterParaglidersMod.LOGGER.info("CURRENT STR VALUE: " + BetterParaglidersAttributes.currentStrengthPenalty);


        //TODO: This will all move into the Attribute class and iterate through a list.
        if (BetterParaglidersAttributes.currentStrengthPenalty == 0) {
            strengthAttribute.setBaseValue(UpdatedModCfg.strengthPenalty());
        }
        else {
            strengthAttribute.setBaseValue(BetterParaglidersAttributes.currentStrengthPenalty);
        }
//        else {
//            strengthAttribute.setBaseValue(BetterParaglidersAttributes.currentStrengthPenalty);
//        }
//        if (strengthAttribute.getValue() == 0 && UpdatedModCfg.strengthPenalty() > 0) {
//            BetterParaglidersMod.LOGGER.info("SETTING STR ATTRIBUTE");
//            strengthAttribute.setBaseValue(UpdatedModCfg.strengthPenalty());
//        }
    }
}
