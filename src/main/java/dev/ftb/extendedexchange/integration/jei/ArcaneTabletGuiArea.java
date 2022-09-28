package dev.ftb.extendedexchange.integration.jei;

import dev.ftb.extendedexchange.client.gui.ArcaneTabletScreen;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.renderer.Rect2i;

import java.util.Collections;
import java.util.List;

public class ArcaneTabletGuiArea implements IGuiContainerHandler<ArcaneTabletScreen> {
    @Override
    public List<Rect2i> getGuiExtraAreas(ArcaneTabletScreen containerScreen) {
        return Collections.singletonList(new Rect2i(containerScreen.getGuiLeft() - 75, containerScreen.getGuiTop() + 10, 76, 89));
    }
}
