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

public class APIHandler {
    private ServerLevel myWorld = null;

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
    protected static final String PLAYERGETPOS = "player.getPos";

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
                BlockPos targetPos = new BlockPos(Integer.parseInt(arg_content[1]), Integer.parseInt(arg_content[2]),
                        Integer.parseInt(arg_content[3]));
                ResourceLocation entityResource = new ResourceLocation(arg_content[0]);
                EntityType<?> targetEntity = BuiltInRegistries.ENTITY_TYPE.get(entityResource);

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
                Minecraft.getInstance().player.connection.sendCommand("/particle " + arg_items[0] + " " + arg_items[1]
                        + " " + arg_items[2] + " " + arg_items[3] + " " + arg_items[4] + " " + arg_items[5] + " "
                        + arg_items[6] + " " + arg_items[7] + " " + arg_items[8]);
                return null;
            } else if (cmd.equals(ENTITYGETPOS)) {
                Vec3 playerPos = Minecraft.getInstance().player.position();
                return playerPos.x + "," + playerPos.y + "," + playerPos.z;
            } else if (cmd.equals(ENTITYSETPOS)) {
                String[] arg_items = args.split(",");
                assert Minecraft.getInstance().player != null;
                assert Minecraft.getInstance().player.connection != null;
                Minecraft.getInstance().player.connection.sendCommand("/tp " + Double.parseDouble(arg_items[1]) + " "
                        + Double.parseDouble(arg_items[2]) + " " + Double.parseDouble(arg_items[3]));
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
                        .sendCommand("/enchant @p " + arg_items[2] + " " + arg_items[1]);
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
