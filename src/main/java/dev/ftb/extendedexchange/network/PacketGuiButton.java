package dev.ftb.extendedexchange.network;

import dev.ftb.extendedexchange.menu.IGuiButtonListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Received on: SERVER
 * Sent by client GUI's when an EXButton (which has a tag) is clicked
 */
public class PacketGuiButton {
    private final String tag;
    private final boolean shift;

    public PacketGuiButton(String tag, boolean shift) {
        this.tag = tag;
        this.shift = shift;
    }
    
    public PacketGuiButton(FriendlyByteBuf buf) {
        this.tag = buf.readUtf(256);
        this.shift = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(tag);
        buf.writeBoolean(shift);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null && player.containerMenu instanceof IGuiButtonListener l) {
                l.handleGUIButtonPress(tag, shift, player);
            }
        });
        ctx.get().setPacketHandled(true);    
    }
}
