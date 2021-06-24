package com.github.takecx.remotecontrollermod;

import com.github.takecx.remotecontrollermod.lists.StageList;
import com.github.takecx.remotecontrollermod.messages.MoveCameraMessageToClient;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class APIHandler {
    private ServerWorld myWorld = null;
    public static AgentEntity myAgent = null;

    // Agent
    protected static final String SUMMONAGENT = "agent.summon";
    protected static final String MOVEAGENT = "agent.move";
    protected static final String STEPFORWARDAGENT = "agent.stepForward";
    protected static final String JUMPFORWARDAGENT = "agent.jumpForward";
    protected static final String ROTATEAGENT = "agent.rotate";
    protected static final String PLAYERGETPOS = "player.getPos";
    protected static final String STARTSTAGE = "startStage";
    protected static final String MOVECAMERA = "moveCamera";

    // Commands
    protected static final String WORLDGETPLAYERIDS = "world.getPlayerIds";
    protected static final String GETBLOCKWITHDATA = "world.getBlockWithData";
    protected static final String SETBLOCK = "world.setBlock";
    protected static final String SETBLOCKS = "world.setBlocks";
    protected static final String WORLDSPAWNENTITY = "world.spawnEntity";
    protected static final String WORLDCHANGEWEATHER = "world.changeWeather";
    protected static final String WORLDCHANGEGAMEMODE = "world.changeGameMode";
    protected static final String WORLDCHANGEDIFFICULTY = "world.changeDifficulty";
    protected static final String WORLDSPAWNPARTICLE = "world.spawnParticle";
    protected static final String ENTITYGETPOS = "entity.getPos";
    protected static final String ENTITYSETPOS = "entity.setPos";
    protected static final String CHAT = "chat.post";
    protected static final String GIVEENCHANT = "giveEnchant";

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
        if(this.myWorld.isRemote == false) {
            this.CheckScreen();
            String[] contents = commandStrIn.split("\\(");
            String cmd = contents[0];
            String args = contents[1].length() != 1 ? contents[1].split("\\)")[0] : "";
            if (cmd.equals(SUMMONAGENT)) {
                double x = Double.parseDouble(args.split(",")[0]);
                double y = Double.parseDouble(args.split(",")[1]);
                double z = Double.parseDouble(args.split(",")[2]);
                Vector3d agentPos = new Vector3d(x, y, z);
                SummonAgent(agentPos);
                return null;
            } else if (cmd.equals(MOVEAGENT)) {
                double x = Double.parseDouble(args.split(",")[0]);
                double y = Double.parseDouble(args.split(",")[1]);
                double z = Double.parseDouble(args.split(",")[2]);
                Vector3d moveVal = new Vector3d(x, y, z);
                this.myAgent.move(MoverType.SELF, moveVal);
                return null;
            } else if (cmd.equals(STEPFORWARDAGENT)) {
                Vector3d mov;
                float agentYaw = this.myAgent.rotationYaw % 360;
                if (agentYaw == 0) {
                    mov = new Vector3d(0, 0, 1);
                } else if (agentYaw == 90) {
                    mov = new Vector3d(-1, 0, 0);
                } else if (agentYaw == 180) {
                    mov = new Vector3d(0, 0, -1);
                } else if (agentYaw == 270) {
                    mov = new Vector3d(1, 0, 0);
                } else {
                    throw new Exception("agent direction should be [0, 90, 180, 270]");
                }
                this.myAgent.move(MoverType.SELF, mov);
                Thread.sleep(500);
                return null;
            } else if (cmd.equals(JUMPFORWARDAGENT)) {
                Vector3d mov;
                float agentYaw = this.myAgent.rotationYaw % 360;
                if (agentYaw == 0) {
                    mov = new Vector3d(0, 1, 1);
                } else if (agentYaw == 90) {
                    mov = new Vector3d(-1, 1, 0);
                } else if (agentYaw == 180) {
                    mov = new Vector3d(0, 1, -1);
                } else if (agentYaw == 270) {
                    mov = new Vector3d(1, 1, 0);
                } else {
                    throw new Exception("agent direction should be [0, 90, 180, 270]");
                }
                this.myAgent.move(MoverType.SELF, mov);
                Thread.sleep(500);
                return null;

            } else if (cmd.equals(ROTATEAGENT)) {
                float angle = 0;
                if (args.equals("right")) {
                    angle = 90;
                } else if (args.equals("left")) {
                    angle = 270;
                } else if (args.equals("back")) {
                    angle = 180;
                }
                this.myAgent.setPositionAndRotationDirect(this.myAgent.getPosX() + 0.01D, this.myAgent.getPosY() + 0.01D,
                        this.myAgent.getPosZ() + 0.01D, this.myAgent.rotationYaw + angle, this.myAgent.rotationPitch,
                        1, true);
                Thread.sleep(500);
                return null;
            } else if (cmd.equals(STARTSTAGE)) {
                StartStage(args);
                return null;
            } else if (cmd.equals(PLAYERGETPOS)) {
                Vector3d playerPos = this.myWorld.getPlayers().get(0).getPositionVec();
                return playerPos.x + "," + playerPos.y + "," + playerPos.z;
            }else if (cmd.equals(MOVECAMERA)) {
                Vector3d arg = new Vector3d(0.5F, 0.5F, 0.5F);
                MoveCameraMessageToClient moveCameraMessageToClient = new MoveCameraMessageToClient(arg);
                Remotecontrollermod.simpleChannel.send(PacketDistributor.ALL.noArg(), moveCameraMessageToClient);
                return null;
            }
            else if (cmd.equals(WORLDGETPLAYERIDS)) {
                List<Integer> players = new ArrayList<Integer>();
                for (PlayerEntity p : myWorld.getPlayers()) {
                    players.add(p.getEntityId());
                }
                Collections.sort(players);

                StringBuilder ids = new StringBuilder();
                for (Integer id : players) {
                    if (ids.length() > 0)
                        ids.append("|");
                    ids.append(id);
                }
                return ids.toString();
            }
            else if (cmd.equals(GETBLOCKWITHDATA)) {
                String[] coords = args.split(",");
                BlockPos targetPos = new BlockPos(Integer.parseInt(coords[0]),Integer.parseInt(coords[1]),Integer.parseInt(coords[2]));
                BlockState targetState = this.myWorld.getBlockState(targetPos);
                return targetState.getBlock().getTranslationKey();
            }
            else if (cmd.equals(SETBLOCK)) {
                String[] arg_content = args.split(",");
                BlockPos targetPos = new BlockPos(Integer.parseInt(arg_content[0]), Integer.parseInt(arg_content[1]), Integer.parseInt(arg_content[2]));
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(arg_content[5]));
                assert block != null;
                myWorld.setBlockState(targetPos, block.getDefaultState());
                return null;
            }
            else if (cmd.equals(SETBLOCKS)) {
                String[] arg_content = args.split(",");
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(arg_content[8]));
                assert block != null;
                int startX = Integer.parseInt(arg_content[0]);
                int startY = Integer.parseInt(arg_content[1]);
                int startZ = Integer.parseInt(arg_content[2]);
                int endX = Integer.parseInt(arg_content[3]);
                int endY = Integer.parseInt(arg_content[4]);
                int endZ = Integer.parseInt(arg_content[5]);
                for (int x = startX; x < endX + 1; x++) {
                    for (int y = startY; y < endY + 1; y++) {
                        for (int z = startZ; z < endZ + 1; z++) {
                            BlockPos targetPos = new BlockPos(x,y,z);
                            myWorld.setBlockState(targetPos, block.getDefaultState());
                        }
                    }
                }
                return null;
            }
            else if (cmd.equals(WORLDSPAWNENTITY)) {
                String[] arg_content = args.split(",");
                BlockPos targetPos = new BlockPos(Integer.parseInt(arg_content[1]), Integer.parseInt(arg_content[2]), Integer.parseInt(arg_content[3]));
                Optional<EntityType<?>> targetEntity = EntityType.byKey(arg_content[0]);
                targetEntity.ifPresent(entityType -> {
                    Entity entity = entityType.create(myWorld);
                    assert entity != null;
                    entity.setPosition(targetPos.getX(), targetPos.getY(), targetPos.getZ());
                    myWorld.addEntity(entity);
                });
                return null;
            }
            else if (cmd.equals(WORLDCHANGEWEATHER)) {
                int weatherTime = 600;
                if(args.equals("clear")){
                    myWorld.setWeather(weatherTime, 0, false, false);
                }else if(args.equals("rain")){
                    myWorld.setWeather(0, weatherTime, true, false);
                }else if(args.equals("thunder")){
                    myWorld.setWeather(0, weatherTime, true, true);
                }
                return null;
            }
            else if (cmd.equals(WORLDCHANGEGAMEMODE)) {
                if (args.equals(String.valueOf(GameType.SURVIVAL.getID()))) {
                    ChangeGameType(GameType.SURVIVAL);
                } else if (args.equals(String.valueOf(GameType.CREATIVE.getID()))) {
                    ChangeGameType(GameType.CREATIVE);
                } else if (args.equals(String.valueOf(GameType.ADVENTURE.getID()))) {
                    ChangeGameType(GameType.ADVENTURE);
                } else if (args.equals(String.valueOf(GameType.SPECTATOR.getID()))) {
                    ChangeGameType(GameType.SPECTATOR);
                }
            }
            else if (cmd.equals(WORLDCHANGEDIFFICULTY)) {
                if(args.equals(String.valueOf(Difficulty.PEACEFUL.getId()))){
                    myWorld.getServer().setDifficultyForAllWorlds(Difficulty.PEACEFUL,true);
                }
                else if(args.equals(String.valueOf(Difficulty.EASY.getId()))) {
                    myWorld.getServer().setDifficultyForAllWorlds(Difficulty.EASY,true);
                }
                else if(args.equals(String.valueOf(Difficulty.NORMAL.getId()))) {
                    myWorld.getServer().setDifficultyForAllWorlds(Difficulty.NORMAL,true);
                }
                else if(args.equals(String.valueOf(Difficulty.HARD.getId()))) {
                    myWorld.getServer().setDifficultyForAllWorlds(Difficulty.HARD,true);
                }
                return null;
            }
            else if (cmd.equals(WORLDSPAWNPARTICLE)) {
                String[] arg_items = args.split(",");
                assert Minecraft.getInstance().player != null;
                Minecraft.getInstance().player.sendChatMessage("/particle " + arg_items[9] + " " + arg_items[1] + " " + arg_items[2] + " " + arg_items[3] + " " + arg_items[4] + " " + arg_items[5] + " " + arg_items[6] + " " + arg_items[7] + " " + arg_items[8]);
                return null;
            }
            else if (cmd.equals(ENTITYGETPOS)) {
                BlockPos playerPos = myWorld.getPlayers().get(0).getPosition();
                return playerPos.getX() + "," + playerPos.getY() + "," + playerPos.getZ();
            }
            else if (cmd.equals(ENTITYSETPOS)) {
                String[] arg_items = args.split(",");
                assert Minecraft.getInstance().player != null;
                Minecraft.getInstance().player.sendChatMessage("/tp " + Double.parseDouble(arg_items[1]) + " " + Double.parseDouble(arg_items[2]) + " " + Double.parseDouble(arg_items[3]));
                return null;
            }
            else if (cmd.equals(CHAT)) {
                for(ServerPlayerEntity p : myWorld.getPlayers()){
                    ITextComponent msg = new StringTextComponent(args);
                    p.sendMessage(msg,p.getUniqueID());
                }
                return null;
            }
            else if (cmd.equals(GIVEENCHANT)) {
                String[] arg_items = args.split(",");
                assert Minecraft.getInstance().player != null;
                Minecraft.getInstance().player.sendChatMessage("/enchant @p " + arg_items[2] + " " + arg_items[1]);
                return null;
            }
            return null;
        }else{
            return null;
        }
    }

    private void ChangeGameType(GameType gameTypeIn){
        for(ServerPlayerEntity serverplayerentity : myWorld.getPlayers()) {
            if (serverplayerentity.interactionManager.getGameType() != gameTypeIn) {
                serverplayerentity.setGameType(gameTypeIn);
            }
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
