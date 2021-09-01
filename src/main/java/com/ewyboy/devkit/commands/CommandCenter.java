package com.ewyboy.devkit.commands;

import com.ewyboy.devkit.DevelopersToolkit;
import com.ewyboy.devkit.commands.server.LocateStructureAndTeleport;
import com.ewyboy.devkit.commands.server.LocateBiomeAndTeleport;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;

public class CommandCenter {

    public CommandCenter(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                LiteralArgumentBuilder.<CommandSource> literal(DevelopersToolkit.MOD_ID)
                        .then(LocateStructureAndTeleport.register(dispatcher))
                        .then(LocateBiomeAndTeleport.register(dispatcher))
                        .executes(ctx -> 0)
        );
    }

}
