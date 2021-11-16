package com.ewyboy.devkit.commands.server;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.world.level.block.Block;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class LocateBlockAndTeleport {

    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.locate.failed"));

    public static LiteralCommandNode<CommandSourceStack> register(CommandDispatcher<CommandSourceStack> source) {
        LiteralArgumentBuilder<CommandSourceStack> literalargumentbuilder = Commands.literal("locateblock").requires((commandSource) -> commandSource.hasPermission(2));

        for (Block block : ForgeRegistries.BLOCKS) {
            String name = Objects.requireNonNull(block.getRegistryName()).toString().replace("minecraft:", "");
            literalargumentbuilder = literalargumentbuilder.then(Commands.literal(name)
                    .executes(ctx -> locate(ctx.getSource(), block))
            );
        }

        return source.register(literalargumentbuilder);
    }

    private static int locate(CommandSourceStack source, Block block) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        ServerLevel level = player.getLevel();

        player.sendMessage(new TextComponent("Started searching for " + block.getName().getString() + ".."), ChatType.GAME_INFO, player.getUUID());

        BlockPos playerPos = new BlockPos(source.getPosition());
        BlockPos blockPos = findClosestBlock(level, playerPos, block, 256 * 256);

        if (blockPos == null) {
            throw ERROR_FAILED.create();
        } else {
            player.teleportTo(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ());
            return showLocateResult(source, block.getName().getString(), playerPos, blockPos, "commands.locate.success");
        }
    }

    public static int showLocateResult(CommandSourceStack source, String blockName, BlockPos playerPos, BlockPos structurePos, String path) {
        int result = Mth.floor(dist(playerPos.getX(), playerPos.getZ(), structurePos.getX(), structurePos.getZ()));
        Component itextcomponent = ComponentUtils.wrapInSquareBrackets(new TranslatableComponent("chat.coordinates", structurePos.getX(), "~", structurePos.getZ())).withStyle((style) -> style.withColor(ChatFormatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + structurePos.getX() + " ~ " + structurePos.getZ())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableComponent("chat.coordinates.tooltip"))));
        source.sendSuccess(new TranslatableComponent(path, blockName, itextcomponent, result), false);

        return result;
    }

    private static float dist(int playerPosX, int playerPosZ, int structurePosX, int structurePosZ) {
        int x = structurePosX - playerPosX;
        int z = structurePosZ - playerPosZ;

        return Mth.sqrt((float)(x * x + z * z));
    }


    private static BlockPos findClosestBlock(ServerLevel level, BlockPos startPos, Block targetBlock, int range) {

        // (directionX, directionZ) is a vector - direction in which we move right now
        int directionX = 1;
        int directionZ = 0;

        // length of current segment
        int segment_length = 1;

        // current position (x, z) and how much of current segment we passed
        int x = startPos.getX();
        int z = startPos.getZ();

        BlockPos targetPos;

        int segment_passed = 0;

        for (int count = 0; count < range; ++count) {
            x += directionX;
            z += directionZ;

            ++segment_passed;

            for (int y = level.getMaxBuildHeight(); y > 0; --y) {
                // make a step, add 'direction' vector (directionX, directionZ) to current position (x, z)
                targetPos = new BlockPos(x, y, z);

                if (level.getBlockState(targetPos).getBlock() == targetBlock) {
                    return targetPos;
                }
            }

            if (segment_passed == segment_length) {
                // done with current segment
                segment_passed = 0;

                // 'rotate' directions
                int buffer = directionX;
                directionX = -directionZ;
                directionZ = buffer;

                // increase segment length if necessary
                if (directionZ == 0) {
                    ++segment_length;
                }
            }
        }

        return null;
    }



















}
