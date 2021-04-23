package com.github.takecx.remotecontrollermod;

import com.github.takecx.remotecontrollermod.lists.StageList;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class APIHandler {
    private ServerWorld myWorld = null;
    public static AgentEntity myAgent = null;

    // Commands
    protected static final String SUMMONAGENT = "agent.summon";
    protected static final String MOVEAGENT = "agent.move";
    protected static final String STEPFORWARDAGENT = "agent.stepForward";
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
                myAgent = (AgentEntity) entity;
                return;
            }
        }
        // if there is no AgentEntity, create one.
        myAgent = new AgentEntity(Remotecontrollermod.AGENT,this.myWorld);
    }

    private void CheckScreen() throws InterruptedException {
        while(Remotecontrollermod.isShowingMenu){
            Thread.sleep(500);
        }
    }

    public Object Process(String commandStrIn) throws Exception {
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
            else if(cmd.equals(STEPFORWARDAGENT)){
                Vector3d mov;
                float agentYaw = this.myAgent.rotationYaw % 360;
                if(agentYaw == 0){
                    mov = new Vector3d(0,0,1);
                }
                else if(agentYaw == 90){
                    mov = new Vector3d(-1,0,0);
                }
                else if(agentYaw == 180){
                    mov = new Vector3d(0,0,-1);
                }
                else if(agentYaw == 270){
                    mov = new Vector3d(1,0,0);
                }
                else{
                    throw new Exception("agent direction should be [0, 90, 180, 270]");
                }
                this.myAgent.move(MoverType.SELF,mov);
                Thread.sleep(500);
                return null;
            }
            else if(cmd.equals(ROTATEAGENT)){
                float angle = 0;
                if(args.equals("right")){
                    angle = 90;
                }else if(args.equals("left")){
                    angle = 270;
                }else if(args.equals("back")){
                    angle = 180;
                }
                this.myAgent.setPositionAndRotationDirect(this.myAgent.getPosX() + 0.01D,this.myAgent.getPosY() + 0.01D,
                        this.myAgent.getPosZ() + 0.01D, this.myAgent.rotationYaw + angle,this.myAgent.rotationPitch,
                        1,true);
                Thread.sleep(500);
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
        AgentEntity.SummonAgent(agentPosIn,this.myWorld,this.myAgent);
    }

    private void StartStage(String stage) {
        Vector3d playerPos = this.myWorld.getPlayers().get(0).getPositionVec();
        Vector3d referencePos = new Vector3d(Math.ceil(playerPos.x),Math.ceil(playerPos.y),Math.ceil(playerPos.z));
        StageList.Stages.get(stage).ConstructStage(referencePos,this.myWorld,this.myAgent);
    }

}
