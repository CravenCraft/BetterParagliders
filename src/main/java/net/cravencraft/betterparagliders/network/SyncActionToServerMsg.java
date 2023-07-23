package net.cravencraft.betterparagliders.network;

import net.minecraft.network.FriendlyByteBuf;

/**
 * A message that contains a record of the total amount of stamina to drain from
 * the player. Sent from client to server.
 *
 * @param totalActionStaminaCost
 */
public record SyncActionToServerMsg(int totalActionStaminaCost) {
    public static SyncActionToServerMsg read(FriendlyByteBuf buffer) {
        return new SyncActionToServerMsg(buffer.readInt());
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(totalActionStaminaCost);
    }
}
