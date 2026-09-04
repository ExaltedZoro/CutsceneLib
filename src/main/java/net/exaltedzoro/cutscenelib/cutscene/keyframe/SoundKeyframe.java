package net.exaltedzoro.cutscenelib.cutscene.keyframe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class SoundKeyframe extends Keyframe {
    private final Holder<SoundEvent> sound;
    private final Optional<Either<Vec3, String>> position;

    private static final Codec<Either<Vec3, String>> POSITION_CODEC = Codec.either(
            Vec3.CODEC,
            Codec.STRING
    );

    public static final Codec<SoundKeyframe> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("time").forGetter(SoundKeyframe::getTime),
                    SoundEvent.CODEC.fieldOf("sound").forGetter(SoundKeyframe::getSound),
                    POSITION_CODEC.optionalFieldOf("position").forGetter(SoundKeyframe::getPosition)
            ).apply(instance, SoundKeyframe::new)
    );


    public SoundKeyframe(float time, Holder<SoundEvent> sound, Optional<Either<Vec3, String>> position) {
        this.time = time;
        this.sound = sound;
        this.position = position;
    }

    public Holder<SoundEvent> getSound() {
        return sound;
    }

    public Optional<Either<Vec3, String>> getPosition() {
        return position;
    }

    @Override
    public Keyframe copy() {
        return new SoundKeyframe(time, sound, position);
    }
}
