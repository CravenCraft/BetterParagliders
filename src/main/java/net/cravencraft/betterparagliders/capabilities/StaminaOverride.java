package net.cravencraft.betterparagliders.capabilities;

public interface StaminaOverride {
    /**
     * Interface for ServerBotWStaminaMixin and BotWStaminaMixin
     */
    void calculateMeleeStaminaCostServerSide(int comboCount);
    void calculateBlockStaminaCostServerSide(float blockedAmount);
    int getTotalActionStaminaCost();
    void setTotalActionStaminaCost(int totalActionStaminaCost);
}