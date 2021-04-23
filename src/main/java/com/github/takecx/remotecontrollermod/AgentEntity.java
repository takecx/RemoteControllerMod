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
import org.lwjgl.system.CallbackI;

public class AgentEntity extends MobEntity {

    public int score = 0;

    public AgentEntity(EntityType<? extends MobEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public static void SummonAgent(Vector3d referencePos, ServerWorld worldIn, AgentEntity myAgent) {
        if(myAgent == null || myAgent.removed){
            myAgent = new AgentEntity(Remotecontrollermod.AGENT,worldIn);
        }
        myAgent.setPositionAndRotationDirect(referencePos.x, referencePos.y,referencePos.z + 0.5D, 0 ,
                myAgent.rotationPitch,1,true);
        if(!myAgent.isAddedToWorld()){
            boolean result = worldIn.addEntity(myAgent);
            if(result == false){
                System.out.println("Agent add fail!!");
            }
        }
    }

    public void Happy() throws InterruptedException {
        Thread.sleep(500);
        move(MoverType.SELF,new Vector3d(0,1,0));
        Thread.sleep(500);
        move(MoverType.SELF,new Vector3d(0,1,0));
    }
}