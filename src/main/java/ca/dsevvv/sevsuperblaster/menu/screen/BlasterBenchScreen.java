package ca.dsevvv.sevsuperblaster.menu.screen;

import ca.dsevvv.sevsuperblaster.SevSuperBlaster;
import ca.dsevvv.sevsuperblaster.menu.BlasterBenchMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BlasterBenchScreen extends AbstractContainerScreen<BlasterBenchMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SevSuperBlaster.MODID, "textures/gui/container/blaster_bench_ui.png");

    public BlasterBenchScreen(BlasterBenchMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.inventoryLabelY = 10000;
        this.titleLabelY = 10000;
        this.imageHeight = 210;
        this.imageWidth = 186;
    }


    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
}
