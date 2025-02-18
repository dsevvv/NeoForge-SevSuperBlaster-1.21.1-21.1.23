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

        renderUpgrade(guiGraphics, mouseX, mouseY);
        renderLevel(guiGraphics, mouseX, mouseY);
        renderConfirm(guiGraphics, mouseX, mouseY);
        renderDamageSlider(guiGraphics, mouseX, mouseY);
        renderExplosionSlider(guiGraphics, mouseX, mouseY);
        renderHealSlider(guiGraphics, mouseX, mouseY);
        renderHomingSlider(guiGraphics, mouseX, mouseY);
    }

    private void renderUpgrade(GuiGraphics guiGraphics, int mouseX, int mouseY){
        if(!menu.isSuperBlasterInside())
            return;

        int offsetX = 120;
        int offsetY = 10;
        int boxSize = 12;

        //is mouse inside upgrade box?
        if(mouseX >= this.leftPos + offsetX && mouseX <= this.leftPos + offsetX + boxSize
        && mouseY >= this.topPos + offsetY && mouseY <= this.topPos + offsetY + boxSize){
            guiGraphics.blit(TEXTURE, this.leftPos + 120, this.topPos + 10, 14, 230, 12, 12);
        }
        else{
            guiGraphics.blit(TEXTURE, this.leftPos + 120, this.topPos + 10, 0, 230, 12, 12);
        }
    }

    private void renderLevel(GuiGraphics guiGraphics, int mouseX, int mouseY){
        if(!menu.isSuperBlasterInside())
            return;

        for(int i = 0; i < menu.getBlasterLevel(); i++){
            guiGraphics.blit(TEXTURE, this.leftPos + 78 + (i * 12), this.topPos + 30, 0, 243, 8, 8);
        }
    }

    private void renderDamageSlider(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if(!menu.isSuperBlasterInside())
            return;

        guiGraphics.drawCenteredString(this.font, "Damage", getGuiLeft() + 92, getGuiTop() + 48, 0xFF0000);

        if(menu.isSuperBlasterInside()){
            int dmg = menu.getBlasterDamage();
            guiGraphics.drawCenteredString(this.font, String.valueOf(dmg), this.leftPos + 164, this.topPos + 48, 0xFF0000);
        }
    }

    private void renderExplosionSlider(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if(!menu.isSuperBlasterInside())
            return;

        guiGraphics.drawCenteredString(this.font, "Explosion Size", getGuiLeft() + 92, getGuiTop() + 84, 0xffd700);

        if(menu.isSuperBlasterInside()){
            int expl = menu.getBlasterExplosion();
            guiGraphics.drawCenteredString(this.font, String.valueOf(expl), this.leftPos + 164, this.topPos + 84, 0xffd700);
        }
    }

    private void renderHealSlider(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if(!menu.isSuperBlasterInside())
            return;

        guiGraphics.drawCenteredString(this.font, "Heal on Kill", getGuiLeft() + 92, getGuiTop() + 120, 0x00ff2e);

        if(menu.isSuperBlasterInside()){
            int heal = menu.getBlasterHeal();
            guiGraphics.drawCenteredString(this.font, String.valueOf(heal), this.leftPos + 164, this.topPos + 120, 0x00ff2e);
        }
    }

    private void renderHomingSlider(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if(!menu.isSuperBlasterInside())
            return;

        guiGraphics.drawCenteredString(this.font, "Homing Speed", getGuiLeft() + 92, getGuiTop() + 156, 0xe514e2);

        if(menu.isSuperBlasterInside()){
            float homing = menu.getBlasterSpeed();
            guiGraphics.drawCenteredString(this.font, String.valueOf(homing), this.leftPos + 164, this.topPos + 156, 0xe514e2);
        }
    }

    private void renderConfirm(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int boxWidth = 53;
        int boxHeight = 16;
        int offsetX = 79;
        int offsetY = 189;

        if(menu.isSuperBlasterInside()){
            if(menu.getBlasterCurrentPower() < menu.getBlasterMaxPower()){
                guiGraphics.drawCenteredString(this.font, String.format("%d/%d", menu.getBlasterCurrentPower(), menu.getBlasterMaxPower()), this.leftPos + 157, this.topPos + 193, 0xffd700);
            } else if (menu.getBlasterCurrentPower() > menu.getBlasterMaxPower()) {
                guiGraphics.drawCenteredString(this.font, String.format("%d/%d", menu.getBlasterCurrentPower(), menu.getBlasterMaxPower()), this.leftPos + 157, this.topPos + 193, 0xFF0000);
            } else if (menu.getBlasterCurrentPower() == menu.getBlasterMaxPower()) {
                guiGraphics.drawCenteredString(this.font, String.format("%d/%d", menu.getBlasterCurrentPower(), menu.getBlasterMaxPower()), this.leftPos + 157, this.topPos + 193, 0x2ff314);
            }

            //is mouse inside confirm box?
            if(mouseX >= this.leftPos + offsetX && mouseX <= this.leftPos + offsetX + boxWidth
            && mouseY >= this.topPos + offsetY && mouseY <= this.topPos + offsetY + boxHeight){
                guiGraphics.blit(TEXTURE, this.leftPos + offsetX, this.topPos + offsetY, 28,230, boxWidth, boxHeight);
            }
            else{
                //red or green confirm button
                if(menu.getBlasterCurrentPower() < menu.getBlasterMaxPower()
                || menu.getBlasterCurrentPower() > menu.getBlasterMaxPower()){
                    guiGraphics.blit(TEXTURE, this.leftPos + offsetX, this.topPos + offsetY, 83,213, boxWidth, boxHeight);
                }
                else{
                    guiGraphics.blit(TEXTURE, this.leftPos + offsetX, this.topPos + offsetY, 28,213, boxWidth, boxHeight);
                }
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
