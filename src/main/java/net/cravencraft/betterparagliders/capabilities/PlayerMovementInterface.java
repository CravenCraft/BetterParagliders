package net.cravencraft.betterparagliders.capabilities;

public interface PlayerMovementInterface {

    /**
     * TODO: Maybe test separating these into their own separate interfaces
     *       if this becomes too large?
     * ServerPlayerMovement
     */
    void setTotalActionStaminaCostServerSide(int totalActionStaminaCost);

    /**
     * PlayerMovement
     */
    int getTotalActionStaminaCost();
    void setTotalActionStaminaCost(int totalActionStaminaCost);

    /**
     * ClientPlayerMovement
     */
    void setTotalActionStaminaCostClientSide(int totalActionStaminaCost);
    void syncActionStaminaClientSide(boolean syncActionStamina);
}