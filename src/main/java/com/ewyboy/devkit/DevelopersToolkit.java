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
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;

import static com.ewyboy.devkit.DevelopersToolkit.MOD_ID;

@Mod(MOD_ID)
public class DevelopersToolkit {

    public static final String MOD_ID = "devkit";

    public DevelopersToolkit() {
        makeServerSide();
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        Settings.setup();
        MinecraftForge.EVENT_BUS.addListener(this :: registerCommands);
        MessageHandler.init();
        System.setProperty("java.awt.headless", "false");
    }

    //Make sure the mod being absent on the other network side does not cause the client to display the server as incompatible
    private void makeServerSide() {
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(
                () -> FMLNetworkConstants.IGNORESERVERONLY,
                (YouCanWriteWhatEverTheFuckYouWantHere, ICreatedSlimeBlocks2YearsBeforeMojangDid) -> true)
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
