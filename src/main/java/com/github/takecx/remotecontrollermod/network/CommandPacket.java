package com.github.takecx.remotecontrollermod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CommandPacket {
    private final String command;

    public CommandPacket(String command) {
        this.command = command;
    }

    // 受信時にバッファから読み取る
    public static CommandPacket decode(FriendlyByteBuf buf) {
        return new CommandPacket(buf.readUtf(32767));
    }

    // 送信時にバッファに書き込む
    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(command);
    }

    // コマンドを実行する処理
    public void handle(Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player != null && player.server != null) {
                // ここでコマンドを実行する（例: チャットに表示するだけ）
                player.server.getCommands().performPrefixedCommand(player.createCommandSourceStack(), command);
            }
        });
        ctx.setPacketHandled(true);
    }
}
