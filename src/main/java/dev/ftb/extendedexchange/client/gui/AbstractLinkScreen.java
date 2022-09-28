package dev.ftb.extendedexchange.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.extendedexchange.ExtendedExchange;
import dev.ftb.extendedexchange.block.entity.AbstractLinkInvBlockEntity;
import dev.ftb.extendedexchange.client.EXClientEventHandler;
import dev.ftb.extendedexchange.inventory.FilterSlot;
import dev.ftb.extendedexchange.menu.AbstractLinkMenu;
import moze_intel.projecte.api.capabilities.PECapabilities;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public abstract class AbstractLinkScreen<C extends AbstractLinkMenu<T>, T extends AbstractLinkInvBlockEntity> extends AbstractEXScreen<C,T> {
    public static final ResourceLocation TEXTURE_MK1 = new ResourceLocation(ExtendedExchange.MOD_ID, "textures/gui/personal_link.png");
    public static final ResourceLocation TEXTURE_MK2 = new ResourceLocation(ExtendedExchange.MOD_ID, "textures/gui/refined_link.png");
    public static final ResourceLocation TEXTURE_MK3 = new ResourceLocation(ExtendedExchange.MOD_ID, "textures/gui/compressed_refined_link.png");

    public AbstractLinkScreen(C menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        // TODO handle long overflow?
        long emc = Minecraft.getInstance().player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY)
                .map(p -> p.getEmc().longValue()).orElse(0L);

        font.draw(poseStack, menu.getBlockEntity().getOwnerName(), 8f, 6f, 0x404040);

        String s = EMCFormat.INSTANCE.format(emc);
        if (EXClientEventHandler.emcRate != 0D) {
            s += (EXClientEventHandler.emcRate > 0D ? (ChatFormatting.DARK_GREEN + "+") : (ChatFormatting.RED + "-")) + EMCFormat.INSTANCE.format(Math.abs(EXClientEventHandler.emcRate)) + "/s";
        }
        font.draw(poseStack, s, 8, getEMCLabelYPos(), 0x404040);
    }

    protected int getEMCLabelYPos() {
        return 73;
    }

    @Override
    public List<Component> getTooltipFromItem(ItemStack itemStack) {
        List<Component> l = super.getTooltipFromItem(itemStack);
        if (!itemStack.isEmpty() && getSlotUnderMouse() instanceof FilterSlot) {
            for (int i = 1; i <= 3; i++) {
                l.add(new TranslatableComponent("gui.extendedexchange.link.tooltip." + i).withStyle(ChatFormatting.GRAY));
            }
        }
        return l;
    }
}
