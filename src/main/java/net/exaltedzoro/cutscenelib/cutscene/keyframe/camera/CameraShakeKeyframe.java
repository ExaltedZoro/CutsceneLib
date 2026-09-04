package net.exaltedzoro.cutscenelib.cutscene.keyframe.camera;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.exaltedzoro.cutscenelib.cutscene.keyframe.Keyframe;

public class CameraShakeKeyframe extends Keyframe {

    private final float duration;
    private final float speed;
    private final float intensity;
    private final float fadeIn;
    private final float fadeOut;

    public static final Codec<CameraShakeKeyframe> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("time").forGetter(CameraShakeKeyframe::getTime),
                    Codec.FLOAT.fieldOf("duration").forGetter(CameraShakeKeyframe::getDuration),
                    Codec.FLOAT.fieldOf("speed").orElse(error -> error, 1f).forGetter(CameraShakeKeyframe::getSpeed),
                    Codec.FLOAT.fieldOf("intensity").orElse(error -> error, 1f).forGetter(CameraShakeKeyframe::getIntensity),
                    Codec.FLOAT.fieldOf("fadeIn").orElse(error -> error, 0.1f).forGetter(CameraShakeKeyframe::getFadeIn),
                    Codec.FLOAT.fieldOf("fadeOut").orElse(error -> error, 0.1f).forGetter(CameraShakeKeyframe::getFadeOut)
            ).apply(instance, CameraShakeKeyframe::new)
    );

    public CameraShakeKeyframe(float time, float duration, float speed, float intensity, float fadeIn, float fadeOut) {
        this.time = time;
        this.duration = duration;
        this.speed = speed;
        this.intensity = intensity;
        this.fadeIn = fadeIn;
        this.fadeOut = fadeOut;
    }

    public float getDuration() {
        return duration;
    }

    public float getSpeed() {
        return speed;
    }

    public float getIntensity() {
        return intensity;
    }

    public float getFadeIn() {
        return fadeIn;
    }

    public float getFadeOut() {
        return fadeOut;
    }

    @Override
    public Keyframe copy() {
        return new CameraShakeKeyframe(time, duration, speed, intensity, fadeIn, fadeOut);
    }
}
