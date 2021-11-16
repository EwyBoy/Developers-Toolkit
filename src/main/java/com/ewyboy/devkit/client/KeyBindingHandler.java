package com.ewyboy.devkit.client;

import com.ewyboy.devkit.DevelopersToolkit;
import com.ewyboy.devkit.network.MessageHandler;
import com.ewyboy.devkit.network.messages.MessageGameInfo;
import com.ewyboy.devkit.util.Toolbox;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fmlclient.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;

public class KeyBindingHandler {

    private static KeyMapping copy;

    public KeyBindingHandler() {}

    public static void initKeyBinding() {
        copy = new KeyMapping("Copy to Clipboard", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_C, DevelopersToolkit.MOD_ID);
        ClientRegistry.registerKeyBinding(copy);
    }

    @SubscribeEvent
    public static void onKeyInput(KeyInputEvent event) {
        if(copy.consumeClick()) {
            Minecraft instance = Minecraft.getInstance();

            if(instance.hitResult.getType() != HitResult.Type.BLOCK){return;}

            Vec3 blockVector = instance.hitResult.getLocation();

            double blockX = blockVector.x;
            double blockY = blockVector.y;
            double blockZ = blockVector.z;

            assert instance.player != null;

            double playerX = instance.player.getX();
            double playerY = instance.player.getY();
            double playerZ = instance.player.getZ();

            if(blockX == Math.floor(blockX) && blockX <= playerX)   {blockX--;}
            if(blockY == Math.floor(blockY) && blockY <= playerY+1) {blockY--;}
            if(blockZ == Math.floor(blockZ) && blockZ <= playerZ)   {blockZ--;}

            assert instance.level != null;

            BlockState block = instance.level.getBlockState(new BlockPos(blockX, blockY, blockZ));

            String nameCopy = Objects.requireNonNull(block.getBlock().getRegistryName()).toString();
            Toolbox.Tools.copyToClipboard(nameCopy);
            MessageHandler.CHANNEL.sendToServer(new MessageGameInfo(nameCopy));
        }
    }

}