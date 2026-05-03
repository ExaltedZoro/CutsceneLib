package net.exaltedzoro.cutscenelib.cutscene;

import net.exaltedzoro.cutscenelib.cutscene.keyframe.CutscenePositionKeyframe;
import net.exaltedzoro.cutscenelib.cutscene.keyframe.CutsceneRotationKeyframe;
import net.exaltedzoro.cutscenelib.cutscene.keyframe.KeyframeInterpolation;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

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

    public ArrayList<CutscenePositionKeyframe> positionKeyframes = new ArrayList<>();
    public ArrayList<CutsceneRotationKeyframe> rotationKeyframes = new ArrayList<>();

    public ArrayList<CutscenePositionKeyframe> currentPositionKeyframes = new ArrayList<>();
    public ArrayList<CutsceneRotationKeyframe> currentRotationKeyframes = new ArrayList<>();

    protected boolean paused;

    protected int tick = 0;

    public Cutscene(Vec3 origin) {
        this(origin, 0);
    }

    public Cutscene(Vec3 origin, float rotation) {
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

    public void addPositionKeyframe(Vec3 position, int tick, KeyframeInterpolation interpolation) {
        positionKeyframes.add(new CutscenePositionKeyframe(position, tick, interpolation));
    }

    public void addRotationKeyframe(Quaternionf rotation, int tick, KeyframeInterpolation interpolation) {
        rotationKeyframes.add(new CutsceneRotationKeyframe(rotation, tick, interpolation));
    }
}
