package net.marcus.marcusmod.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.marcus.marcusmod.marcusmod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.awt.*;


public class crazyMachinescreen extends AbstractContainerScreen<crazyMachineMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(marcusmod.MOD_ID, "textures/gui/crazy.png");

    public crazyMachinescreen(crazyMachineMenu pMenu, Inventory pPlayerInventory, net.minecraft.network.chat.Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelX = 100000;
        this.inventoryLabelY = 100000;
    }

    public static final int imageWidth = 174;
    public static final int imageHeight = 165;

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 176, 205);

        renderProgressArrow(guiGraphics, x, y);

    }


    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        int arrowWidth = 22; // 箭头总宽度
        int progressWidth = menu.getScaledProgress(); // 动态宽度

        // 屏幕上绘制的位置（x 是左端固定，右端增长）
        int drawX = x + (arrowWidth - progressWidth);

        // 纹理上的采样位置（U 从右往左偏移）
        int drawU = 60 + (arrowWidth - progressWidth);
        if(menu.isCrafting()) {
            guiGraphics.blit(TEXTURE, x + 47, y + 25, 0, 166, menu.getScaledProgress(), 39, 176, 205);
            guiGraphics.blit(TEXTURE, drawX + 107, y + 25, drawU, 166, menu.getScaledProgress(), 39, 176, 205);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
