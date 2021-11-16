package com.ewyboy.devkit.events.handlers;

import com.ewyboy.devkit.network.MessageHandler;
import com.ewyboy.devkit.util.ModLogger;
import com.ewyboy.devkit.util.Toolbox;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Objects;

public class TooltipEventHandler {

    private final int shift = 340;
    private final int ctrl = 341;
    private final int alt = 342;
    private final int c = 67;

    private static boolean isShiftPressed = false;
    private static boolean isCtrlPressed = false;
    private static boolean isAltPressed = false;
    private static boolean isCPressed = false;

    private boolean isAdvanced(ItemTooltipEvent event) {
        return event.getFlags().isAdvanced();
    }

    private Player getPlayer(ItemTooltipEvent event) {
        return event.getPlayer();
    }

    private ItemStack getItemStack(ItemTooltipEvent event) {
        return event.getItemStack();
    }

    private void toggleModifier(int keyCode, int targetKeyCode, boolean isPressed, boolean release) {
        if (release) {
            if (isPressed && keyCode == targetKeyCode) {
                if (targetKeyCode == shift) isShiftPressed = false;
                if (targetKeyCode == ctrl) isCtrlPressed = false;
                if (targetKeyCode == alt) isAltPressed = false;
                if (targetKeyCode == c) isCPressed = false;
            }
        } else if (!isPressed && keyCode == targetKeyCode) {
            if (targetKeyCode == shift) isShiftPressed = true;
            if (targetKeyCode == ctrl) isCtrlPressed = true;
            if (targetKeyCode == alt) isAltPressed = true;
            if (targetKeyCode == c) isCPressed = true;
        }
    }

    @SubscribeEvent
    public void onKeyPress(GuiScreenEvent.KeyboardKeyPressedEvent.Pre event) {
        ModLogger.info(event.getKeyCode() + " :: keycode");
        if (event.getGui() instanceof CreativeModeInventoryScreen) {
            toggleModifier(event.getKeyCode(), shift, isShiftPressed, false);
            toggleModifier(event.getKeyCode(), ctrl, isCtrlPressed, false);
            toggleModifier(event.getKeyCode(), alt, isAltPressed, false);
            toggleModifier(event.getKeyCode(), c, isCPressed, false);
        }
    }

    @SubscribeEvent
    public void onKeyPress(GuiScreenEvent.KeyboardKeyReleasedEvent.Post event) {
        if (event.getGui() instanceof CreativeModeInventoryScreen) {
            toggleModifier(event.getKeyCode(), shift, isShiftPressed, true);
            toggleModifier(event.getKeyCode(), ctrl, isCtrlPressed, true);
            toggleModifier(event.getKeyCode(), alt, isAltPressed, true);
            toggleModifier(event.getKeyCode(), c, isCPressed, true);
        }
    }

    @SubscribeEvent
    public void tooltip(ItemTooltipEvent event) {
        // TODO Super Advanced Tooltip Info here

        if (isShiftPressed) {
            event.getToolTip().add(new TextComponent("Registry Name: " + getItemStack(event).getItem().getRegistryName()));
        }

        if (isCtrlPressed) {
            event.getToolTip().add(new TextComponent("Description ID: " + getItemStack(event).getItem().getDescriptionId()));
        }

        if (isAltPressed) {
            event.getToolTip().add(new TextComponent("Description: " + getItemStack(event).getItem().getDescription()));
        }

        if (isCPressed) {
            String name = getItemStack(event).getItem().getRegistryName() + "";
            Toolbox.Tools.copyToClipboard(name);
            Objects.requireNonNull(event.getPlayer()).sendMessage(new TextComponent(name + " has been copied to Clipboard"), event.getPlayer().getUUID());
            isCPressed = false;
        }

    }

}
