package com.ewyboy.devkit.events.handlers;

import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TooltipEventHandler {

    private final int shift = 340;
    private final int ctrl = 341;
    private final int alt = 342;

    private static boolean isShiftPressed = false;
    private static boolean isCtrlPressed = false;
    private static boolean isAltPressed = false;

    private boolean isAdvanced(ItemTooltipEvent event) {
        return event.getFlags().isAdvanced();
    }

    private PlayerEntity getPlayer(ItemTooltipEvent event) {
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
            }
        } else if (!isPressed && keyCode == targetKeyCode) {
            if (targetKeyCode == shift) isShiftPressed = true;
            if (targetKeyCode == ctrl) isCtrlPressed = true;
            if (targetKeyCode == alt) isAltPressed = true;
        }
    }

    @SubscribeEvent
    public void onKeyPress(GuiScreenEvent.KeyboardKeyPressedEvent.Pre event) {
        if (event.getGui() instanceof CreativeScreen) {
            toggleModifier(event.getKeyCode(), shift, isShiftPressed, false);
            toggleModifier(event.getKeyCode(), ctrl, isCtrlPressed, false);
            toggleModifier(event.getKeyCode(), alt, isAltPressed, false);
        }
    }

    @SubscribeEvent
    public void onKeyPress(GuiScreenEvent.KeyboardKeyReleasedEvent.Post event) {
        if (event.getGui() instanceof CreativeScreen) {
            toggleModifier(event.getKeyCode(), shift, isShiftPressed, true);
            toggleModifier(event.getKeyCode(), ctrl, isCtrlPressed, true);
            toggleModifier(event.getKeyCode(), alt, isAltPressed, true);
        }
    }

    @SubscribeEvent
    public void tooltip(ItemTooltipEvent event) {
        // TODO Super Advanced Tooltip Info here

        if (isShiftPressed) {
            event.getToolTip().add(new StringTextComponent("Registry Name: " + getItemStack(event).getItem().getRegistryName()));
        }

        if (isCtrlPressed) {
            event.getToolTip().add(new StringTextComponent("Description ID: " + getItemStack(event).getItem().getDescriptionId()));
        }

        if (isAltPressed) {
            event.getToolTip().add(new StringTextComponent("Description: " + getItemStack(event).getItem().getDescription()));
        }

    }

}
