package dev.ftb.extendedexchange.network;

import dev.ftb.extendedexchange.ExtendedExchange;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "2";  // bump for any incompatible protocol changes
    private static final SimpleChannel NETWORK = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(ExtendedExchange.MOD_ID, "main_channel"))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();
    private static int det = 0;

    private static int nextId() {
        return det++;
    }

    public static void init() {
        registerMessage(PacketJEIGhost.class, PacketJEIGhost::toBytes, PacketJEIGhost::new, PacketJEIGhost::handle,
                NetworkDirection.PLAY_TO_SERVER);
        registerMessage(PacketClearRecipeCache.class, PacketClearRecipeCache::toBytes, PacketClearRecipeCache::new, PacketClearRecipeCache::handle,
                NetworkDirection.PLAY_TO_CLIENT);
        registerMessage(PacketGuiButton.class, PacketGuiButton::toBytes, PacketGuiButton::new, PacketGuiButton::handle,
                NetworkDirection.PLAY_TO_SERVER);
        registerMessage(PacketNotifyKnowledgeChange.class, PacketNotifyKnowledgeChange::toBytes, PacketNotifyKnowledgeChange::new, PacketNotifyKnowledgeChange::handle,
                NetworkDirection.PLAY_TO_CLIENT);
    }

    private static <MSG> void registerMessage(Class<MSG> messageType, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer, NetworkDirection direction) {
        NETWORK.registerMessage(nextId(), messageType, encoder, decoder, messageConsumer, Optional.of(direction));
    }

    public static void sendToPlayer(ServerPlayer sp, Object message) {
        NETWORK.send(PacketDistributor.PLAYER.with(() -> sp), message);
    }

    public static void sendToServer(Object message) {
        NETWORK.sendToServer(message);
    }

    public static void sendToAll(Object message) {
        NETWORK.send(PacketDistributor.ALL.noArg(), message);
    }
}
