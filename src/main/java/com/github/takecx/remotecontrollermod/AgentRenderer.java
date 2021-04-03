package com.github.takecx.remotecontrollermod;

//import net.minecraft.client.render.entity.EntityRenderDispatcher;
//import net.minecraft.client.render.entity.MobEntityRenderer;
//import net.minecraft.client.render.entity.model.CreeperEntityModel;
//import net.minecraft.util.Identifier;
//
//public class AgentRenderer extends MobEntityRenderer<AgentEntity, CreeperEntityModel<AgentEntity>> {
//    public AgentRenderer(EntityRenderDispatcher entityRenderDispatcher_1)
//    {
//        //モデルを指定
//        super(entityRenderDispatcher_1, new CreeperEntityModel<>(), 1);
//    }
//    @Override
//    public Identifier getTexture(AgentEntity entity) {
//        //テクスチャの場所を指定
//        return new Identifier("example", "textures/entity/example_entity.png"); //ここも以前のバージョン(1.15.2まで)とは違います。
//    }
//}

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
        return new ResourceLocation("remotecontrollermod", "textures/entity/agent.png" );
    }
}