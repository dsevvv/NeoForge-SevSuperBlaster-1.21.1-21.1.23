package ca.dsevvv.sevsuperblaster.menu.screen;

import ca.dsevvv.sevsuperblaster.SevSuperBlaster;
import ca.dsevvv.sevsuperblaster.menu.BlasterBenchMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class BlasterBenchScreen extends AbstractContainerScreen<BlasterBenchMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SevSuperBlaster.MODID, "textures/gui/container/blaster_bench_ui.png");

    private int simDmg;
    private int simExpl;
    private int simHeal;
    private float simSpd;
    private ItemStack stack;

    public BlasterBenchScreen(BlasterBenchMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        this.inventoryLabelY = 9999;    //pce
        this.titleLabelY = 9999;        //cya later
        this.imageHeight = 210;
        this.imageWidth = 186;
        this.simDmg = menu.getBlasterDamage();
        this.simExpl = menu.getBlasterExplosion();
        this.simHeal = menu.getBlasterHeal();
        this.simSpd = menu.getBlasterSpeed();
        this.stack = menu.blockEntity.inventory.getStackInSlot(0);
        super.init();
    }

    @Override
    protected void containerTick() {
        if(!menu.blockEntity.inventory.getStackInSlot(0).equals(stack)){
            simDmg = menu.getBlasterDamage();
            simExpl = menu.getBlasterExplosion();
            simHeal = menu.getBlasterHeal();
            simSpd = menu.getBlasterSpeed();
            stack = menu.blockEntity.inventory.getStackInSlot(0);
        }
        super.containerTick();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);

        if(!menu.isSuperBlasterInside())
            return;

        renderLevelUp(guiGraphics, mouseX, mouseY);
        renderLevelOrb(guiGraphics, mouseX, mouseY);
        renderConfirm(guiGraphics, mouseX, mouseY);
        renderDamageSlider(guiGraphics, mouseX, mouseY);
        renderExplosionSlider(guiGraphics, mouseX, mouseY);
        renderHealSlider(guiGraphics, mouseX, mouseY);
        renderHomingSlider(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(!menu.isSuperBlasterInside())
            return super.mouseClicked(mouseX, mouseY, button);

        clickLevelUp(mouseX, mouseY);
        clickSlider(mouseX, mouseY);
        clickConfirm(mouseX, mouseY);

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void clickLevelUp(double mouseX, double mouseY){
        int offsetX = 120;
        int offsetY = 10;
        int boxSize = 12;

        if(mouseX >= this.leftPos + offsetX && mouseX <= this.leftPos + offsetX + boxSize
        && mouseY >= this.topPos + offsetY && mouseY <= this.topPos + offsetY + boxSize){
            menu.levelUp();
        }
    }

    private void clickSlider(double mouseX, double mouseY){
        long mouseXNorm = Math.round(mouseX - this.leftPos - 26); //20 is starting pixel for position 0 of slider
        long sliderPosClicked = mouseXNorm / 14;

        if(sliderPosClicked > 10 || mouseX - this.leftPos < 26)
            return;

        //14 between start of each click notch
        //damage slider
        if(mouseY >= this.topPos + 60 && mouseY <= this.topPos + 74){
            playClick();
            simDmg = (int) sliderPosClicked;
        }
        //explosion size slider
        else if(mouseY >= this.topPos + 96 && mouseY <= this.topPos + 110){
            playClick();
            simExpl = (int) sliderPosClicked;
        }
        //heal slider
        else if(mouseY >= this.topPos + 132 && mouseY <= this.topPos + 146){
            playClick();
            simHeal = (int) sliderPosClicked;
        }
        //homing speed slider
        else if(mouseY >= this.topPos + 168 && mouseY <= this.topPos + 182){
            playClick();
            simSpd = sliderPosClicked / 10f;
        }
    }

    private void clickConfirm(double mouseX, double mouseY){
        int boxWidth = 54;
        int boxHeight = 16;
        int offsetX = 79;
        int offsetY = 189;

        if(getSimPower() != menu.getBlasterMaxPower())
            return;

        if(mouseX >= this.leftPos + offsetX && mouseX <= this.leftPos + offsetX + boxWidth
        && mouseY >= this.topPos + offsetY && mouseY <= this.topPos + offsetY + boxHeight){
            playClick();
            menu.updateBlaster(simDmg, simExpl, simHeal, simSpd);
        }
    }

    private void renderLevelUp(GuiGraphics guiGraphics, int mouseX, int mouseY){
        int offsetX = 120;
        int offsetY = 10;
        int boxSize = 12;

        if(!menu.playerUpgrade())
            return;

        //is mouse inside upgrade box?
        if(mouseX >= this.leftPos + offsetX && mouseX <= this.leftPos + offsetX + boxSize
        && mouseY >= this.topPos + offsetY && mouseY <= this.topPos + offsetY + boxSize){
            guiGraphics.blit(TEXTURE, this.leftPos + 120, this.topPos + 10, 14, 230, 12, 12);
        }
        else{
            guiGraphics.blit(TEXTURE, this.leftPos + 120, this.topPos + 10, 0, 230, 12, 12);
        }
    }

    private void renderLevelOrb(GuiGraphics guiGraphics, int mouseX, int mouseY){
        for(int i = 0; i < menu.getBlasterLevel(); i++){
            guiGraphics.blit(TEXTURE, this.leftPos + 78 + (i * 12), this.topPos + 30, 0, 243, 8, 8);
        }
    }

    private void renderDamageSlider(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawCenteredString(this.font, "Damage", this.leftPos + 92, this.topPos + 48, 0xFF0000);

        guiGraphics.drawCenteredString(this.font, String.valueOf(simDmg), this.leftPos + 164, this.topPos + 48, 0xFF0000);
        renderSlider(guiGraphics, mouseX, mouseY, 60, simDmg);
    }

    private void renderExplosionSlider(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawCenteredString(this.font, "Explosion Size", this.leftPos + 92, this.topPos + 84, 0xffd700);

        guiGraphics.drawCenteredString(this.font, String.valueOf(simExpl), this.leftPos + 164, this.topPos + 84, 0xffd700);
        renderSlider(guiGraphics, mouseX, mouseY, 96, simExpl);
    }

    private void renderHealSlider(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawCenteredString(this.font, "Heal on Kill", this.leftPos + 92, this.topPos + 120, 0x00ff2e);

        guiGraphics.drawCenteredString(this.font, String.valueOf(simHeal), this.leftPos + 164, this.topPos + 120, 0x00ff2e);
        renderSlider(guiGraphics, mouseX, mouseY, 132, simHeal);
    }

    private void renderHomingSlider(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawCenteredString(this.font, "Homing Speed",  this.leftPos + 92, this.topPos + 156, 0xe514e2);

        guiGraphics.drawCenteredString(this.font, String.valueOf(Math.round(simSpd * 10)), this.leftPos + 164, this.topPos + 156, 0xe514e2);
        renderSlider(guiGraphics, mouseX, mouseY, 168, Math.round(simSpd * 10));
    }

    private void renderConfirm(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int boxWidth = 54;
        int boxHeight = 16;
        int offsetX = 79;
        int offsetY = 189;

        if(getSimPower() < menu.getBlasterMaxPower()){
            guiGraphics.drawCenteredString(this.font, String.format("%d/%d", getSimPower(), menu.getBlasterMaxPower()), this.leftPos + 157, this.topPos + 193, 0xffd700);
        } else if (getSimPower() > menu.getBlasterMaxPower()) {
            guiGraphics.drawCenteredString(this.font, String.format("%d/%d", getSimPower(), menu.getBlasterMaxPower()), this.leftPos + 157, this.topPos + 193, 0xFF0000);
        } else if (getSimPower() == menu.getBlasterMaxPower()) {
            guiGraphics.drawCenteredString(this.font, String.format("%d/%d", getSimPower(), menu.getBlasterMaxPower()), this.leftPos + 157, this.topPos + 193, 0x2ff314);
        }

        //is mouse inside confirm box?
        if(mouseX >= this.leftPos + offsetX && mouseX <= this.leftPos + offsetX + boxWidth
        && mouseY >= this.topPos + offsetY && mouseY <= this.topPos + offsetY + boxHeight){
            guiGraphics.blit(TEXTURE, this.leftPos + offsetX, this.topPos + offsetY, 28,230, boxWidth, boxHeight);
        }
        else{
            //red or green confirm button
            if(getSimPower() < menu.getBlasterMaxPower()
            || getSimPower() > menu.getBlasterMaxPower()){
                guiGraphics.blit(TEXTURE, this.leftPos + offsetX, this.topPos + offsetY, 83,213, boxWidth, boxHeight);
            }
            else{
                guiGraphics.blit(TEXTURE, this.leftPos + offsetX, this.topPos + offsetY, 28,213, boxWidth, boxHeight);
            }
        }
    }

    private void renderSlider(GuiGraphics guiGraphics, int mouseX, int mouseY, int sliderY, int sliderLvl){
        int texX = 26 + (sliderLvl * 14);

        if(mouseY >=  this.topPos + sliderY && mouseY <= this.topPos + sliderY + 14
        && mouseX >= this.leftPos + texX    && mouseX <= this.leftPos + texX + 12){
            guiGraphics.blit(TEXTURE, this.leftPos + texX, this.topPos + sliderY, 14,213, 12, 15);
        }
        else{
            guiGraphics.blit(TEXTURE, this.leftPos + texX, this.topPos + sliderY, 0 ,213, 12, 15);
        }
    }

    private int getSimPower(){
        int simSpdInt = Math.round(simSpd * 10);
        return simDmg + simExpl + simHeal + simSpdInt;
    }

    private void playClick(){
        menu.player.level().playSound(menu.player, menu.blockEntity.getBlockPos(), SoundEvents.LEVER_CLICK, SoundSource.BLOCKS);
    }
}
