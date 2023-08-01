package net.cravencraft.betterparagliders.capabilities;

public interface PlayerMovementInterface {
    void setTotalActionStaminaCost(int totalActionStaminaCost);
    void setTotalActionStaminaCostServerSide(int totalActionStaminaCost);
    void setTotalActionStaminaCostClientSide(int totalActionStaminaCost);
    int getTotalActionStaminaCost();

    void calculateBlockStaminaCost(float amount);
}
