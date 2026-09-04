package net.exaltedzoro.cutscenelib.cutscene.keyframe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class FOVKeyframe extends Keyframe {

    final double fov;

    final KeyframeInterpolation interpolation;

    public static final Codec<FOVKeyframe> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("time").forGetter(FOVKeyframe::getTime),
                    Codec.DOUBLE.fieldOf("fov").forGetter(FOVKeyframe::getFOV),
                    KeyframeInterpolation.CODEC.fieldOf("interpolation").forGetter(FOVKeyframe::getInterpolation)
            ).apply(instance, FOVKeyframe::new)
    );

    public FOVKeyframe(float time, double fov, KeyframeInterpolation interpolation) {
        this.time = time;
        this.fov = fov;
        this.interpolation = interpolation;
    }

    public double getFOV() {
        return fov;
    }

    public KeyframeInterpolation getInterpolation() {
        return interpolation;
    }

    @Override
    public Keyframe copy() {
        return new FOVKeyframe(time, fov, interpolation);
    }
}
