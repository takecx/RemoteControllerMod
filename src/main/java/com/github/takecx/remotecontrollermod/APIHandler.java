package com.github.takecx.remotecontrollermod;

import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class APIHandler {
    private ServerWorld myWorld = null;
    private AgentEntity myAgent = null;

    // Commands
    protected static final String SUMMONAGENT = "agent.summon";
    protected static final String PLAYERGETPOS = "player.getPos";

    public APIHandler(){
        MinecraftServer currentServer = ServerLifecycleHooks.getCurrentServer();
        this.myWorld = currentServer.getWorld(World.OVERWORLD);
    }

    public Object Process(String commandStrIn){
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
        else if(cmd.equals(PLAYERGETPOS)){
            Vector3d playerPos = this.myWorld.getPlayers().get(0).getPositionVec();
            return playerPos.x + "," + playerPos.y + "," + playerPos.z;
        }
        else{
            return null;
        }
    }

    private void SummonAgent(Vector3d agentPosIn){
        if(this.myAgent == null){
            this.myAgent = new AgentEntity(Remotecontrollermod.AGENT,this.myWorld);
            boolean result = this.myWorld.addEntity(this.myAgent);
        }
        this.myAgent.setPosition(agentPosIn.x,agentPosIn.y,agentPosIn.z);
    }
}
