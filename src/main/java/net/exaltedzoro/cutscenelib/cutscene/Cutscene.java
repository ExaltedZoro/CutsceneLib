package net.exaltedzoro.cutscenelib.cutscene;

import net.exaltedzoro.cutscenelib.cutscene.track.Track;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

public class Cutscene {
    /**
     * The origin of the cutscene in world coordinates. All keyframe positions will be taken relative to this vector.
     */
    protected Vec3 origin;

    /**
     * The y-rotation of the cutscene in degrees. All keyframes for the camera, actors etc. will be rotated around the origin by this value.
     */
    protected float rotation;

    private CutsceneData data;

    protected boolean paused;

    protected long startTime;

    protected int tick = 0;

    public Cutscene(CutsceneData data, Vec3 origin) {
        this(data, origin, 0);
    }

    public Cutscene(CutsceneData data, Vec3 origin, float rotation) {
        this.data = data;
        this.origin = origin;
        this.rotation = rotation;
    }

    public Vec3 getOrigin() {
        return origin;
    }

    public void setOrigin(Vec3 pos) {
        origin = pos;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public int getTick() {
        return tick;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }

    public float getRotation() {
        return rotation;
    }

    public void tick(float partialTick) {
        Level level = Minecraft.getInstance().level;
        assert (level != null);
        float elapsedTime = level.getGameTime() - startTime + partialTick;

        ArrayList<Track<?>> tracks = data.getTracks();

        for (Track<?> track : tracks) {
            track.evaluate(elapsedTime, this);
        }
    }
}
