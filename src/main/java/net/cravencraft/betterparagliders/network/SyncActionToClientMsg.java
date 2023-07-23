package net.cravencraft.betterparagliders.network;

import net.cravencraft.betterparagliders.capabilities.UpdatedPlayerMovement;
import net.minecraft.network.FriendlyByteBuf;

/**
 * A message that contains a record of the total amount of stamina to drain from
 * the player. Sent from server to client.
 *
 * @param totalActionStaminaCost
 */
public record SyncActionToClientMsg(int totalActionStaminaCost) {
    public static SyncActionToClientMsg read(FriendlyByteBuf buffer){
        return new SyncActionToClientMsg(buffer.readInt());
    }

    public SyncActionToClientMsg(UpdatedPlayerMovement playerMovement){
        this(playerMovement.totalActionStaminaCost);
    }

    public void copyTo(UpdatedPlayerMovement playerMovement){
        playerMovement.totalActionStaminaCost = totalActionStaminaCost;
    }

    public void write(FriendlyByteBuf buffer){
        buffer.writeInt(totalActionStaminaCost);
    }
}
