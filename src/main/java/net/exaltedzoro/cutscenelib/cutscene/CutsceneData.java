package net.exaltedzoro.cutscenelib.cutscene;

import net.exaltedzoro.cutscenelib.cutscene.keyframe.CameraPositionKeyframe;
import net.exaltedzoro.cutscenelib.cutscene.keyframe.CameraRotationKeyframe;

import java.util.ArrayList;

public class CutsceneData {
    private final String name;

    /**
     * The tracks of a cutscene. Each track contains the keyframes for a certain aspect of the cutscene (camera position/rotation, audio, actors etc.)
     */
    private ArrayList<Track> tracks = new ArrayList<>();

    private ArrayList<CameraPositionKeyframe> positionKeyframes = new ArrayList<>();
    private ArrayList<CameraRotationKeyframe> rotationKeyframes = new ArrayList<>();

    public CutsceneData(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ArrayList<CameraPositionKeyframe> getPositionKeyframes() {
        return positionKeyframes;
    }

    public ArrayList<CameraRotationKeyframe> getRotationKeyframes() {
        return rotationKeyframes;
    }

    public void addTrack(Track track) {
        tracks.add(track);
    }
}
