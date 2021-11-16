package com.ewyboy.devkit.network.messages;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageGameInfo {

    public String serverInformation;

    public MessageGameInfo(String serverInformation) {
        this.serverInformation = serverInformation;
    }

    public String getServerInformation() {
        return serverInformation;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(this.serverInformation);
    }

    public static MessageGameInfo decode(FriendlyByteBuf buf) {
        return new MessageGameInfo(buf.readUtf());
    }

    public static void handle(MessageGameInfo message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();

            player.sendMessage(new TextComponent(
                    message.getServerInformation() + " has been copied to clipboard"
            ), ChatType.GAME_INFO, player.getUUID());

        });
        ctx.get().setPacketHandled(true);
    }

}
