package net.exaltedzoro.cutscenelib.cutscene.keyframe;

import org.joml.Quaternionf;

public class CameraRotationKeyframe extends Keyframe {
    private Quaternionf rotation;

    private KeyframeInterpolation interpolation;

    public CameraRotationKeyframe(int tick, Quaternionf rotation, KeyframeInterpolation interpolation) {
        this.tick = tick;
        this.rotation = rotation;
        this.interpolation = interpolation;
    }

    public int getTick() {
        return tick;
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public KeyframeInterpolation getInterpolation() {
        return interpolation;
    }
}
