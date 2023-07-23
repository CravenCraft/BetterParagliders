package net.cravencraft.betterparagliders.capabilities;
import net.bettercombat.api.AttackHand;
import net.bettercombat.logic.PlayerAttackHelper;
import net.bettercombat.logic.PlayerAttackProperties;
import net.cravencraft.betterparagliders.BetterParaglidersMod;
import net.cravencraft.betterparagliders.config.UpdatedModCfg;
import net.cravencraft.betterparagliders.network.ModNet;
import net.cravencraft.betterparagliders.network.SyncActionToServerMsg;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import tictim.paraglider.ParagliderMod;
import tictim.paraglider.capabilities.ClientPlayerMovement;

public class UpdatedClientPlayerMovement extends UpdatedPlayerMovement {

    public static final int BASE_ATTACK_DAMAGE = 5;
    public static UpdatedClientPlayerMovement instance;
    public ClientPlayerMovement clientPlayerMovement;
    //TODO: Need to figure out if I really need this when I implement other stamina draining skills.
    private int totalMeleeStaminaCost;
    private int totalBlockStaminaCost;
    private int comboCount;

    public UpdatedClientPlayerMovement(ClientPlayerMovement clientPlayerMovement) {
        super(clientPlayerMovement);
        this.clientPlayerMovement = clientPlayerMovement;
        this.comboCount = 0;
        instance = this;
    }

    /**
     * Updates the client player. Calls methods to check if the player is performing any kind of action that drains
     * stamina as well.
     */
    @Override
    public void update() {
        calculateTotalStaminaCost();
        updateStamina();
        addEffects();
    }

    /**
     * //TODO: ACCOUNT FOR SPRINTING. Probably need to do this in the UpdatedPlayerMovement class and the stamina wheel render class
     * Calculates the total stamina cost from all actions (melee and ranged attacks so far). If the action stamina cost
     * is greater than 0, then a message is sent to the server to update the server player.
     */
    private void calculateTotalStaminaCost() {
        // Where all stamina draining methods will go
        calculateMeleeStaminaCost();
        calculateRangeStaminaCost();
//        calculateBlockStaminaCost(event.getAmount());

        if (this.totalActionStaminaCost > 0) {
            this.totalActionStaminaCost--;
            ModNet.NET.sendToServer(new SyncActionToServerMsg(this.totalActionStaminaCost));
        }
    }

    /**
     * Calculates the total amount of stamina to drain if the player is performing a melee attack. First checks if the
     * player is holding a Better Combat compatible weapon, then checks the current combo state of the weapon to see
     * if the stamina needs to be updated.
     */
    private void calculateMeleeStaminaCost() {
        Player player = clientPlayerMovement.player;
        int currentCombo = ((PlayerAttackProperties) player).getComboCount();
        AttackHand attackHand = PlayerAttackHelper.getCurrentAttack(player, currentCombo);

        this.comboCount = (currentCombo == 0) ? currentCombo : this.comboCount;

        if (attackHand != null && currentCombo > 0 && currentCombo != this.comboCount) {

            this.comboCount = currentCombo;
            double attackDamage = 1;
            int tierLevel = 0;


            if (player.getMainHandItem().getItem() instanceof SwordItem swordItem) {
                attackDamage = swordItem.getDamage();
                tierLevel = (int) swordItem.getTier().getAttackDamageBonus();
            }
            else if (player.getMainHandItem().getItem() instanceof AxeItem axeItem) {
                attackDamage = axeItem.getAttackDamage();
                tierLevel = (int) axeItem.getTier().getAttackDamageBonus();
            }

            double damageTierFactor;
            switch (tierLevel) {
                case 1:
                case 2:
                    damageTierFactor = 7.0;
                    break;
                case 3:
                    damageTierFactor = 8.0;
                    break;
                case 4:
                    damageTierFactor = 9.0;
                    break;
                case 5:
                    damageTierFactor = 10.0;
                    break;
                default:
                    damageTierFactor = 5.0;

            }


            double damageFactor = attackDamage + 1;
            if (damageFactor < 5) {
                damageFactor = 5;
            }
            else if (damageFactor > damageTierFactor) {
                damageFactor = damageTierFactor;
            }

            double reachFactor = attackHand.attributes().attackRange();
            if (reachFactor > 3) {
                reachFactor = 3;
            }
            BetterParaglidersMod.LOGGER.info("REACH FACTOR: " + reachFactor);
            BetterParaglidersMod.LOGGER.info("DAMAGE FACTOR: " + damageFactor);

            this.totalActionStaminaCost = (int) ((damageFactor * reachFactor));
            BetterParaglidersMod.LOGGER.info("TOTAL STAMINA COST: " + this.totalActionStaminaCost);
//            this.totalActionStaminaCost += this.totalMeleeStaminaCost;
        }
    }

    private void calculateRangeStaminaCost() {
        Player player = clientPlayerMovement.player;
        if (player.getUseItem().getItem() instanceof  ProjectileWeaponItem projectileWeaponItem) {
            BetterParaglidersMod.LOGGER.info("WEAPON DAMAGE: " + player.getUseItem().getRarity());
            ParagliderMod.LOGGER.info( (int) player.getCurrentItemAttackStrengthDelay());


            // TODO: Def gonna make some static final vars
            this.totalActionStaminaCost = UpdatedModCfg.baseRangeStaminaCost();
        }
    }
}