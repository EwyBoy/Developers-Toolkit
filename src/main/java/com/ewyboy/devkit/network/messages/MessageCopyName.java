package com.ewyboy.devkit.network.messages;

import com.ewyboy.devkit.util.ModLogger;
import com.ewyboy.devkit.util.Toolbox;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Objects;
import java.util.function.Supplier;

public class MessageCopyName {

    public MessageCopyName() {}

    public static void encode(MessageCopyName pkt, PacketBuffer buf) {}

    public static MessageCopyName decode(PacketBuffer buf) {
        return new MessageCopyName();
    }

    public static void handle(MessageCopyName message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();

            Minecraft instance = Minecraft.getInstance();

            if(instance.hitResult.getType() != RayTraceResult.Type.BLOCK){return;}

            Vector3d blockVector = instance.hitResult.getLocation();

            double bX = blockVector.x;
            double bY = blockVector.y;
            double bZ = blockVector.z;

            double pX = instance.player.getX();
            double pY = instance.player.getY();
            double pZ = instance.player.getZ();

            if(bX == Math.floor(bX) && bX <= pX){bX--;}
            if(bY == Math.floor(bY) && bY <= pY+1){bY--;} // +1 on Y to get y from player eyes instead of feet
            if(bZ == Math.floor(bZ) && bZ <= pZ){bZ--;}

            BlockState block = instance.level.getBlockState(new BlockPos(bX, bY, bZ));

            String nameCopy = Objects.requireNonNull(block.getBlock().getRegistryName()).toString();

            player.sendMessage(new StringTextComponent(
                    nameCopy + " has been copied to clipboard"
            ), ChatType.GAME_INFO, player.getUUID());

            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Clipboard clipboard = toolkit.getSystemClipboard();
            StringSelection strSel = new StringSelection(nameCopy);
            clipboard.setContents(strSel, null);

        });
        ctx.get().setPacketHandled(true);
    }

}
