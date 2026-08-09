package net.exaltedzoro.cutscenelib.cutscene.keyframe;

import net.minecraft.world.phys.Vec3;

public class CameraPositionKeyframe extends Keyframe {
    private final Vec3 position;

    private final KeyframeInterpolation interpolation;

    public CameraPositionKeyframe(int tick, Vec3 position, KeyframeInterpolation interpolation) {
        this.time = tick;
        this.position = position;
        this.interpolation = interpolation;
    }

    public Vec3 getPosition() {
        return position;
    }

    public KeyframeInterpolation getInterpolation() {
        return interpolation;
    }
}
