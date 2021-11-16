package com.ewyboy.devkit.commands.server;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.server.commands.LocateCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.biome.Biome;

public class LocateBiomeAndTeleport {

    public static final DynamicCommandExceptionType ERROR_INVALID_BIOME = new DynamicCommandExceptionType((error) -> new TranslatableComponent("commands.locatebiome.invalid", error));
    private static final DynamicCommandExceptionType ERROR_BIOME_NOT_FOUND = new DynamicCommandExceptionType((error) -> new TranslatableComponent("commands.locatebiome.notFound", error));

    public static LiteralCommandNode<CommandSourceStack> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return dispatcher.register(Commands.literal("locatebiome").requires((source) -> source.hasPermission(2)).then(Commands.argument("biome", ResourceLocationArgument.id()).suggests(SuggestionProviders.AVAILABLE_BIOMES).executes((ctx) -> locateBiome(ctx.getSource(), ctx.getArgument("biome", ResourceLocation.class)))));
    }

    private static int locateBiome(CommandSourceStack source, ResourceLocation resourceLocation) throws CommandSyntaxException {
        Biome biome = source.getServer().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getOptional(resourceLocation).orElseThrow(
                () -> ERROR_INVALID_BIOME.create(resourceLocation)
        );
        BlockPos playerPos = new BlockPos(source.getPosition());
        BlockPos biomePos = source.getLevel().findNearestBiome(biome, playerPos, 6400, 8);
        String biomeResourceLocation = resourceLocation.toString();
        if (biomePos == null) {
            throw ERROR_BIOME_NOT_FOUND.create(biomeResourceLocation);
        } else {
            // TODO Bulletproofing needed
            ServerPlayer player = source.getPlayerOrException();
            player.teleportTo(biomePos.getX(), biomePos.getY(), biomePos.getZ());

            return LocateCommand.showLocateResult(source, biomeResourceLocation, playerPos, biomePos, "commands.locatebiome.success");
        }
    }

}
