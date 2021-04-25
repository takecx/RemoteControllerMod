package com.github.takecx.remotecontrollermod.stages;

import com.github.takecx.remotecontrollermod.AgentEntity;
import com.github.takecx.remotecontrollermod.lists.BlockList;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

public class Stage1_3 extends BaseStage{
    public Stage1_3(String stageIDIn, Integer stageXIn, Integer stageYIn, Integer stageZIn) {
        super(stageIDIn, stageXIn, stageYIn, stageZIn);
    }

    @Override
    void ConstructStageContents(Vector3d referencePos, ServerWorld worldIn, AgentEntity myAgent) {
        ConstructGround(this.stageAreaVec,referencePos,worldIn);
        Vector3d agentStartPos = new Vector3d(referencePos.x + this.stageAreaVec.x / 2,referencePos.y + 2,referencePos.z + 2);
        AgentEntity.SummonAgent(agentStartPos,worldIn,myAgent);
        worldIn.setBlockState(new BlockPos(agentStartPos.x + 2,agentStartPos.y,agentStartPos.z + 2), BlockList.StarBlock.getDefaultState());

        //
        worldIn.setBlockState(new BlockPos(agentStartPos.x,agentStartPos.y - 1,agentStartPos.z), Blocks.COARSE_DIRT.getDefaultState());
        worldIn.setBlockState(new BlockPos(agentStartPos.x,agentStartPos.y - 1,agentStartPos.z + 1), Blocks.COARSE_DIRT.getDefaultState());
        worldIn.setBlockState(new BlockPos(agentStartPos.x,agentStartPos.y - 1,agentStartPos.z + 2), Blocks.COARSE_DIRT.getDefaultState());
        worldIn.setBlockState(new BlockPos(agentStartPos.x + 1,agentStartPos.y - 1,agentStartPos.z + 2), Blocks.COARSE_DIRT.getDefaultState());
        worldIn.setBlockState(new BlockPos(agentStartPos.x + 2,agentStartPos.y - 1,agentStartPos.z + 2), Blocks.COARSE_DIRT.getDefaultState());

    }
}
