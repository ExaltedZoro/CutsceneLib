package net.exaltedzoro.cutscenelib.cutscene;

import net.exaltedzoro.cutscenelib.cutscene.track.Track;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

public class Cutscene {
    public static final Cutscene EMPTY = new Cutscene(CutsceneData.EMPTY, Vec3.ZERO, 0);

    /**
     * The origin of the cutscene in world coordinates. All keyframe positions will be taken relative to this vector.
     */
    protected Vec3 origin;

    /**
     * The y-rotation of the cutscene in degrees. All keyframes for the camera, actors etc. will be rotated around the origin by this value.
     */
    protected float rotation;

    private final CutsceneData data;

    protected boolean paused;

    protected long startTime;

    private boolean initialised = false;

    public Cutscene(CutsceneData data, Vec3 origin, long startTime) {
        this(data, origin, 0, startTime);
    }

    public Cutscene(CutsceneData data, Vec3 origin, float rotation, long startTime) {
        this.data = data;
        this.origin = origin;
        this.rotation = rotation;
        this.startTime = startTime;
    }

    public Vec3 getOrigin() {
        return origin;
    }

    public void setOrigin(Vec3 pos) {
        origin = pos;
    }

    public CutsceneData getData() {
        return data;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public long getStartTime() {
        return startTime;
    }

    public float getRotation() {
        return rotation;
    }

    public boolean isInitialised() {
        return initialised;
    }

    // Initialises all tracks to their starting position. Primarily used to get the camera in the right place when a cutscene is first started
    public void initialise() {
        for (Track<?> track : data.getTracks()) {
            track.evaluate(0, this);
        }

        this.initialised = true;
    }

    public void tick(float partialTick) {
        Level level = Minecraft.getInstance().level;
        float elapsedTime = level.getGameTime() - startTime + partialTick;

        ArrayList<Track<?>> tracks = data.getTracks();

        for (Track<?> track : tracks) {
            track.evaluate(elapsedTime, this);
        }
    }
}
