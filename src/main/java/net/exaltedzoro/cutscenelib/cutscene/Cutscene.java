package net.exaltedzoro.cutscenelib.cutscene;

import net.exaltedzoro.cutscenelib.cutscene.keyframe.CameraPositionKeyframe;
import net.exaltedzoro.cutscenelib.cutscene.keyframe.CameraRotationKeyframe;
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

    private CutsceneData data;

    public ArrayList<CameraPositionKeyframe> positionKeyframes = new ArrayList<>();
    public ArrayList<CameraRotationKeyframe> rotationKeyframes = new ArrayList<>();

    public ArrayList<CameraPositionKeyframe> currentPositionKeyframes = new ArrayList<>();
    public ArrayList<CameraRotationKeyframe> currentRotationKeyframes = new ArrayList<>();

    protected boolean paused;

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

    public void addPositionKeyframe(Vec3 position, int tick, KeyframeInterpolation interpolation) {
        positionKeyframes.add(new CameraPositionKeyframe(tick, position, interpolation));
    }

    public void addRotationKeyframe(Quaternionf rotation, int tick, KeyframeInterpolation interpolation) {
        rotationKeyframes.add(new CameraRotationKeyframe(tick, rotation, interpolation));
    }

    public ArrayList<CameraPositionKeyframe> getCurrentPositionKeyframes() {
        int time = getTick();
        ArrayList<CameraPositionKeyframe> list = new ArrayList<>();
        for (CameraPositionKeyframe keyframe : positionKeyframes) {
            int index = positionKeyframes.indexOf(keyframe);
            if (keyframe.getTick() <= time) {
                if (positionKeyframes.size() <= 4) {
                    switch (positionKeyframes.size()) {
                        case 2 -> {
                            list.add(positionKeyframes.get(0));
                            list.add(positionKeyframes.get(0));
                            list.add(positionKeyframes.get(1));
                            list.add(positionKeyframes.get(1));
                        }
                        case 3 -> {
                            int maxIndex = positionKeyframes.size() - 1;

                            list.add(positionKeyframes.get(Math.max(0, index - 1)));
                            list.add(positionKeyframes.get(index));
                            list.add(positionKeyframes.get(Math.min(maxIndex, index + 1)));
                            list.add(positionKeyframes.get(Math.min(maxIndex, index + 2)));
                        }
                        case 4 -> {
                            list = positionKeyframes;
                        }
                    }
                } else {
                    if (index == 0) {
                        list.add(keyframe);
                        list.add(keyframe);
                        list.add(positionKeyframes.get(index + 1));
                        list.add(positionKeyframes.get(index + 2));
                    } else if (index == positionKeyframes.size() - 1) {
                        list.add(positionKeyframes.get(index - 2));
                        list.add(positionKeyframes.get(index - 1));
                        list.add(keyframe);
                        list.add(keyframe);
                    } else if (index == positionKeyframes.size() - 2) {
                        list.add(positionKeyframes.get(index - 1));
                        list.add(keyframe);
                        list.add(positionKeyframes.get(index + 1));
                        list.add(positionKeyframes.get(index + 1));
                    } else {
                        list.add(positionKeyframes.get(index - 1));
                        list.add(keyframe);
                        list.add(positionKeyframes.get(index + 1));
                        list.add(positionKeyframes.get(index + 2));
                    }
                }
            }
        }
        return list;
    }

    public ArrayList<CameraRotationKeyframe> getCurrentRotationKeyframes() {
        int time = getTick();
        ArrayList<CameraRotationKeyframe> list = new ArrayList<>();
        for (CameraRotationKeyframe keyframe : rotationKeyframes) {
            int index = rotationKeyframes.indexOf(keyframe);
            if (keyframe.getTick() <= time) {
                if (rotationKeyframes.size() <= 4) {
                    switch (rotationKeyframes.size()) {
                        case 2 -> {
                            list.add(rotationKeyframes.get(0));
                            list.add(rotationKeyframes.get(0));
                            list.add(rotationKeyframes.get(1));
                            list.add(rotationKeyframes.get(1));
                        }
                        case 3 -> {
                            if (index == 0) {
                                list.add(rotationKeyframes.get(0));
                                list.add(rotationKeyframes.get(0));
                                list.add(rotationKeyframes.get(1));
                                list.add(rotationKeyframes.get(2));
                            } else if (index >= 1) {
                                list.add(rotationKeyframes.get(0));
                                list.add(rotationKeyframes.get(1));
                                list.add(rotationKeyframes.get(2));
                                list.add(rotationKeyframes.get(2));
                            }
                        }
                        case 4 -> {
                            list = rotationKeyframes;
                        }
                    }
                } else {
                    if (index == 0) {
                        list.add(keyframe);
                        list.add(keyframe);
                        list.add(rotationKeyframes.get(index + 1));
                        list.add(rotationKeyframes.get(index + 2));
                    } else if (index == rotationKeyframes.size() - 1) {
                        list.add(rotationKeyframes.get(index - 2));
                        list.add(rotationKeyframes.get(index - 1));
                        list.add(keyframe);
                        list.add(keyframe);
                    } else if (index == rotationKeyframes.size() - 2) {
                        list.add(rotationKeyframes.get(index - 1));
                        list.add(keyframe);
                        list.add(rotationKeyframes.get(index + 1));
                        list.add(rotationKeyframes.get(index + 1));
                    } else {
                        list.add(rotationKeyframes.get(index - 1));
                        list.add(keyframe);
                        list.add(rotationKeyframes.get(index + 1));
                        list.add(rotationKeyframes.get(index + 2));
                    }
                }
            }
        }
        return list;
    }
}
