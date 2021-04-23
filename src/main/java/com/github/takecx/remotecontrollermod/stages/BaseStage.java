package com.github.takecx.remotecontrollermod.stages;

import com.github.takecx.remotecontrollermod.AgentEntity;
import com.github.takecx.remotecontrollermod.Remotecontrollermod;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

public abstract class BaseStage {
    public String stageID;

    public BaseStage(String stageIDIn){
        this.stageID = stageIDIn;
    }

    abstract void ConstructStageContents(Vector3d areaVec, Vector3d referencePos, ServerWorld worldIn, AgentEntity myAgent);

    public void ConstructStage(Vector3d areaVec, Vector3d referencePos, ServerWorld worldIn, AgentEntity myAgent){
        this.ClearStageArea(areaVec,referencePos,worldIn);
        this.BuildAreaBoundary(areaVec,referencePos,worldIn);

        this.ConstructStageContents(areaVec,referencePos,worldIn,myAgent);
    }
    private void ClearStageArea(Vector3d areaVec, Vector3d referencePos, ServerWorld worldIn){
        for (int i = 0; i < areaVec.x; i++) {
            for (int j = 0; j < areaVec.z; j++) {
                for (int k = 0; k < areaVec.y; k++) {
                    BlockPos pos = new BlockPos(referencePos.x + i,referencePos.y + k,referencePos.z + j);
                    worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
                }
            }
        }
    }

    private void BuildAreaBoundary(Vector3d areaVec, Vector3d referencePos, ServerWorld worldIn){
        for (int i = 0; i < areaVec.x; i++) {
            for (int j = 0; j < areaVec.y; j++) {
                BlockPos pos1 = new BlockPos(referencePos.x + i,referencePos.y + j,referencePos.z - 1);
                worldIn.setBlockState(pos1, Blocks.GLASS.getDefaultState());
                BlockPos pos2 = new BlockPos(referencePos.x + i,referencePos.y + j,referencePos.z + areaVec.z);
                worldIn.setBlockState(pos2, Blocks.GLASS.getDefaultState());
            }
        }

        for (int i = 0; i < areaVec.x; i++) {
            for (int j = 0; j < areaVec.z; j++) {
                BlockPos pos1 = new BlockPos(referencePos.x + i,referencePos.y - 1,referencePos.z + j);
                worldIn.setBlockState(pos1, Blocks.GLASS.getDefaultState());
                BlockPos pos2 = new BlockPos(referencePos.x + i,referencePos.y + areaVec.y,referencePos.z + j);
                worldIn.setBlockState(pos2, Blocks.GLASS.getDefaultState());
            }
        }

        for (int i = 0; i < areaVec.y; i++) {
            for (int j = 0; j < areaVec.z; j++) {
                BlockPos pos1 = new BlockPos(referencePos.x - 1,referencePos.y + i,referencePos.z + j);
                worldIn.setBlockState(pos1, Blocks.GLASS.getDefaultState());
                BlockPos pos2 = new BlockPos(referencePos.x + areaVec.x,referencePos.y + i,referencePos.z + j);
                worldIn.setBlockState(pos2, Blocks.GLASS.getDefaultState());
            }
        }
    }

    protected void ConstructGround(Vector3d areaVec, Vector3d referencePos, ServerWorld worldIn){
        for (int i = 0; i < areaVec.x; i++) {
            for (int j = 0; j < areaVec.z; j++) {
                BlockPos pos1 = new BlockPos(referencePos.x + i,referencePos.y,referencePos.z + j);
                worldIn.setBlockState(pos1, Blocks.DIRT.getDefaultState());
                BlockPos pos2 = new BlockPos(referencePos.x + i,referencePos.y + 1,referencePos.z + j);
                worldIn.setBlockState(pos2, Blocks.GRASS_BLOCK.getDefaultState());
            }
        }
    }
}
