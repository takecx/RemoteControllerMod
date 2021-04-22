package com.github.takecx.remotecontrollermod;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * CreeperModel - Either Mojang or a mod author (Taken From Memory)
 * Created using Tabula 8.0.0
 */
@OnlyIn(Dist.CLIENT)
//public class AgentModel extends EntityModel {
public class AgentModel<T extends Entity> extends EntityModel<T> {
    public ModelRenderer body;
    public ModelRenderer l_b_leg;
    public ModelRenderer l_f_leg;
    public ModelRenderer r_f_leg;
    public ModelRenderer head;
    public ModelRenderer core;
    public ModelRenderer r_b_leg;

    public AgentModel() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.body = new ModelRenderer(this, 16, 16);
        this.body.setRotationPoint(0.0F, 6.0F, 0.0F);
        this.body.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.l_b_leg = new ModelRenderer(this, 0, 16);
        this.l_b_leg.setRotationPoint(2.0F, 18.0F, 4.0F);
        this.l_b_leg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.r_f_leg = new ModelRenderer(this, 0, 16);
        this.r_f_leg.setRotationPoint(-2.0F, 18.0F, -4.0F);
        this.r_f_leg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.core = new ModelRenderer(this, 32, 0);
        this.core.setRotationPoint(0.0F, 6.0F, 0.0F);
        this.core.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.5F, 0.5F, 0.5F);
        this.head = new ModelRenderer(this, 0, 0);
        this.head.setRotationPoint(0.0F, 6.0F, 0.0F);
        this.head.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.l_f_leg = new ModelRenderer(this, 0, 16);
        this.l_f_leg.setRotationPoint(2.0F, 18.0F, -4.0F);
        this.l_f_leg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.r_b_leg = new ModelRenderer(this, 0, 16);
        this.r_b_leg.setRotationPoint(-2.0F, 18.0F, 4.0F);
        this.r_b_leg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, 0.0F, 0.0F, 0.0F);
    }

//    @Override
//    public void setRotationAngles(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
//
//    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        ImmutableList.of(this.body, this.l_b_leg, this.r_f_leg, this.core, this.head, this.l_f_leg, this.r_b_leg).forEach((modelRenderer) -> {
            modelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
