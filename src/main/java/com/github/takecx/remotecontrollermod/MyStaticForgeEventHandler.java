package com.github.takecx.remotecontrollermod;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.IngameMenuScreen;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class MyStaticForgeEventHandler {

    @SubscribeEvent
    public static void GuiOpen(GuiOpenEvent event) {
        System.out.println("GuiOpenEvent!");
        Remotecontrollermod.isShowingMenu = event.getGui() instanceof IngameMenuScreen;
    }

    @SubscribeEvent
    public static void onRenderGameOverlay(RenderGameOverlayEvent.Text event)
    {
        MatrixStack matrixStack = event.getMatrixStack();
        String scoreMsg = "Score : " + String.valueOf(APIHandler.myAgent.score);
        Minecraft.getInstance().fontRenderer.drawStringWithShadow(matrixStack, scoreMsg,170,200,0xffFF0000);
    }
}