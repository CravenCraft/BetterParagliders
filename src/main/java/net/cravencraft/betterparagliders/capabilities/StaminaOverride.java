package net.cravencraft.betterparagliders.capabilities;

public interface StaminaOverride {


    /**
     * Interface for ServerBotWStaminaMixin and BotWStaminaMixin
     */
    void setTotalActionStaminaCostServerSide(int totalActionStaminaCost);
    void calculateMeleeStaminaCostServerSide(int comboCount);
    void setActionStaminaCost(int attackStaminaCost);
    void attacking(boolean isAttacking);
    void performingAction(boolean isPerformingAction);
    boolean isPerformingAction();
    boolean isAttacking();
    int getTotalActionStaminaCost();
    void setTotalActionStaminaCost(int totalActionStaminaCost);

    void setTotalActionStaminaCostClientSide(int totalActionStaminaCost);
}