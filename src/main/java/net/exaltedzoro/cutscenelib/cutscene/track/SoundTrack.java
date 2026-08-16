package net.exaltedzoro.cutscenelib.cutscene.track;

import com.mojang.serialization.MapCodec;
import net.exaltedzoro.cutscenelib.cutscene.Cutscene;
import net.exaltedzoro.cutscenelib.cutscene.keyframe.SoundKeyframe;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;

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
                SoundEvent sound = keyframe.getSound().value();

                // Any warnings with getCameraEntity returning null can be ignored since this code should only be run inside a check that it is not null
                Minecraft.getInstance().getCameraEntity().playSound(sound);
            }
        }

        previousTime = time;
    }

    @Override
    public void finalise() {
        sortKeyframes();
    }
}
