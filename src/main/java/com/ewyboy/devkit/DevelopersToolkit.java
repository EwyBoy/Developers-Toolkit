package com.ewyboy.devkit;

import com.ewyboy.devkit.client.KeyBindingHandler;
import com.ewyboy.devkit.client.MainMenuEvent;
import com.ewyboy.devkit.commands.CommandCenter;
import com.ewyboy.devkit.config.Settings;
import com.ewyboy.devkit.events.handlers.TooltipEventHandler;
import com.ewyboy.devkit.network.MessageHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static com.ewyboy.devkit.DevelopersToolkit.MOD_ID;

@Mod(MOD_ID)
public class DevelopersToolkit {

    public static final String MOD_ID = "devkit";

    public DevelopersToolkit() {
        ignoreServerOnly();
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        Settings.setup();
        MinecraftForge.EVENT_BUS.addListener(this :: registerCommands);
        MessageHandler.init();
        System.setProperty("java.awt.headless", "false");
    }

    //Make sure the mod being absent on the other network side does not cause the client to display the server as incompatible
    private void ignoreServerOnly() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () ->
                new IExtensionPoint.DisplayTest(() -> "You Can Write Whatever The Fuck You Want Here", (YouCanWriteWhatEverTheFuckYouWantHere, ICreatedSlimeBlocks2YearsBeforeMojangDid) -> ICreatedSlimeBlocks2YearsBeforeMojangDid)
        );
    }
    
    public void registerCommands(RegisterCommandsEvent event) {
        new CommandCenter(event.getDispatcher());
    }

    @SubscribeEvent
    public void clientRegister(FMLClientSetupEvent event) {
        KeyBindingHandler.initKeyBinding();
        MinecraftForge.EVENT_BUS.register(new MainMenuEvent());
        MinecraftForge.EVENT_BUS.register(new TooltipEventHandler());
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, KeyBindingHandler :: onKeyInput);
    }

}
