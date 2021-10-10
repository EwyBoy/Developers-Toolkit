package com.ewyboy.devkit.network;

import com.ewyboy.devkit.network.messages.MessageGameInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class MessageHandler {

    private static int messageID = 0;
    private static final String PROTOCOL_VERSION = Integer.toString(1);
    public static final SimpleChannel CHANNEL;

    public MessageHandler() {}

    private static int nextID() {
        return messageID++;
    }

    public static void init() {
        CHANNEL.registerMessage(nextID(), MessageGameInfo.class, MessageGameInfo :: encode, MessageGameInfo:: decode, MessageGameInfo:: handle);
    }

    static {
        NetworkRegistry.ChannelBuilder channelBuilder = NetworkRegistry.ChannelBuilder.named(new ResourceLocation("devkit", "network"));
        String version = PROTOCOL_VERSION;
        channelBuilder = channelBuilder.clientAcceptedVersions(version :: equals);
        version = PROTOCOL_VERSION;
        CHANNEL = channelBuilder.serverAcceptedVersions(version :: equals).networkProtocolVersion(() -> PROTOCOL_VERSION).simpleChannel();
    }

}
