package net.exaltedzoro.cutscenelib.cutscene;

import net.exaltedzoro.cutscenelib.cutscene.track.Track;

import java.util.ArrayList;

public class CutsceneData {
    private final String name;

    /**
     * The tracks of a cutscene. Each track contains the keyframes for a certain aspect of the cutscene (camera position/rotation, audio, actors etc.)
     */
    private ArrayList<Track<?>> tracks = new ArrayList<>();

    public CutsceneData(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Track<?>> getTracks() {
        return tracks;
    }

    public void addTrack(Track<?> track) {
        tracks.add(track);
    }
}
