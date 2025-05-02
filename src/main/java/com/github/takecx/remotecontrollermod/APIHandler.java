package com.github.takecx.remotecontrollermod;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameType;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class APIHandler {
    private ServerLevel myWorld = null;
    private static final Logger LOGGER = LogManager.getLogger();

    // Commands
    // 以下のコマンド文字列の後に()をつけて()内には適切な引数を入れる
    // player.getPos()
    protected static final String PLAYERGETPOS = "player.getPos";
    // world.getPlayerIds()
    protected static final String WORLDGETPLAYERIDS = "world.getPlayerIds";
    // world.getBlockWithData(x,y,z)
    protected static final String GETBLOCKWITHDATA = "world.getBlockWithData";
    // world.setBlock(x,y,z,minecraft:magma_block)
    protected static final String SETBLOCK = "world.setBlock";
    // world.setBlocks(x,y,z,x,y,z,minecraft:magma_block)
    protected static final String SETBLOCKS = "world.setBlocks";
    // world.spawnEntity(x,y,z,minecraft:wither_skeleton)
    protected static final String WORLDSPAWNENTITY = "world.spawnEntity";
    // world.changeWeather(clear|rain|thunder)
    protected static final String WORLDCHANGEWEATHER = "world.changeWeather";
    // world.changeGameMode(0|1|2|3)
    protected static final String WORLDCHANGEGAMEMODE = "world.changeGameMode";
    // world.changeDifficulty(0|1|2|3)
    protected static final String WORLDCHANGEDIFFICULTY = "world.changeDifficulty";
    // world.spawnParticle(minecraft:explosion_emitter,x,y,z,dx,dy,dz,speed,count)
    protected static final String WORLDSPAWNPARTICLE = "world.spawnParticle";
    // entity.getPos()
    protected static final String ENTITYGETPOS = "entity.getPos";
    // entity.setPos(x,y,z)
    protected static final String ENTITYSETPOS = "entity.setPos";
    // chat.post(message)
    protected static final String CHAT = "chat.post";
    // giveEnchant(minecraft:sharpness,5)
    protected static final String GIVEENCHANT = "giveEnchant";

    public APIHandler() {
        MinecraftServer currentServer = ServerLifecycleHooks.getCurrentServer();
        this.myWorld = currentServer.overworld();
    }

    private void CheckScreen() throws InterruptedException {
        while (Remotecontrollermod.isShowingMenu) {
            Thread.sleep(500);
        }
    }

    public Object Process(String commandStrIn) throws Exception {
        if (this.myWorld.isClientSide == false) {
            this.CheckScreen();
            String[] contents = commandStrIn.split("\\(");
            String cmd = contents[0];
            String args = contents[1].length() != 1 ? contents[1].split("\\)")[0] : "";
            if (cmd.equals(PLAYERGETPOS)) {
                Vec3 playerPos = this.myWorld.players().get(0).position();
                return playerPos.x + "," + playerPos.y + "," + playerPos.z;
            } else if (cmd.equals(WORLDGETPLAYERIDS)) {
                List<Integer> players = new ArrayList<Integer>();
                for (Player p : myWorld.players()) {
                    players.add(p.getId());
                }
                Collections.sort(players);

                StringBuilder ids = new StringBuilder();
                for (Integer id : players) {
                    if (ids.length() > 0)
                        ids.append("|");
                    ids.append(id);
                }
                return ids.toString();
            } else if (cmd.equals(GETBLOCKWITHDATA)) {
                String[] coords = args.split(",");
                BlockPos targetPos = new BlockPos(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]),
                        Integer.parseInt(coords[2]));
                BlockState targetState = this.myWorld.getBlockState(targetPos);
                return targetState.getBlock().getDescriptionId();
            } else if (cmd.equals(SETBLOCK)) {
                String[] arg_content = args.split(",");
                BlockPos targetPos = new BlockPos(Integer.parseInt(arg_content[0]), Integer.parseInt(arg_content[1]),
                        Integer.parseInt(arg_content[2]));
                String[] resourceParts = arg_content[3].split(":");
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(resourceParts[0], resourceParts[1]));
                assert block != null;
                myWorld.setBlock(targetPos, block.defaultBlockState(), 3);
                return null;
            } else if (cmd.equals(SETBLOCKS)) {
                String[] arg_content = args.split(",");
                String[] resourceParts = arg_content[6].split(":");
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(resourceParts[0], resourceParts[1]));
                assert block != null;
                int startX = Integer.parseInt(arg_content[0]);
                int startY = Integer.parseInt(arg_content[1]);
                int startZ = Integer.parseInt(arg_content[2]);
                int endX = Integer.parseInt(arg_content[3]);
                int endY = Integer.parseInt(arg_content[4]);
                int endZ = Integer.parseInt(arg_content[5]);
                if (startX > endX) {
                    int tmp = startX;
                    startX = endX;
                    endX = tmp;
                }
                if (startY > endY) {
                    int tmp = startY;
                    startY = endY;
                    endY = tmp;
                }
                if (startZ > endZ) {
                    int tmp = startZ;
                    startZ = endZ;
                    endZ = tmp;
                }
                for (int x = startX; x < endX + 1; x++) {
                    for (int y = startY; y < endY + 1; y++) {
                        for (int z = startZ; z < endZ + 1; z++) {
                            BlockPos targetPos = new BlockPos(x, y, z);
                            myWorld.setBlock(targetPos, block.defaultBlockState(), 3);
                        }
                    }
                }
                return null;
            } else if (cmd.equals(WORLDSPAWNENTITY)) {
                String[] arg_content = args.split(",");
                BlockPos targetPos = new BlockPos(Integer.parseInt(arg_content[0]), Integer.parseInt(arg_content[1]),
                        Integer.parseInt(arg_content[2]));
                String[] resourceParts = arg_content[3].split(":");
                EntityType<?> targetEntity = ForgeRegistries.ENTITY_TYPES
                        .getValue(new ResourceLocation(resourceParts[0], resourceParts[1]));

                if (targetEntity != null) {
                    Entity entity = targetEntity.create(myWorld);
                    assert entity != null;
                    entity.setPos(targetPos.getX(), targetPos.getY(), targetPos.getZ());
                    myWorld.addFreshEntity(entity);
                } else {
                    throw new IllegalArgumentException("Invalid entity type: " + arg_content[0]);
                }
                return null;
            } else if (cmd.equals(WORLDCHANGEWEATHER)) {
                int weatherTime = 600; // 天候の持続時間
                if (args.equals("clear")) {
                    myWorld.setWeatherParameters(weatherTime, 0, false, false); // 晴れ
                } else if (args.equals("rain")) {
                    myWorld.setWeatherParameters(0, weatherTime, true, false); // 雨
                } else if (args.equals("thunder")) {
                    myWorld.setWeatherParameters(0, weatherTime, true, true); // 雷雨
                }
                return null;
            } else if (cmd.equals(WORLDCHANGEGAMEMODE)) {
                if (args.equals(String.valueOf(GameType.SURVIVAL.getId()))) {
                    ChangeGameType(GameType.SURVIVAL);
                } else if (args.equals(String.valueOf(GameType.CREATIVE.getId()))) {
                    ChangeGameType(GameType.CREATIVE);
                } else if (args.equals(String.valueOf(GameType.ADVENTURE.getId()))) {
                    ChangeGameType(GameType.ADVENTURE);
                } else if (args.equals(String.valueOf(GameType.SPECTATOR.getId()))) {
                    ChangeGameType(GameType.SPECTATOR);
                }
            } else if (cmd.equals(WORLDCHANGEDIFFICULTY)) {
                if (args.equals(String.valueOf(Difficulty.PEACEFUL.getId()))) {
                    myWorld.getServer().setDifficulty(Difficulty.PEACEFUL, true);
                } else if (args.equals(String.valueOf(Difficulty.EASY.getId()))) {
                    myWorld.getServer().setDifficulty(Difficulty.EASY, true);
                } else if (args.equals(String.valueOf(Difficulty.NORMAL.getId()))) {
                    myWorld.getServer().setDifficulty(Difficulty.NORMAL, true);
                } else if (args.equals(String.valueOf(Difficulty.HARD.getId()))) {
                    myWorld.getServer().setDifficulty(Difficulty.HARD, true);
                }
                return null;
            } else if (cmd.equals(WORLDSPAWNPARTICLE)) {
                String[] arg_items = args.split(",");
                assert Minecraft.getInstance().player != null;
                assert Minecraft.getInstance().player.connection != null;
                Minecraft.getInstance().player.connection
                        .sendCommand("particle " + arg_items[0] + " " + Integer.parseInt(arg_items[1])
                                + " " + Integer.parseInt(arg_items[2]) + " " + Integer.parseInt(arg_items[3]) + " "
                                + Integer.parseInt(arg_items[4]) + " " + Integer.parseInt(arg_items[5]) + " "
                                + Integer.parseInt(arg_items[6]) + " " + Float.parseFloat(arg_items[7]) + " "
                                + Integer.parseInt(arg_items[8]));
                return null;
            } else if (cmd.equals(ENTITYGETPOS)) {
                Vec3 playerPos = Minecraft.getInstance().player.position();
                return playerPos.x + "," + playerPos.y + "," + playerPos.z;
            } else if (cmd.equals(ENTITYSETPOS)) {
                String[] arg_items = args.split(",");
                assert Minecraft.getInstance().player != null;
                assert Minecraft.getInstance().player.connection != null;
                Minecraft.getInstance().player.connection.sendCommand("tp " + Double.parseDouble(arg_items[0]) + " "
                        + Double.parseDouble(arg_items[1]) + " " + Double.parseDouble(arg_items[2]));
                return null;
            } else if (cmd.equals(CHAT)) {
                for (ServerPlayer p : myWorld.players()) {
                    Component msg = Component.literal(args);
                    p.sendSystemMessage(msg);
                }
                return null;
            } else if (cmd.equals(GIVEENCHANT)) {
                String[] arg_items = args.split(",");
                assert Minecraft.getInstance().player != null;
                assert Minecraft.getInstance().player.connection != null;
                Minecraft.getInstance().player.connection
                        .sendCommand("enchant @p " + arg_items[0] + " " + arg_items[1]);
                return null;
            }
            return null;
        } else {
            return null;
        }
    }

    private void ChangeGameType(GameType gameTypeIn) {
        for (ServerPlayer serverplayer : myWorld.players()) {
            if (serverplayer.gameMode.getGameModeForPlayer() != gameTypeIn) {
                serverplayer.setGameMode(gameTypeIn);
            }
        }
    }
}
