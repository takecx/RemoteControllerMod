package com.github.takecx.remotecontrollermod;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class AgentRenderer extends MobRenderer {

    public AgentRenderer(EntityRendererManager renderManagerIn, AgentModel entityModelIn, float shadowSizeIn) {
        super(renderManagerIn, entityModelIn, shadowSizeIn);
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return new ResourceLocation(Remotecontrollermod.MODID, "textures/entities/agent.png" );
    }
}