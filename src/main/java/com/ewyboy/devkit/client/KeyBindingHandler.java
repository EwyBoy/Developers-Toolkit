package com.ewyboy.devkit.client;

import com.ewyboy.devkit.DevelopersToolkit;
import com.ewyboy.devkit.network.MessageHandler;
import com.ewyboy.devkit.network.messages.MessageGameInfo;
import com.ewyboy.devkit.util.Toolbox;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;

public class KeyBindingHandler {

    private static KeyBinding copy;

    public KeyBindingHandler() {}

    public static void initKeyBinding() {
        copy = new KeyBinding("Copy to Clipboard", KeyConflictContext.IN_GAME, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_C, DevelopersToolkit.MOD_ID);
        ClientRegistry.registerKeyBinding(copy);
    }

    @SubscribeEvent
    public static void onKeyInput(KeyInputEvent event) {
        if(copy.consumeClick()) {
            Minecraft instance = Minecraft.getInstance();

            if(instance.hitResult.getType() != RayTraceResult.Type.BLOCK){return;}

            Vector3d blockVector = instance.hitResult.getLocation();

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