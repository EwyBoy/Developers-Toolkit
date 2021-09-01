package com.ewyboy.devkit.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public class Settings {

    public static final ForgeConfigSpec clientSettingSpec;
    public static final ForgeConfigSpec commonSettingSpec;
    public static final ForgeConfigSpec serverSettingSpec;

    public static final ClientSettings CLIENT_SETTINGS;
    public static final CommonSettings COMMON_SETTINGS;
    public static final ServerSettings SERVER_SETTINGS;

    static {
        Pair<ClientSettings, ForgeConfigSpec> clientSpecPair = (new ForgeConfigSpec.Builder()).configure(ClientSettings :: new);
        Pair<CommonSettings, ForgeConfigSpec> commonSpecPair = (new ForgeConfigSpec.Builder()).configure(CommonSettings :: new);
        Pair<ServerSettings, ForgeConfigSpec> serverSpecPair = (new ForgeConfigSpec.Builder()).configure(ServerSettings :: new);

        clientSettingSpec = clientSpecPair.getRight();
        commonSettingSpec = commonSpecPair.getRight();
        serverSettingSpec = serverSpecPair.getRight();

        CLIENT_SETTINGS = clientSpecPair.getLeft();
        COMMON_SETTINGS = commonSpecPair.getLeft();
        SERVER_SETTINGS = serverSpecPair.getLeft();
    }

    public static class ClientSettings {

        public final ForgeConfigSpec.ConfigValue<Integer> placeholderValue;

        ClientSettings(ForgeConfigSpec.Builder builder) {
            builder.comment("Developer's Toolkit Client Settings");
                builder.push("SETTINGS");
                    placeholderValue = builder.defineInRange("placeholder", 0, -1, Integer.MAX_VALUE);
                builder.pop();
            builder.build();
        }
    }

    public static class CommonSettings {

        public final ForgeConfigSpec.ConfigValue<Integer> placeholderValue;

        CommonSettings(ForgeConfigSpec.Builder builder) {
            builder.comment("Developer's Toolkit Common Settings");
                builder.push("SETTINGS");
                    placeholderValue = builder.defineInRange("placeholder", 0, -1, Integer.MAX_VALUE);
                builder.pop();
            builder.build();
        }
    }

    public static class ServerSettings {

        public final ForgeConfigSpec.ConfigValue<Integer> placeholderValue;

        ServerSettings(ForgeConfigSpec.Builder builder) {
            builder.comment("Developer's Toolkit Server Settings");
                builder.push("SETTINGS");
                    placeholderValue = builder.defineInRange("placeholder", 0, -1, Integer.MAX_VALUE);
                builder.pop();
            builder.build();
        }
    }

    public static void setup() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Settings.clientSettingSpec, "DevKit-Client.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Settings.commonSettingSpec, "DevKit-Common.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Settings.serverSettingSpec, "DevKit-Server.toml");
    }

}
