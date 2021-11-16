package com.ewyboy.devkit.commands.server;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class LocateStructureAndTeleport {

    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.locate.failed"));

    public static LiteralCommandNode<CommandSourceStack> register(CommandDispatcher<CommandSourceStack> source) {
        LiteralArgumentBuilder<CommandSourceStack> literalargumentbuilder = Commands.literal("locate").requires((commandSource) -> commandSource.hasPermission(2));

        for (StructureFeature<?> structureFeature : ForgeRegistries.STRUCTURE_FEATURES) {
            String name = Objects.requireNonNull(structureFeature.getRegistryName()).toString().replace("minecraft:", "");
            literalargumentbuilder = literalargumentbuilder.then(Commands.literal(name)
                    .executes(ctx -> locate(ctx.getSource(), structureFeature))
            );
        }

        return source.register(literalargumentbuilder);
    }

    private static int locate(CommandSourceStack source, StructureFeature<?> structure) throws CommandSyntaxException {
        BlockPos playerPos = new BlockPos(source.getPosition());
        BlockPos structurePos = source.getLevel().findNearestMapFeature(structure, playerPos, 100, false);

        if (structurePos == null) {
            throw ERROR_FAILED.create();
        } else {
            ServerPlayer player = source.getPlayerOrException();
            ServerLevel level = player.getLevel();

            toggleForcedChunks(level, structurePos, true);
            player.teleportTo(structurePos.getX(),  findSurface(level, structurePos), structurePos.getZ());
            toggleForcedChunks(level, structurePos, false);

            return showLocateResult(source, structure.getFeatureName(), playerPos, structurePos, "commands.locate.success");
        }
    }


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
