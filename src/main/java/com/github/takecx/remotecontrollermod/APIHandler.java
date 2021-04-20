package com.github.takecx.remotecontrollermod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import net.minecraft.block.Blocks;

public class APIHandler {
    private ServerWorld myWorld = null;
    public static AgentEntity myAgent = null;

    // Commands
    protected static final String SUMMONAGENT = "agent.summon";
    protected static final String MOVEAGENT = "agent.move";
    protected static final String ROTATEAGENT = "agent.rotate";
    protected static final String PLAYERGETPOS = "player.getPos";
    protected static final String STARTSTAGE = "startStage";

    public APIHandler(){
        MinecraftServer currentServer = ServerLifecycleHooks.getCurrentServer();
        this.myWorld = currentServer.getWorld(World.OVERWORLD);
        this.searchAgentEntity();
    }

    private void searchAgentEntity(){
        for(Entity entity : this.myWorld.getEntitiesIteratable()){
            if(entity instanceof AgentEntity){
                this.myAgent = (AgentEntity) entity;
                break;
            }
        }
    }

    private void CheckScreen() throws InterruptedException {
        while(Remotecontrollermod.isShowingMenu){
            Thread.sleep(500);
        }
    }

    public Object Process(String commandStrIn) throws InterruptedException {
        if(this.myWorld.isRemote == false){
            this.CheckScreen();
            String[] contents = commandStrIn.split("\\(");
            String cmd = contents[0];
            String args = contents[1].length() != 1 ? contents[1].split("\\)")[0] : "";
            if (cmd.equals(SUMMONAGENT)){
                double x = Double.parseDouble(args.split(",")[0]);
                double y = Double.parseDouble(args.split(",")[1]);
                double z = Double.parseDouble(args.split(",")[2]);
                Vector3d agentPos = new Vector3d(x,y,z);
                SummonAgent(agentPos);
                return null;
            }
            else if(cmd.equals(MOVEAGENT)){
                double x = Double.parseDouble(args.split(",")[0]);
                double y = Double.parseDouble(args.split(",")[1]);
                double z = Double.parseDouble(args.split(",")[2]);
                Vector3d moveVal = new Vector3d(x,y,z);
                this.myAgent.move(MoverType.SELF,moveVal);
                return null;
            }
            else if(cmd.equals(ROTATEAGENT)){
                this.myAgent.setPositionAndRotationDirect(this.myAgent.getPosX() + 0.01D,this.myAgent.getPosY() + 0.01D,
                        this.myAgent.getPosZ() + 0.01D, Float.parseFloat(args),this.myAgent.rotationPitch,1,true);
                return null;
            }
            else if(cmd.equals(PLAYERGETPOS)){
                Vector3d playerPos = this.myWorld.getPlayers().get(0).getPositionVec();
                return playerPos.x + "," + playerPos.y + "," + playerPos.z;
            }
            else if(cmd.equals(STARTSTAGE)){
                StartStage(args);
                return null;
            }
            else{
                return null;
            }
        }else{
            return null;
        }
    }

    private void SummonAgent(Vector3d agentPosIn){
        if(this.myAgent == null || this.myAgent.removed){
            this.myAgent = new AgentEntity(Remotecontrollermod.AGENT,this.myWorld);
        }
        this.myAgent.setPosition(agentPosIn.x + 0.5D, agentPosIn.y,agentPosIn.z + 0.5D);
        if(!this.myAgent.isAddedToWorld()){
            boolean result = this.myWorld.addEntity(this.myAgent);
            if(result == false){
                System.out.println("Agent add fail!!");
            }
        }
    }

    private void StartStage(String stage) {
        Vector3d areaVec = new Vector3d(10,10,10);
        Vector3d referencePos = this.myAgent.getPositionVec();
        this.ClearStageArea(areaVec,referencePos);
        this.BuildAreaBoundary(areaVec,referencePos);

        BlockPos pos = new BlockPos(referencePos.x + 1,referencePos.y + 1,referencePos.z + 1);
        this.myWorld.setBlockState(pos, Blocks.LAVA.getDefaultState());

        BlockPos posTorch = new BlockPos(referencePos.x,referencePos.y + 2,referencePos.z);
        this.myWorld.setBlockState(pos,Blocks.TORCH.getDefaultState());
    }

    private void ClearStageArea(Vector3d areaVec, Vector3d referencePos){
        for (int i = 0; i < areaVec.x; i++) {
            for (int j = 0; j < areaVec.z; j++) {
                for (int k = 0; k < areaVec.y; k++) {
                    BlockPos pos = new BlockPos(referencePos.x + i,referencePos.y + k,referencePos.z + j);
                    this.myWorld.setBlockState(pos, Blocks.AIR.getDefaultState());
                }
            }
        }
    }

    private void BuildAreaBoundary(Vector3d areaVec, Vector3d referencePos){
        for (int i = 0; i < areaVec.x; i++) {
            for (int j = 0; j < areaVec.y; j++) {
                BlockPos pos1 = new BlockPos(referencePos.x + i,referencePos.y + j,referencePos.z - 1);
                this.myWorld.setBlockState(pos1, Blocks.GLASS.getDefaultState());
                BlockPos pos2 = new BlockPos(referencePos.x + i,referencePos.y + j,referencePos.z + areaVec.z);
                this.myWorld.setBlockState(pos2, Blocks.GLASS.getDefaultState());
            }
        }

        for (int i = 0; i < areaVec.x; i++) {
            for (int j = 0; j < areaVec.z; j++) {
                BlockPos pos1 = new BlockPos(referencePos.x + i,referencePos.y - 1,referencePos.z + j);
                this.myWorld.setBlockState(pos1, Blocks.GLASS.getDefaultState());
                BlockPos pos2 = new BlockPos(referencePos.x + i,referencePos.y + areaVec.y,referencePos.z + j);
                this.myWorld.setBlockState(pos2, Blocks.GLASS.getDefaultState());
            }
        }

        for (int i = 0; i < areaVec.y; i++) {
            for (int j = 0; j < areaVec.z; j++) {
                BlockPos pos1 = new BlockPos(referencePos.x - 1,referencePos.y + i,referencePos.z + j);
                this.myWorld.setBlockState(pos1, Blocks.GLASS.getDefaultState());
                BlockPos pos2 = new BlockPos(referencePos.x + areaVec.x,referencePos.y + i,referencePos.z + j);
                this.myWorld.setBlockState(pos2, Blocks.GLASS.getDefaultState());
            }
        }
    }
}
