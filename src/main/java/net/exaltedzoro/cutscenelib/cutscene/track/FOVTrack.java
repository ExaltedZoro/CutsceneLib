package net.exaltedzoro.cutscenelib.cutscene.track;

import com.mojang.serialization.MapCodec;
import net.exaltedzoro.cutscenelib.cutscene.Cutscene;
import net.exaltedzoro.cutscenelib.cutscene.keyframe.FOVKeyframe;
import net.exaltedzoro.cutscenelib.entity.CutsceneCameraEntity;
import net.exaltedzoro.cutscenelib.registry.ModTrackTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class FOVTrack extends Track<FOVKeyframe> {

    private ArrayList<FOVKeyframe> currentKeyframes = new ArrayList<>();

    private double currentFOV = 0;

    public FOVTrack(List<FOVKeyframe> keyframes) {
        super(new ArrayList<>(keyframes));
    }

    @Override
    public MapCodec<? extends Track<?>> type() {
        return ModTrackTypes.FOV_TRACK_CODEC;
    }

    public double getCurrentFOV() {
        return currentFOV;
    }

    @Override
    public void evaluate(float time, Cutscene cutscene) {
        // Only reevaluate current keyframes if necessary
        if (time >= currentKeyframes.get(2).getTime() || !cutscene.isInitialised()) {
            recacheCurrentKeyframes(time);
        }

        CutsceneCameraEntity cameraEntity = (CutsceneCameraEntity) Minecraft.getInstance().getCameraEntity();

        double newFOV = 0;
        float progress = getCurrentProgress(time);
        if (keyframes.size() <= 3) {
            newFOV = keyframes.get(1).getFOV();
        } else {
            switch (currentKeyframes.get(1).getInterpolation()) {
                case LINEAR -> {
                    newFOV = calculateLinear(progress);
                }
                case SMOOTH -> {
                    newFOV = calculateSmooth(progress);
                }
                case CUT -> {
                    newFOV = currentKeyframes.get(1).getFOV();
                }
            }
        }

        currentFOV = newFOV;
    }

    @Override
    public void finalise() {
        sortKeyframes();
        duplicateEnds();

        recacheCurrentKeyframes(0);
    }

    private void duplicateEnds() {
        keyframes.addFirst((FOVKeyframe) keyframes.getFirst().copy());
        keyframes.addLast((FOVKeyframe) keyframes.getLast().copy());
    }

    private void recacheCurrentKeyframes(float time) {
        currentKeyframes = getCurrentKeyframes(time);
    }

    private ArrayList<FOVKeyframe> getCurrentKeyframes(float time) {
        int currentKeyframe;

        if (time >= keyframes.get(keyframes.size() - 2).getTime()) {
            currentKeyframe = keyframes.size() - 2;
        } else {
            currentKeyframe = getKeyframeIndex(time);
        }

        ArrayList<FOVKeyframe> toReturn;

        if (keyframes.size() <= 4) {
            toReturn = new ArrayList<>(keyframes);
        } else {
            toReturn = new ArrayList<>();

            if (currentKeyframe > keyframes.size() - 3) {
                toReturn.add(keyframes.get(currentKeyframe - 2));
                toReturn.add(keyframes.get(currentKeyframe - 1));
                toReturn.add(keyframes.get(currentKeyframe));
                toReturn.add(keyframes.get(currentKeyframe + 1));
            } else {
                toReturn.add(keyframes.get(currentKeyframe - 1));
                toReturn.add(keyframes.get(currentKeyframe));
                toReturn.add(keyframes.get(currentKeyframe + 1));
                toReturn.add(keyframes.get(currentKeyframe + 2));
            }
        }
        return toReturn;
    }

    private float getCurrentProgress(float time) {
        float timeIntoKeyframe = time - currentKeyframes.get(1).getTime();
        float transitionTime = currentKeyframes.get(2).getTime() - currentKeyframes.get(1).getTime();

        float progress = timeIntoKeyframe / transitionTime;

        if (Float.isNaN(progress)) {
            progress = 0;
        }

        return Math.clamp(progress, 0, 1);
    }

    private double calculateLinear(float progress) {
        double fov1 = currentKeyframes.get(1).getFOV();
        double fov2 = currentKeyframes.get(2).getFOV();

        return Mth.lerp(progress, fov1, fov2);
    }

    private double calculateSmooth(float progress) {
        float fov1 = (float) currentKeyframes.get(0).getFOV();
        float fov2 = (float) currentKeyframes.get(1).getFOV();
        float fov3 = (float) currentKeyframes.get(2).getFOV();
        float fov4 = (float) currentKeyframes.get(3).getFOV();

        return Mth.catmullrom(progress, fov1, fov2, fov3, fov4);
    }
}
