package net.cravencraft.betterparagliders.capabilities;
import net.cravencraft.betterparagliders.BetterParaglidersMod;
import net.cravencraft.betterparagliders.network.ModNet;
import net.cravencraft.betterparagliders.network.SyncActionToClientMsg;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ShieldItem;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.network.PacketDistributor;
import tictim.paraglider.ModCfg;
import tictim.paraglider.ParagliderMod;
import tictim.paraglider.capabilities.PlayerState;
import tictim.paraglider.capabilities.ServerPlayerMovement;

public class UpdatedServerPlayerMovement extends UpdatedPlayerMovement {

    //TODO: Probably make a utility class for this and the client one.
    public static UpdatedServerPlayerMovement instance;
    public ServerPlayerMovement serverPlayerMovement;
    private Player player;

    private boolean actionStaminaNeedsSync;

    public UpdatedServerPlayerMovement(ServerPlayerMovement serverPlayerMovement) {
        super(serverPlayerMovement);
        this.serverPlayerMovement = serverPlayerMovement;
        this.player = serverPlayerMovement.player;
        instance = this;
    }

    @Override
    public void update() {
        //TODO: Is this necessary?
        if(!serverPlayerMovement.player.isCreative()&&serverPlayerMovement.isDepleted()){
            serverPlayerMovement.player.addEffect(new MobEffectInstance(MobEffect.byId(18))); // Adds weakness
        }

        checkShieldDisable();
        syncActionStamina();
        addEffects();
        updateStamina();
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
            if (serverPlayerMovement.player instanceof ServerPlayer serverPlayer) {
                SyncActionToClientMsg msg = new SyncActionToClientMsg(this);
                if(ModCfg.traceMovementPacket()) ParagliderMod.LOGGER.debug("Sending packet {} to player {}", msg, this.serverPlayerMovement.player);
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
        if (player.getCooldowns().isOnCooldown(shieldItem) && !serverPlayerMovement.isDepleted()) {
            BetterParaglidersMod.LOGGER.info("REMOVING COOLDOWN");
            player.getCooldowns().removeCooldown(shieldItem);
        }
        else if (serverPlayerMovement.isDepleted()) {
            int recoveryRate = PlayerState.IDLE.change();
            int currentRecoveredAmount = serverPlayerMovement.getStamina();
            float cooldownPercentage = player.getCooldowns().getCooldownPercent(shieldItem, 0.0F);
            int shieldRecoveryDelay = (int) (serverPlayerMovement.getMaxStamina() * (1 - cooldownPercentage));
            if (shieldRecoveryDelay > currentRecoveredAmount) {
                player.getCooldowns().addCooldown(shieldItem, (serverPlayerMovement.getMaxStamina() - currentRecoveredAmount) / recoveryRate);
            }
        }
    }
}