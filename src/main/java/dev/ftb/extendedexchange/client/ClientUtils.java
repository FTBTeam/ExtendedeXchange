package dev.ftb.extendedexchange.client;

import dev.ftb.extendedexchange.client.gui.KnowledgeUpdateListener;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class ClientUtils {
    public static Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    public static void onknowledgeUpdate() {
        if (Minecraft.getInstance().screen instanceof KnowledgeUpdateListener k) {
            k.onKnowledgeUpdate();
        }
    }
}
