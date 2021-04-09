package com.github.takecx.remotecontrollermod;

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
}