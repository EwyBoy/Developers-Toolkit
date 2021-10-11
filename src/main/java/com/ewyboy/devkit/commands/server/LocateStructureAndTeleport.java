package com.ewyboy.devkit.commands.server;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class LocateStructureAndTeleport {

    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.locate.failed"));

    public static LiteralCommandNode<CommandSource> register(CommandDispatcher<CommandSource> source) {
        LiteralArgumentBuilder<CommandSource> literalargumentbuilder = Commands.literal("locate").requires((commandSource) -> commandSource.hasPermission(2));

        for (Structure<?> structureFeature : ForgeRegistries.WORLD_TYPES) {
            String name = Objects.requireNonNull(structureFeature.getRegistryName()).toString().replace("minecraft:", "");
            literalargumentbuilder = literalargumentbuilder.then(Commands.literal(name)
                    .executes(ctx -> locate(ctx.getSource(), structureFeature))
            );
        }

        return source.register(literalargumentbuilder);
    }

    private static int locate(CommandSource source, Structure<?> structure) throws CommandSyntaxException {
        BlockPos playerPos = new BlockPos(source.getPosition());
        BlockPos structurePos = source.getLevel().findNearestMapFeature(structure, playerPos, 100, false);

        if (structurePos == null) {
            throw ERROR_FAILED.create();
        } else {
            ServerPlayerEntity player = source.getPlayerOrException();
            ServerWorld level = player.getLevel();

            toggleForcedChunks(level, structurePos, true);
            player.teleportTo(structurePos.getX(),  findSurface(level, structurePos), structurePos.getZ());
            toggleForcedChunks(level, structurePos, false);

            return showLocateResult(source, structure.getFeatureName(), playerPos, structurePos, "commands.locate.success");
        }
    }


    private static void toggleForcedChunks(ServerWorld level, BlockPos structurePos, boolean isForced) {
        BlockPos targetPos = new BlockPos(structurePos.getX(), structurePos.getY(), structurePos.getZ());
        level.setChunkForced(level.getChunk(targetPos).getPos().x, level.getChunk(targetPos).getPos().z, isForced);
    }

    private static int findSurface(ServerWorld level, BlockPos structurePos) {
        int y = structurePos.getY();
        while (!level.canSeeSky(new BlockPos(structurePos.getX(), y, structurePos.getZ()))) {
            y++;
        }
        return y;
    }

    public static int showLocateResult(CommandSource source, String structureName, BlockPos playerPos, BlockPos structurePos, String path) {
        int result = MathHelper.floor(dist(playerPos.getX(), playerPos.getZ(), structurePos.getX(), structurePos.getZ()));
        ITextComponent itextcomponent = TextComponentUtils.wrapInSquareBrackets(new TranslationTextComponent("chat.coordinates", structurePos.getX(), "~", structurePos.getZ())).withStyle((style) -> style.withColor(TextFormatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + structurePos.getX() + " ~ " + structurePos.getZ())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslationTextComponent("chat.coordinates.tooltip"))));
        source.sendSuccess(new TranslationTextComponent(path, structureName, itextcomponent, result), false);

        return result;
    }

    private static float dist(int playerPosX, int playerPosZ, int structurePosX, int structurePosZ) {
        int x = structurePosX - playerPosX;
        int z = structurePosZ - playerPosZ;

        return MathHelper.sqrt((float)(x * x + z * z));
    }

}
