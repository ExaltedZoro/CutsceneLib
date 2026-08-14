package net.exaltedzoro.cutscenelib.cutscene;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.exaltedzoro.cutscenelib.cutscene.codec.CutsceneCodecs;
import net.exaltedzoro.cutscenelib.cutscene.track.Track;

import java.util.ArrayList;
import java.util.List;

public class CutsceneData {
    private final String displayName;

    /**
     * The tracks of a cutscene. Each track contains the keyframes for a certain aspect of the cutscene (camera position/rotation, audio, actors etc.)
     */
    private ArrayList<Track<?>> tracks;

    public static final Codec<CutsceneData> CODEC = getCodec();

    public CutsceneData(String name, List<Track<?>> tracks) {
        this.displayName = name;
        this.tracks = new ArrayList<>(tracks);
    }

    public String getDisplayName() {
        return displayName;
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
                        CutsceneCodecs.TRACK_CODEC.listOf().fieldOf("tracks").forGetter(CutsceneData::getTracks)
                ).apply(instance, CutsceneData::new)
        );

        return CODEC;
    }
}
