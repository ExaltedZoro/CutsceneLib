package net.exaltedzoro.cutscenelib.cutscene.keyframe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.exaltedzoro.cutscenelib.cutscene.codec.CutsceneCodecs;
import org.joml.Quaternionf;

public class CameraRotationKeyframe extends Keyframe {
    private Quaternionf rotation;

    private KeyframeInterpolation interpolation;

    public static final Codec<CameraRotationKeyframe> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("time").forGetter(CameraRotationKeyframe::getTime),
                    CutsceneCodecs.QUATERNION_CODEC.fieldOf("rotation").forGetter(CameraRotationKeyframe::getRotation),
                    KeyframeInterpolation.CODEC.fieldOf("interpolation").forGetter(CameraRotationKeyframe::getInterpolation)
            ).apply(instance, CameraRotationKeyframe::new)
    );

    public CameraRotationKeyframe(float time, Quaternionf rotation, KeyframeInterpolation interpolation) {
        this.time = time;
        this.rotation = rotation;
        this.interpolation = interpolation;
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public KeyframeInterpolation getInterpolation() {
        return interpolation;
    }
}
