package com.github.takecx.remotecontrollermod.stages;

import com.github.takecx.remotecontrollermod.AgentEntity;
import com.github.takecx.remotecontrollermod.lists.BlockList;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

public class Stage1_4 extends BaseStage {
    public Stage1_4(String stageIDIn, Integer stageXIn, Integer stageYIn, Integer stageZIn) {
        super(stageIDIn, stageXIn, stageYIn, stageZIn);
    }

    @Override
    void ConstructStageContents(Vector3d referencePos, ServerWorld worldIn, AgentEntity myAgent) {
        ConstructGround(this.stageAreaVec,referencePos,worldIn);
        Vector3d agentStartPos = new Vector3d(referencePos.x + this.stageAreaVec.x / 2,referencePos.y + 2,referencePos.z + 8);
        AgentEntity.SummonAgent(agentStartPos,worldIn,myAgent);
        worldIn.setBlockState(new BlockPos(agentStartPos.x, agentStartPos.y,agentStartPos.z - 6), BlockList.StarBlock.getDefaultState());

        //
        for (int i = 0; i < 7; i++) {
            worldIn.setBlockState(new BlockPos(agentStartPos.x,agentStartPos.y - 1,agentStartPos.z - i), Blocks.COARSE_DIRT.getDefaultState());
        }
    }
}
