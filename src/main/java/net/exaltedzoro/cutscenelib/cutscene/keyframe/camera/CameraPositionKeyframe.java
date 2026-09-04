package net.exaltedzoro.cutscenelib.cutscene.keyframe.camera;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.exaltedzoro.cutscenelib.cutscene.keyframe.Keyframe;
import net.exaltedzoro.cutscenelib.cutscene.keyframe.KeyframeInterpolation;
import net.minecraft.world.phys.Vec3;

public class CameraPositionKeyframe extends Keyframe {
    private final Vec3 position;

    private final KeyframeInterpolation interpolation;

    public static final Codec<CameraPositionKeyframe> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
                Codec.FLOAT.fieldOf("time").forGetter(CameraPositionKeyframe::getTime),
                Vec3.CODEC.fieldOf("position").forGetter(CameraPositionKeyframe::getPosition),
                KeyframeInterpolation.CODEC.fieldOf("interpolation").forGetter(CameraPositionKeyframe::getInterpolation)
        ).apply(instance, CameraPositionKeyframe::new)
    );

    public CameraPositionKeyframe(float time, Vec3 position, KeyframeInterpolation interpolation) {
        this.time = time;
        this.position = position;
        this.interpolation = interpolation;
    }

    public Vec3 getPosition() {
        return position;
    }

    public KeyframeInterpolation getInterpolation() {
        return interpolation;
    }

    @Override
    public Keyframe copy() {
        return new CameraPositionKeyframe(time, position, interpolation);
    }
}
