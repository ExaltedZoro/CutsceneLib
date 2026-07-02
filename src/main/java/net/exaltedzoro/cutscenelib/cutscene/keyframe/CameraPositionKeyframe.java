package net.exaltedzoro.cutscenelib.cutscene.keyframe;

import net.minecraft.world.phys.Vec3;

public class CameraPositionKeyframe extends Keyframe {
    private Vec3 position;

    private KeyframeInterpolation interpolation;

    public CameraPositionKeyframe(int tick, Vec3 position, KeyframeInterpolation interpolation) {
        this.tick = tick;
        this.position = position;
        this.interpolation = interpolation;
    }

    public int getTick() {
        return tick;
    }

    public Vec3 getPosition() {
        return position;
    }

    public KeyframeInterpolation getInterpolation() {
        return interpolation;
    }
}
