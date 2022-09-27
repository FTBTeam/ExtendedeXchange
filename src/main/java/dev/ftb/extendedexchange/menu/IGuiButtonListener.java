package dev.ftb.extendedexchange.menu;

import net.minecraft.server.level.ServerPlayer;

/**
 * Implement on menus or block entities which can respond to a PacketGuiButton sent by a GUI button click
 */
public interface IGuiButtonListener {
    void handleGUIButtonPress(String tag, boolean shiftHeld, ServerPlayer player);
}
