package com.ewyboy.devkit.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public class Toolbox {

    public static class Position {

        public static BlockPos BlockPosFromVec3d(Vector3d vec3d) {
            return new BlockPos(vec3d.x, vec3d.y, vec3d.z);
        }

    }

}
