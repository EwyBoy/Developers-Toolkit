package com.ewyboy.devkit.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class Toolbox {

    public static class Position {

        public static BlockPos BlockPosFromVec3d(Vec3 vec3d) {
            return new BlockPos(vec3d.x, vec3d.y, vec3d.z);
        }

    }

    public static class Tools {

        public static void copyToClipboard(String copy) {
            StringSelection selection = new StringSelection(copy);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
        }

    }

}
