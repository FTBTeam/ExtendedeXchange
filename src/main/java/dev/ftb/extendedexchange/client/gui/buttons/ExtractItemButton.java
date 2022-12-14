package dev.ftb.extendedexchange.client.gui.buttons;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.extendedexchange.client.gui.EMCFormat;
import dev.ftb.extendedexchange.network.NetworkHandler;
import dev.ftb.extendedexchange.network.PacketGuiButton;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.lwjgl.opengl.GL11;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class ExtractItemButton extends EXButton {
    private final IKnowledgeProvider provider;
    private ItemStack item = ItemStack.EMPTY;

    public ExtractItemButton(int x, int y, IKnowledgeProvider provider) {
        super(x, y, 18, 18, b -> {});
        this.provider = provider;
    }

    @Override
    public void onPress() {
        if (!item.isEmpty()) {
            NetworkHandler.sendToServer(new PacketGuiButton("extract:" + item.getItem().getRegistryName().toString(), Screen.hasShiftDown()));
        }
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;

        if (!item.isEmpty()) {
            // draw number of items which could be extracted
            Font font = Minecraft.getInstance().font;
            Minecraft.getInstance().getItemRenderer().renderGuiItem(item, x, y);
            String label = getExtractionCountStr();
            poseStack.pushPose();
            poseStack.translate(x + 17, y + 12, 200d);
            poseStack.scale(0.5F, 0.5F, 0.5F);
            font.drawShadow(poseStack, label, -font.width(label), 0, 0xFFFFFFFF);
            poseStack.popPose();
        }
        if (isHoveredOrFocused()) {
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            fill(poseStack, x, y, x + width, y + height, 0x80FFFFFF);
            RenderSystem.disableBlend();
        }
    }

    private static final BigDecimal ONE_TENTH = BigDecimal.valueOf(1L, 1);
    private String getExtractionCountStr() {
        long emc = ProjectEAPI.getEMCProxy().getValue(item);
        if (emc == 0L) return "???"; // shouldn't happen, but...

        String label = "";
        BigDecimal d = new BigDecimal(provider.getEmc()).setScale(1, RoundingMode.DOWN)
                .divide(BigDecimal.valueOf(emc), RoundingMode.DOWN);
        if (d.compareTo(BigDecimal.ONE) >= 0) {
            label = EMCFormat.formatBigDecimal(d.setScale(0, RoundingMode.DOWN));
        } else if (d.compareTo(ONE_TENTH) >= 0) {
            label = d.toString();
        }
        return label;
    }

    @Override
    public void addTooltip(double mouseX, double mouseY, List<Component> curTip, boolean shift) {
        curTip.addAll(item.getTooltipLines(Minecraft.getInstance().player, Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL));
    }
}
