package net.cravencraft.betterparagliders.client;

import net.cravencraft.betterparagliders.capabilities.UpdatedClientPlayerMovement;
import net.cravencraft.betterparagliders.config.UpdatedModCfg;
import tictim.paraglider.capabilities.PlayerMovement;
import tictim.paraglider.capabilities.PlayerState;
import tictim.paraglider.client.DisableStaminaRender;
import tictim.paraglider.client.StaminaWheelConstants;
import tictim.paraglider.client.StaminaWheelRenderer;
import tictim.paraglider.utils.Color;

import static tictim.paraglider.client.StaminaWheelConstants.*;

/**
 * Basically just a copy and paste of the original Paragliders class of the same name.
 * One small change is adding in a check for additional action stamina cost (i.e., attack, block, etc.).
 */
public class InGameStaminaWheelRenderer extends StaminaWheelRenderer implements DisableStaminaRender {
	private int prevStamina;
	private long fullTime;

	@Override protected void makeWheel(PlayerMovement pm) {
		UpdatedClientPlayerMovement clientPlayer = UpdatedClientPlayerMovement.instance;
		if (clientPlayer != null) {
			int stamina = clientPlayer.playerMovement.getStamina();
			int maxStamina = clientPlayer.playerMovement.getMaxStamina();

			if (stamina>=maxStamina) {
				long time = System.currentTimeMillis();
				long timeDiff;
				if (prevStamina!=stamina){
					prevStamina = stamina;
					fullTime = time;
					timeDiff = 0;
				}
				else timeDiff = time-fullTime;
				Color color = StaminaWheelConstants.getGlowAndFadeColor(timeDiff);
				if (color.alpha<=0) return;
				for (WheelLevel t : WheelLevel.values())
					addWheel(t, 0, t.getProportion(stamina), color);
			}
			else {
				prevStamina = stamina;
				Color color = DEPLETED_1.blend(DEPLETED_2, cycle(System.currentTimeMillis(), clientPlayer.playerMovement.isDepleted() ? DEPLETED_BLINK : BLINK));
				PlayerState state = clientPlayer.playerMovement.getState();
				// Account for action stamina cost in the state change.
				//TODO: Account for sprinting and swimming. Should sum the two. Not just choose one or the other.
				int stateChange = (state.isConsume()) ? state.change() : -clientPlayer.totalActionStaminaCost;
				for (WheelLevel t : WheelLevel.values()) {
					addWheel(t, 0, t.getProportion(maxStamina), EMPTY);
					if (clientPlayer.playerMovement.isDepleted()) {
						addWheel(t, 0, t.getProportion(stamina), color);
					} else {
						addWheel(t, 0, t.getProportion(stamina), IDLE);
						// Add the check for action stamina cost here as well.
						if (((state.isConsume() && (state.isParagliding() ? UpdatedModCfg.paraglidingConsumesStamina() : UpdatedModCfg.runningConsumesStamina())))
								|| clientPlayer.totalActionStaminaCost > 0) {
							addWheel(t, t.getProportion(stamina + stateChange * 10), t.getProportion(stamina), color);
						}
					}
				}
			}
		}
	}
}