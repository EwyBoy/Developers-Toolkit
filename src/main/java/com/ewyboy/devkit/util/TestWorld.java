package com.ewyboy.devkit.util;

import com.mojang.serialization.Lifecycle;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.MappedRegistry;
import net.minecraft.world.*;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.LevelStorageSource;

import java.io.IOException;
import java.util.Random;

import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.dimension.LevelStem;

public class TestWorld {

    // TODO Config String here:
    private static final String name = "Test World";
    private static final MappedRegistry<LevelStem> simpleRegistry = new DefaultedRegistry<>("overworld", Registry.LEVEL_STEM_REGISTRY, Lifecycle.stable());

    public static void createTestWorld() {
        LevelSettings worldsettings = new LevelSettings(
                name,
                GameType.CREATIVE,
                false,
                Difficulty.PEACEFUL,
                true,
                new GameRules(),
                DataPackConfig.DEFAULT
        );

        Random random = new Random();

        Minecraft.getInstance().createLevel(
                name,
                worldsettings,
                RegistryAccess.RegistryHolder.builtin(),
                new WorldGenSettings(random.nextLong(), false, false, simpleRegistry)
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
        LevelStorageSource saveFormat = Minecraft.getInstance().getLevelSource();
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
