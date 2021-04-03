package com.github.takecx.remotecontrollermod;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class AgentModel extends EntityModel {
    public ModelRenderer r_arm;
    public ModelRenderer r_leg;
    public ModelRenderer head;
    public ModelRenderer body;
    public ModelRenderer l_arm;
    public ModelRenderer l_leg;
    public ModelRenderer hat;

    public AgentModel() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.head = new ModelRenderer(this, 0, 0);
        this.head.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.head.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.body = new ModelRenderer(this, 16, 16);
        this.body.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.body.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.l_arm = new ModelRenderer(this, 40, 16);
        this.l_arm.mirror = true;
        this.l_arm.setRotationPoint(5.0F, 2.0F, 0.0F);
        this.l_arm.addBox(-1.0F, -2.0F, -1.0F, 4.0F, 12.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(l_arm, 0.0F, 0.0F, -0.10000736647217022F);
        this.r_leg = new ModelRenderer(this, 0, 16);
        this.r_leg.setRotationPoint(-2.0F, 12.0F, 0.1F);
        this.r_leg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.r_arm = new ModelRenderer(this, 40, 16);
        this.r_arm.setRotationPoint(-5.0F, 2.0F, 0.0F);
        this.r_arm.addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(r_arm, 0.0F, 0.0F, 0.10000736647217022F);
        this.hat = new ModelRenderer(this, 32, 0);
        this.hat.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.hat.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.5F, 0.5F, 0.5F);
        this.l_leg = new ModelRenderer(this, 0, 16);
        this.l_leg.mirror = true;
        this.l_leg.setRotationPoint(2.0F, 12.0F, 0.1F);
        this.l_leg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, 0.0F, 0.0F);
    }

    @Override
    public void setRotationAngles(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks,
                                  float netHeadYaw, float headPitch) {
        this.r_arm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.l_arm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * -1.4F * limbSwingAmount;
        this.r_leg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * -1.4F * limbSwingAmount;
        this.l_leg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn,
                       float red, float green, float blue, float alpha) {
        ImmutableList.of(this.head, this.body, this.l_arm, this.r_leg, this.r_arm, this.hat, this.l_leg).forEach((modelRenderer) -> {
            modelRenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        });
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}