package net.exaltedzoro.cutscenelib.cutscene.track;

import com.mojang.serialization.MapCodec;
import net.exaltedzoro.cutscenelib.cutscene.Cutscene;
import net.exaltedzoro.cutscenelib.cutscene.keyframe.Keyframe;

import java.util.ArrayList;
import java.util.Comparator;

public abstract class Track<T extends Keyframe> {
    ArrayList<T> keyframes;

    public Track(ArrayList<T> keyframes) {
        this.keyframes = keyframes;
    }

    public abstract MapCodec<? extends Track<?>> type();

    public void addKeyframe(T keyframe) {
        keyframes.add(keyframe);
    }

    public void addKeyframes(ArrayList<T> newKeyframes) {
        keyframes.addAll(newKeyframes);
    }

    public ArrayList<T> getKeyframes() {
        return keyframes;
    }

    /**
     * Sorts keyframes in chronological order.
     * Used to make sure that keyframes are in the right position in the list when pulling from JSON, even if the JSON order is wrong
     */
    protected void sortKeyframes() {
        keyframes.sort(Comparator.comparingDouble(Keyframe::getTime));
    }

    /**
     * Core evaluation method for a given track. This will be called every render tick to evaluate what has to happen at the given time
     * @param time How far the cutscene has progressed
     * @param cutscene The cutscene being evaluated for
     */
    public abstract void evaluate(float time, Cutscene cutscene);

    /**
     * Called when the keyframes for a track have all been added. Used to do any necessary preprocessing on the keyframes (e.g. sorting, duplicating ends etc.)
     */
    public abstract void finalise();

    public T getKeyframe(float time) {
        int index = 1;
        for (int i = 1; i < keyframes.size() - 1; i++) {
            if (keyframes.get(i).getTime() <= time && keyframes.get(i + 1).getTime() >= time) {
                index = i;
                break;
            }
        }
        return keyframes.get(index);
    }

    public int getKeyframeIndex(float time) {
        int index = 1;
        for (int i = 1; i < keyframes.size() - 1; i++) {
            if (keyframes.get(i).getTime() <= time && keyframes.get(i + 1).getTime() >= time) {
                index = i;
                break;
            }
        }
        return index;
    }
}
