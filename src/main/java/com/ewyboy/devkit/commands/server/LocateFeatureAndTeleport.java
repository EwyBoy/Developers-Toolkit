package com.ewyboy.devkit.commands.server;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class LocateFeatureAndTeleport {

    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.locate.failed"));

    public static LiteralCommandNode<CommandSourceStack> register(CommandDispatcher<CommandSourceStack> source) {
        LiteralArgumentBuilder<CommandSourceStack> literalargumentbuilder = Commands.literal("locateFeature").requires((commandSource) -> commandSource.hasPermission(2));

        for (Feature<?> feature : ForgeRegistries.FEATURES) {
            String name = Objects.requireNonNull(feature.getRegistryName()).toString().replace("minecraft:", "");
            literalargumentbuilder = literalargumentbuilder.then(Commands.literal(name)
                    .executes(ctx -> locate(ctx.getSource(), feature))
            );
        }

        return source.register(literalargumentbuilder);
    }

    private static int locate(CommandSourceStack source, Feature<?> feature) throws CommandSyntaxException {
        BlockPos playerPos = new BlockPos(source.getPosition());
        //BlockPos structurePos = source.getLevel().findNearestMapFeature(feature, playerPos, 100, false);

        /*if (structurePos == null) {
            throw ERROR_FAILED.create();
        } else {
            ServerPlayerEntity player = source.getPlayerOrException();
            ServerWorld level = player.getLevel();

            toggleForcedChunks(level, structurePos, true);
            player.teleportTo(structurePos.getX(),  findSurface(level, structurePos), structurePos.getZ());
            toggleForcedChunks(level, structurePos, false);

            return showLocateResult(source, feature.getFeatureName(), playerPos, structurePos, "commands.locate.success");
        }*/
        return 0;
    }

    /*public BlockPos findNearestMapFeature(ServerWorld level, Feature<?> feature, BlockPos pos, int p_241117_3_, boolean p_241117_4_) {
        return level.getServer().getWorldData().worldGenSettings().generateFeatures() ? null : level.getChunkSource().getGenerator().findNearestMapFeature(level, feature, pos, p_241117_3_, p_241117_4_);
    }*/

    private static void toggleForcedChunks(ServerLevel level, BlockPos structurePos, boolean isForced) {
        BlockPos targetPos = new BlockPos(structurePos.getX(), structurePos.getY(), structurePos.getZ());
        level.setChunkForced(level.getChunk(targetPos).getPos().x, level.getChunk(targetPos).getPos().z, isForced);
    }

    private static int findSurface(ServerLevel level, BlockPos structurePos) {
        int y = structurePos.getY();
        while (!level.canSeeSky(new BlockPos(structurePos.getX(), y, structurePos.getZ()))) {
            y++;
        }
        return y;
    }

    public static int showLocateResult(CommandSourceStack source, String structureName, BlockPos playerPos, BlockPos structurePos, String path) {
        int result = Mth.floor(dist(playerPos.getX(), playerPos.getZ(), structurePos.getX(), structurePos.getZ()));
        Component itextcomponent = ComponentUtils.wrapInSquareBrackets(new TranslatableComponent("chat.coordinates", structurePos.getX(), "~", structurePos.getZ())).withStyle((style) -> style.withColor(ChatFormatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + structurePos.getX() + " ~ " + structurePos.getZ())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableComponent("chat.coordinates.tooltip"))));
        source.sendSuccess(new TranslatableComponent(path, structureName, itextcomponent, result), false);

        return result;
    }

    private static float dist(int playerPosX, int playerPosZ, int structurePosX, int structurePosZ) {
        int x = structurePosX - playerPosX;
        int z = structurePosZ - playerPosZ;

        return Mth.sqrt((float)(x * x + z * z));
    }

}
