package net.exaltedzoro.cutscenelib.cutscene.keyframe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;

public class SoundKeyframe extends Keyframe {
    private final Holder<SoundEvent> sound;

    public static final Codec<SoundKeyframe> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("time").forGetter(SoundKeyframe::getTime),
                    SoundEvent.CODEC.fieldOf("sound").forGetter(SoundKeyframe::getSound)
            ).apply(instance, SoundKeyframe::new)
    );

    public SoundKeyframe(float time, Holder<SoundEvent> sound) {
        this.time = time;
        this.sound = sound;
    }

    public Holder<SoundEvent> getSound() {
        return sound;
    }

    @Override
    public Keyframe copy() {
        return new SoundKeyframe(time, sound);
    }
}
