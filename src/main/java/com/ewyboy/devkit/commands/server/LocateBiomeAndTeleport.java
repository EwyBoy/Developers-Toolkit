package com.ewyboy.devkit.commands.server;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.command.impl.LocateCommand;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.biome.Biome;

public class LocateBiomeAndTeleport {

    public static final DynamicCommandExceptionType ERROR_INVALID_BIOME = new DynamicCommandExceptionType((error) -> new TranslationTextComponent("commands.locatebiome.invalid", error));
    private static final DynamicCommandExceptionType ERROR_BIOME_NOT_FOUND = new DynamicCommandExceptionType((error) -> new TranslationTextComponent("commands.locatebiome.notFound", error));

    public static LiteralCommandNode<CommandSource> register(CommandDispatcher<CommandSource> dispatcher) {
        return dispatcher.register(Commands.literal("locatebiome").requires((source) -> source.hasPermission(2)).then(Commands.argument("biome", ResourceLocationArgument.id()).suggests(SuggestionProviders.AVAILABLE_BIOMES).executes((ctx) -> locateBiome(ctx.getSource(), ctx.getArgument("biome", ResourceLocation.class)))));
    }

    private static int locateBiome(CommandSource source, ResourceLocation resourceLocation) throws CommandSyntaxException {
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
            ServerPlayerEntity player = source.getPlayerOrException();
            player.teleportTo(biomePos.getX(), biomePos.getY(), biomePos.getZ());

            return LocateCommand.showLocateResult(source, biomeResourceLocation, playerPos, biomePos, "commands.locatebiome.success");
        }
    }

}
