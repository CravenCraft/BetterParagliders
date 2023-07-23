package net.cravencraft.betterparagliders.capabilities;

import net.cravencraft.betterparagliders.config.UpdatedModCfg;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import tictim.paraglider.capabilities.PlayerMovement;
import tictim.paraglider.capabilities.PlayerState;

public abstract class UpdatedPlayerMovement {

    public PlayerMovement playerMovement;
    public int totalActionStaminaCost;

    public UpdatedPlayerMovement(PlayerMovement playerMovement) {
        this.playerMovement = playerMovement;
    }

    public abstract void update();

    /**
     * Updates the stamina both server and client side. Simply drains stamina if the player is performing
     * an action that will drain it. Very similar to how Paragliders does it orginally.
     */
    public void updateStamina() {
        PlayerState state = playerMovement.getState();
        if (this.totalActionStaminaCost != 0 || state.isConsume() ) {
            playerMovement.setRecoveryDelay(playerMovement.RECOVERY_DELAY);

            //TODO: Account for sprinting and swimming. Should sum the two. Not just choose one or the other.
            int stateChange = (state.isConsume()) ? state.change() : -this.totalActionStaminaCost;
            if (!playerMovement.isDepleted() && ((state.isParagliding() ? UpdatedModCfg.paraglidingConsumesStamina() : UpdatedModCfg.runningConsumesStamina()) || this.totalActionStaminaCost != 0)) {
                // TODO: Double check this area if you're getting an increasing value with attacks.
                playerMovement.setStamina(Math.max(0, playerMovement.getStamina() + stateChange));
            }
        }
    }

    /**
     * Adds all the effects to be applied whenever the player's stamina is depleted.
     */
    protected void addEffects() {
        if(!playerMovement.player.isCreative()&&playerMovement.isDepleted()){
            playerMovement.player.addEffect(new MobEffectInstance(MobEffect.byId(18))); // Adds weakness
        }
    }
}