package com.ewyboy.devkit.commands.server;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.block.Block;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class LocateBlockAndTeleport {

    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.locate.failed"));

    public static LiteralCommandNode<CommandSource> register(CommandDispatcher<CommandSource> source) {
        LiteralArgumentBuilder<CommandSource> literalargumentbuilder = Commands.literal("locateblock").requires((commandSource) -> commandSource.hasPermission(2));

        for (Block block : ForgeRegistries.BLOCKS) {
            String name = Objects.requireNonNull(block.getRegistryName()).toString().replace("minecraft:", "");
            literalargumentbuilder = literalargumentbuilder.then(Commands.literal(name)
                    .executes(ctx -> locate(ctx.getSource(), block))
            );
        }

        return source.register(literalargumentbuilder);
    }

    private static int locate(CommandSource source, Block block) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrException();
        ServerWorld level = player.getLevel();

        player.sendMessage(new StringTextComponent("Started searching for " + block.getName().getString() + ".."), ChatType.GAME_INFO, player.getUUID());

        BlockPos playerPos = new BlockPos(source.getPosition());
        BlockPos blockPos = findClosestBlock(level, playerPos, block, 256 * 256);

        if (blockPos == null) {
            throw ERROR_FAILED.create();
        } else {
            player.teleportTo(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ());
            return showLocateResult(source, block.getName().getString(), playerPos, blockPos, "commands.locate.success");
        }
    }

    public static int showLocateResult(CommandSource source, String blockName, BlockPos playerPos, BlockPos structurePos, String path) {
        int result = MathHelper.floor(dist(playerPos.getX(), playerPos.getZ(), structurePos.getX(), structurePos.getZ()));
        ITextComponent itextcomponent = TextComponentUtils.wrapInSquareBrackets(new TranslationTextComponent("chat.coordinates", structurePos.getX(), "~", structurePos.getZ())).withStyle((style) -> style.withColor(TextFormatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + structurePos.getX() + " ~ " + structurePos.getZ())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslationTextComponent("chat.coordinates.tooltip"))));
        source.sendSuccess(new TranslationTextComponent(path, blockName, itextcomponent, result), false);

        return result;
    }

    private static float dist(int playerPosX, int playerPosZ, int structurePosX, int structurePosZ) {
        int x = structurePosX - playerPosX;
        int z = structurePosZ - playerPosZ;

        return MathHelper.sqrt((float)(x * x + z * z));
    }


    private static BlockPos findClosestBlock(ServerWorld level, BlockPos startPos, Block targetBlock, int range) {

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
