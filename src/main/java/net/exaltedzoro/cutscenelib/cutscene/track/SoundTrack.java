package net.exaltedzoro.cutscenelib.cutscene.track;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import net.exaltedzoro.cutscenelib.cutscene.Cutscene;
import net.exaltedzoro.cutscenelib.cutscene.keyframe.SoundKeyframe;
import net.exaltedzoro.cutscenelib.entity.CutsceneCameraEntity;
import net.exaltedzoro.cutscenelib.registry.ModTrackTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class SoundTrack extends Track<SoundKeyframe> {

    private float previousTime = 0;

    public SoundTrack(List<SoundKeyframe> keyframes) {
        super(new ArrayList<>(keyframes));
    }

    @Override
    public MapCodec<? extends Track<?>> type() {
        return ModTrackTypes.SOUND_TRACK_CODEC;
    }

    @Override
    public void evaluate(float time, Cutscene cutscene) {
        for (SoundKeyframe keyframe : getKeyframes()) {
            // Only play a sound if we've just crossed the threshold for its keyframe
            if (this.previousTime < keyframe.getTime() && time >= keyframe.getTime()) {
                CutsceneCameraEntity cameraEntity = (CutsceneCameraEntity) Minecraft.getInstance().getCameraEntity();
                SoundEvent sound = keyframe.getSound().value();

                Vec3 position;
                if (keyframe.getPosition().isEmpty()) {
                    position = cameraEntity.position();
                } else {
                    Either<Vec3, String> either = keyframe.getPosition().get();
                    if (either.left().isPresent()) {
                        position = either.left().get().add(cutscene.getOrigin());
                    } else {
                        // TODO: Implement actor position fetch when that system is implemented
                        position = cameraEntity.position();
                    }
                }

                Minecraft.getInstance().level.playLocalSound(position.x(), position.y(), position.z(), sound, SoundSource.AMBIENT, 1, 1, false);
            }
        }

        previousTime = time;
    }

    @Override
    public void finalise() {
        sortKeyframes();
    }
}
