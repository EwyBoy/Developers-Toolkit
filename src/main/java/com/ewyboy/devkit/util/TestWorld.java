package com.ewyboy.devkit.util;

import com.mojang.serialization.Lifecycle;
import net.minecraft.client.Minecraft;
import net.minecraft.util.datafix.codec.DatapackCodec;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.*;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraft.world.storage.SaveFormat;

import java.io.IOException;
import java.util.Random;

public class TestWorld {

    // TODO Config String here:
    private static final String name = "Test World";
    private static final SimpleRegistry<Dimension> simpleRegistry = new DefaultedRegistry<>("overworld", Registry.LEVEL_STEM_REGISTRY, Lifecycle.stable());

    public static void createTestWorld() {
        WorldSettings worldsettings = new WorldSettings(
                name,
                GameType.CREATIVE,
                false,
                Difficulty.PEACEFUL,
                true,
                new GameRules(),
                DatapackCodec.DEFAULT
        );

        Random random = new Random();

        Minecraft.getInstance().createLevel(
                name,
                worldsettings,
                DynamicRegistries.Impl.builtin(),
                new DimensionGeneratorSettings(random.nextLong(), false, false, simpleRegistry)
        );
    }

    // Load the world
    public static boolean loadDevWorld() {
        if (Minecraft.getInstance().getLevelSource().levelExists(name)) {
            Minecraft.getInstance().loadLevel(name);
            return true;
        }
        return false;
    }

    // Delete the world
    public static void deleteDevWorld() {
        SaveFormat saveFormat = Minecraft.getInstance().getLevelSource();
        try {
            saveFormat.createAccess(name).deleteLevel();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Check to see if the Save Exists
    public static boolean saveExist() {
        return Minecraft.getInstance().getLevelSource().levelExists(name);
    }


}
