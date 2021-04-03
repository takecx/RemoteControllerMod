package com.github.takecx.remotecontrollermod;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.OwnerHurtByTargetGoal;
import net.minecraft.entity.ai.goal.OwnerHurtTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.entity.MoverType;

public class AgentEntity extends MobEntity {

    protected AgentEntity(EntityType<? extends MobEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    public void travel(Vector3d direction) {
        if (this.isServerWorld() || this.canPassengerSteer()) {
            // this.setMoveForward(3.0F);
            double speed = this.getAIMoveSpeed() * 0.3;

            Vector3d lookVector = this.getLookVec();
            Vector3d moveVector = lookVector.normalize().scale(speed);

            this.setMotion(this.getMotion().add(moveVector).scale(0.91));
            this.move(MoverType.SELF, this.getMotion());
        }
        this.updateLimbs();
    }

    private void updateLimbs() {
        this.prevLimbSwingAmount = this.limbSwingAmount;

        double deltaX = this.getPosX() - this.prevPosX;
        double deltaY = this.getPosY() - this.prevPosY;
        double deltaZ = this.getPosZ() - this.prevPosZ;

        float distance = MathHelper.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
        float moveAmount = Math.min(distance * 4.0F, 1.0F);

        this.limbSwingAmount += (moveAmount - this.limbSwingAmount) * 0.4F;
        this.limbSwing += this.limbSwingAmount;
    }

//    @Override
//    public AgeableEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
//        return null;
//    }

//    protected void registerGoals() {
//        this.goalSelector.addGoal(1, new SwimGoal(this));
//        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0D, true));
//        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
//        this.goalSelector.addGoal(8, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
//        this.goalSelector.addGoal(10, new LookAtGoal(this, PlayerEntity.class, 8.0F));
//        this.goalSelector.addGoal(10, new LookRandomlyGoal(this));
//        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
//        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
//        this.targetSelector.addGoal(3, (new HurtByTargetGoal(this)).setCallsForHelp());
//        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, AbstractSkeletonEntity.class, false));
//    }

//    public ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
////        if(!this.isTamed()) {
////            this.setInLove(player);
////            this.setTamedBy(player);
////        }
//        return ActionResultType.SUCCESS;
//    }

//    @Nullable
//    @Override
//    public AgeableEntity createChild(ServerWorld world, AgeableEntity mate) {
//        return null;
//    }
}