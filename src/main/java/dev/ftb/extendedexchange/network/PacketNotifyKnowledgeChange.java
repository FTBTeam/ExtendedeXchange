package dev.ftb.extendedexchange.network;

import dev.ftb.extendedexchange.client.ClientUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Received on: CLIENT
 * Sent by server when knowledge is changed, to force any open GUI to refresh itself
 */
public class PacketNotifyKnowledgeChange {
    public PacketNotifyKnowledgeChange() {
    }

    public PacketNotifyKnowledgeChange(FriendlyByteBuf buf) {
    }

    void toBytes(FriendlyByteBuf buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() == null) {
                ClientUtils.onknowledgeUpdate();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
