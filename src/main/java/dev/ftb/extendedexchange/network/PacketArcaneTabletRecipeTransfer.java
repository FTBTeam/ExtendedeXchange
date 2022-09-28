package dev.ftb.extendedexchange.network;

import com.google.common.collect.ImmutableList;
import dev.ftb.extendedexchange.menu.ArcaneTabletMenu;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Received on: SERVER
 * Sent by client when transferring a recipe to the Arcane Tablet via JEI '+' button
 */
public class PacketArcaneTabletRecipeTransfer {
    private final Int2ObjectMap<List<ItemStack>> stacksMap;
    private final boolean maxTransfer;

    public PacketArcaneTabletRecipeTransfer(Int2ObjectMap<List<ItemStack>> stacksMap, boolean maxTransfer) {
        this.stacksMap = stacksMap;
        this.maxTransfer = maxTransfer;
    }

    public PacketArcaneTabletRecipeTransfer(FriendlyByteBuf buf) {
        this.stacksMap = new Int2ObjectOpenHashMap<>();
        int nSlots = buf.readVarInt();
        for (int i = 0; i < nSlots; i++) {
            int slot = buf.readVarInt();
            int nStacks = buf.readVarInt();
            List<ItemStack> stacks = IntStream.range(0, nStacks).mapToObj(j -> buf.readItem()).collect(Collectors.toList());
            stacksMap.put(slot, ImmutableList.copyOf(stacks));
        }
        this.maxTransfer = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeVarInt(stacksMap.size());
        stacksMap.forEach((index, stacks) -> {
            buf.writeVarInt(index);
            buf.writeVarInt(stacks.size());
            stacks.forEach(buf::writeItem);
        });
        buf.writeBoolean(maxTransfer);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null && player.containerMenu instanceof ArcaneTabletMenu tabletMenu) {
                tabletMenu.onRecipeTransfer(stacksMap, maxTransfer);
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
