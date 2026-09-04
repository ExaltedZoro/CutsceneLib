package net.exaltedzoro.cutscenelib.cutscene.keyframe.camera;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.exaltedzoro.cutscenelib.cutscene.keyframe.Keyframe;
import net.exaltedzoro.cutscenelib.cutscene.keyframe.KeyframeInterpolation;
import net.minecraft.world.phys.Vec3;

public class CameraRotationKeyframe extends Keyframe {
    private Vec3 rotation;

    private KeyframeInterpolation interpolation;

    public static final Codec<CameraRotationKeyframe> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("time").forGetter(CameraRotationKeyframe::getTime),
                    Vec3.CODEC.fieldOf("rotation").forGetter(CameraRotationKeyframe::getRotation),
                    KeyframeInterpolation.CODEC.fieldOf("interpolation").forGetter(CameraRotationKeyframe::getInterpolation)
            ).apply(instance, CameraRotationKeyframe::new)
    );

    public CameraRotationKeyframe(float time, Vec3 rotation, KeyframeInterpolation interpolation) {
        this.time = time;
        this.rotation = rotation;
        this.interpolation = interpolation;
    }

    public Vec3 getRotation() {
        return rotation;
    }

    public KeyframeInterpolation getInterpolation() {
        return interpolation;
    }

    @Override
    public Keyframe copy() {
        return new CameraRotationKeyframe(time, rotation, interpolation);
    }
}
