package dev.ftb.extendedexchange.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.extendedexchange.ExtendedExchange;
import dev.ftb.extendedexchange.block.entity.AbstractEMCBlockEntity;
import dev.ftb.extendedexchange.client.gui.buttons.ArrowButton;
import dev.ftb.extendedexchange.client.gui.buttons.ExtractItemButton;
import dev.ftb.extendedexchange.client.gui.buttons.HighlightButton;
import dev.ftb.extendedexchange.config.ConfigHelper;
import dev.ftb.extendedexchange.menu.AbstractTableMenu;
import dev.ftb.extendedexchange.menu.StoneTableMenu;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.PECapabilities;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class StoneTableScreen extends AbstractEXScreen<StoneTableMenu, AbstractEMCBlockEntity> implements AbstractTableMenu.KnowledgeUpdater {
    private static final ResourceLocation TEXTURE = new ResourceLocation(ExtendedExchange.MOD_ID, "textures/gui/stone_table.png");

    // static so they persist across GUI invocations
    private static int staticPage = 0;
    private static String staticSearch = "";

    private final List<ItemStack> validItems = new ArrayList<>();
    private final List<ExtractItemButton> extractButtons = new ArrayList<>();
    private EditBox searchField;

    public StoneTableScreen(StoneTableMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);

        imageHeight = 217;

        menu.setKnowledgeUpdater(this);
    }

    public Rect2i searchFieldPos() {
        return new Rect2i(leftPos + 8, topPos + 7, 160, 11);
    }

    @Override
    protected void init() {
        super.init();

        Rect2i tb = searchFieldPos();
        searchField = new EditBox(font, tb.getX(), tb.getY(), tb.getWidth(), tb.getHeight(), TextComponent.EMPTY);
        searchField.setTextColor(0xFFFFFFFF);
        searchField.setTextColorUneditable(0xFF808080);
        searchField.setBordered(false);
        searchField.setMaxLength(35);
        searchField.setValue(staticSearch);
        if (ConfigHelper.client().general.searchType.get().autoselected) {
            setFocused(searchField);
        }
        addRenderableWidget(searchField);

        addRenderableWidget(new ArrowButton(leftPos + 7, topPos + 20, b -> changePage(false))
                .withTexture(TEXTURE, 196, 0));
        addRenderableWidget(new ArrowButton(leftPos + 151, topPos + 20, b -> changePage(true))
                .withTexture(TEXTURE, 215, 0));

        addRenderableWidget(new HighlightButton(leftPos + 9, topPos + 116).withTag("learn"));
        addRenderableWidget(new HighlightButton(leftPos + 153, topPos + 116).withTag("unlearn"));

        addRenderableWidget(new HighlightButton(leftPos + 80, topPos + 68).withTag("burn"));

        extractButtons.clear();
        addExtractButton(new ExtractItemButton(leftPos + 80, topPos + 28, menu.getProvider()));
        addExtractButton(new ExtractItemButton(leftPos + 110, topPos + 38, menu.getProvider()));
        addExtractButton(new ExtractItemButton(leftPos + 50, topPos + 38, menu.getProvider()));
        addExtractButton(new ExtractItemButton(leftPos + 120, topPos + 68, menu.getProvider()));
        addExtractButton(new ExtractItemButton(leftPos + 40, topPos + 68, menu.getProvider()));
        addExtractButton(new ExtractItemButton(leftPos + 110, topPos + 98, menu.getProvider()));
        addExtractButton(new ExtractItemButton(leftPos + 50, topPos + 98, menu.getProvider()));
        addExtractButton(new ExtractItemButton(leftPos + 80, topPos + 108, menu.getProvider()));

        updateValidItemList();
    }

    private void addExtractButton(ExtractItemButton extractItemButton) {
        extractButtons.add(extractItemButton);
        addRenderableWidget(extractItemButton);
    }

    @Override
    protected void containerTick() {
        if (!staticSearch.equals(searchField.getValue())) {
            staticSearch = searchField.getValue();
            staticPage = 0;
            updateValidItemList();
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        changePage(delta < 0);  // scroll down = forward a page
        return true;
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        Minecraft.getInstance().player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY).ifPresent(p -> {
            String s = EMCFormat.INSTANCE.format(p.getEmc());
            font.draw(poseStack, s, ((imageWidth - font.width(s)) / 2f),  -9f, 0xFFB5B5B5);
        });
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.minecraft.player.closeContainer();
        }
        if (this.searchField.keyPressed(keyCode, scanCode, modifiers) || this.searchField.canConsumeInput()) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected ResourceLocation getGuiTexture() {
        return TEXTURE;
    }

    @Override
    public void onKnowledgeUpdate() {
        staticPage = 0;
        staticSearch = "";
        searchField.setValue("");
        updateValidItemList();
    }

    private String trim(String s) {
        return ChatFormatting.stripFormatting(s).trim().toLowerCase(Locale.ROOT);
    }

    private void changePage(boolean forward) {
        if (forward) {
            if (staticPage < Math.ceil(validItems.size() / (float) extractButtons.size()) - 1) {
                staticPage++;
                updateDisplayedItems();
            }
        } else if (staticPage > 0) {
            staticPage--;
            updateDisplayedItems();
        }
    }

    private void updateValidItemList() {
        validItems.clear();

        String srchStr = trim(staticSearch);
        boolean mod = srchStr.startsWith("@");
        if (mod) {
            srchStr = srchStr.substring(1);
        }

        for (ItemInfo itemInfo : menu.getProvider().getKnowledge()) {
            ItemStack stack = itemInfo.createStack();
            if (menu.isItemValid(stack) && (srchStr.isEmpty() || mod ?
                            itemInfo.getItem().getRegistryName().getNamespace().startsWith(srchStr) :
                            StringUtils.contains(trim(stack.getDisplayName().getString()), srchStr)))
            {
                validItems.add(ProjectEAPI.getEMCProxy().getPersistentInfo(itemInfo).createStack());
            }
        }

        Collections.reverse(validItems);
        updateDisplayedItems();
    }

    public void updateDisplayedItems() {
        for (int i = 0; i < extractButtons.size(); i++) {
            int index = i + staticPage * extractButtons.size();
            if (index >= 0 && index < validItems.size()) {
                extractButtons.get(i).setItem(validItems.get(index));
            } else {
                extractButtons.get(i).setItem(ItemStack.EMPTY);
            }
        }
    }
}
