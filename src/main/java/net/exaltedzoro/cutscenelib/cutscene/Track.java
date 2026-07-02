package net.exaltedzoro.cutscenelib.cutscene;

import net.exaltedzoro.cutscenelib.cutscene.keyframe.Keyframe;

import java.util.ArrayList;

public class Track<T extends Keyframe> {
    ArrayList<T> keyframes;

    public Track() {}

    public void addKeyframe(T keyframe) {
        keyframes.add(keyframe);
    }

    public ArrayList<T> getKeyframes() {
        return keyframes;
    }
}
