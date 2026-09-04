package net.exaltedzoro.cutscenelib.cutscene;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.exaltedzoro.cutscenelib.codec.ModCodecs;
import net.exaltedzoro.cutscenelib.cutscene.keyframe.Keyframe;
import net.exaltedzoro.cutscenelib.cutscene.track.Track;

import java.util.ArrayList;
import java.util.List;

public class CutsceneData {
    public static final CutsceneData EMPTY = new CutsceneData("empty", true, new ArrayList<>());

    private final String displayName;

    private final boolean hidePlayer;

    /**
     * The tracks of a cutscene. Each track contains the keyframes for a certain aspect of the cutscene (camera position/rotation, audio, actors etc.)
     */
    private ArrayList<Track<?>> tracks;

    public static final Codec<CutsceneData> CODEC = getCodec();

    public CutsceneData(String name, boolean hidePlayer, List<Track<?>> tracks) {
        this.displayName = name;
        this.hidePlayer = hidePlayer;
        this.tracks = new ArrayList<>(tracks);
        for (Track<?> track : tracks) {
            track.finalise();
        }
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean shouldHidePlayer() {
        return hidePlayer;
    }

    public ArrayList<Track<?>> getTracks() {
        return tracks;
    }

    public void addTrack(Track<?> track) {
        tracks.add(track);
    }

    private static Codec<CutsceneData> getCodec() {
        Codec<CutsceneData> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.STRING.fieldOf("display_name").forGetter(CutsceneData::getDisplayName),
                        Codec.BOOL.fieldOf("hide_player").orElse(error -> error, true).forGetter(CutsceneData::shouldHidePlayer),
                        ModCodecs.TRACK_CODEC.listOf().fieldOf("tracks").forGetter(CutsceneData::getTracks)
                ).apply(instance, CutsceneData::new)
        );

        return CODEC;
    }

    public float getLength() {
        float length = 0;
        for (Track<?> track : tracks) {
            Keyframe lastKeyframe = track.getKeyframes().getLast();
            if (lastKeyframe.getTime() > length) {
                length = lastKeyframe.getTime();
            }
        }

        return length;
    }
}
