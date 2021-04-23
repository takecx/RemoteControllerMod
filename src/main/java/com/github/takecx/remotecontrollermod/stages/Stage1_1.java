package com.github.takecx.remotecontrollermod.stages;

import com.github.takecx.remotecontrollermod.AgentEntity;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

public class Stage1_1 extends BaseStage{

    public Stage1_1(String stageIDIn){
        super(stageIDIn);
    }

    @Override
    void ConstructStageContents(Vector3d areaVec, Vector3d referencePos, ServerWorld worldIn, AgentEntity myAgent) {
        ConstructGround(areaVec,referencePos,worldIn);
        Vector3d agentStartPos = new Vector3d(referencePos.x + areaVec.x / 2 - 1,referencePos.y + 2,referencePos.z + 2);
        AgentEntity.SummonAgent(agentStartPos,worldIn,myAgent);
    }
}
