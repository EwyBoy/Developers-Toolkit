package com.ewyboy.devkit.commands.server;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ILocationArgument;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.command.impl.TeleportCommand;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class DimensionTeleport {

    private static final SimpleCommandExceptionType INVALID_POSITION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.teleport.invalidPosition"));

    public static void register(CommandDispatcher<CommandSource> source) {
        LiteralCommandNode<CommandSource> literalcommandnode = source.register(Commands.literal("dimension").requires((commandSource) -> {
            return commandSource.hasPermission(2);
        }).then(Commands.argument("location", DimensionArgument.dimension()).executes((ctx) -> {
            return teleportToDimension(ctx.getSource(), EntityArgument.getEntities(ctx, "targets"), ctx.getSource().getLevel(), Vec3Argument.getCoordinates(ctx, "location"), (ILocationArgument)null, (TeleportCommand.Facing)null);
        })));
        source.register(Commands.literal("dim").requires((commandSource) -> {
            return commandSource.hasPermission(2);
        }).redirect(literalcommandnode));
    }

    public static int teleportToDimension(ServerPlayerEntity player, ServerWorld level, DimensionArgument dimension) {

        ServerWorld dim = player.getLevel();

        ServerWorld overworld = (ServerWorld) player.level;
        MinecraftServer minecraftserver = overworld.getServer();

        RegistryKey<World> currentDim = player.level.dimension();

        try {
            ServerWorld targetDim = minecraftserver.getLevel();
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }

    }

}
