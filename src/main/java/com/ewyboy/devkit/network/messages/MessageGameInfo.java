package com.ewyboy.devkit.network.messages;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageGameInfo {

    public String serverInformation;

    public MessageGameInfo(String serverInformation) {
        this.serverInformation = serverInformation;
    }

    public String getServerInformation() {
        return serverInformation;
    }

    public void encode(PacketBuffer buf) {
        buf.writeUtf(this.serverInformation);
    }

    public static MessageGameInfo decode(PacketBuffer buf) {
        return new MessageGameInfo(buf.readUtf());
    }

    public static void handle(MessageGameInfo message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();

            player.sendMessage(new StringTextComponent(
                    message.getServerInformation() + " has been copied to clipboard"
            ), ChatType.GAME_INFO, player.getUUID());

        });
        ctx.get().setPacketHandled(true);
    }

}
