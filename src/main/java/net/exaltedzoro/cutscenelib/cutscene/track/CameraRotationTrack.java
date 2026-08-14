package net.exaltedzoro.cutscenelib.cutscene.track;

import com.mojang.serialization.MapCodec;
import net.exaltedzoro.cutscenelib.cutscene.Cutscene;
import net.exaltedzoro.cutscenelib.cutscene.keyframe.CameraRotationKeyframe;
import net.exaltedzoro.cutscenelib.cutscene.keyframe.KeyframeUtil;
import net.exaltedzoro.cutscenelib.entity.CutsceneCameraEntity;
import net.minecraft.client.Minecraft;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

public class CameraRotationTrack extends Track<CameraRotationKeyframe> {

    private ArrayList<CameraRotationKeyframe> currentKeyframes = new ArrayList<>();

    private Quaternionf currentRotation;

    public CameraRotationTrack(List<CameraRotationKeyframe> keyframes) {
        super(new ArrayList<>(keyframes));
    }

    @Override
    public MapCodec<? extends Track<?>> type() {
        return ModTrackTypes.CAMERA_POSITION_TRACK_CODEC;
    }

    /**
     * Duplicates the end keyframes on the track. This is useful for anything that uses Catmull-Rom splines so that edge cases are covered by default.
     */
    private void duplicateEnds() {
        keyframes.addFirst(keyframes.getFirst());
        keyframes.addLast(keyframes.getLast());
    }

    @Override
    public void evaluate(float time, Cutscene cutscene) {
        // Only reevaluate current keyframes if necessary
        if (time >= currentKeyframes.get(2).getTime()) {
            recacheCurrentKeyframes(time);
        }

        if (Minecraft.getInstance().getCameraEntity() instanceof CutsceneCameraEntity cameraEntity) {
            Quaternionf newRotation = new Quaternionf();
            float progress = getCurrentProgress(time);

            switch (currentKeyframes.get(1).getInterpolation()) {
                case LINEAR -> {
                    newRotation = calculateLinear(progress, cutscene.getRotation());
                }
                case SMOOTH -> {
                    newRotation = calculateSmooth(progress, cutscene.getRotation());
                }
                case CUT -> {
                    newRotation = currentKeyframes.get(1).getRotation();
                }
            }

            currentRotation = newRotation;
        }
    }

    @Override
    public void finalise() {
        // Sort the keyframes in ascending time order, then duplicate the ends
        sortKeyframes();
        duplicateEnds();
    }

    private void recacheCurrentKeyframes(float time) {
        currentKeyframes = getCurrentKeyframes(time);
    }

    /**
     * Used to get the current set of keyframes used for Catmull-Rom interpolation
     * @param time How far the cutscene is through playing
     * @return An ArrayList of length 4 with elements T extends Keyframe
     */
    private ArrayList<CameraRotationKeyframe> getCurrentKeyframes(float time) {
        int currentKeyframe = getKeyframeIndex(time);
        ArrayList<CameraRotationKeyframe> toReturn = new ArrayList<>();
        toReturn.add(keyframes.get(currentKeyframe - 1));
        toReturn.add(keyframes.get(currentKeyframe));
        toReturn.add(keyframes.get(currentKeyframe + 1));
        toReturn.add(keyframes.get(currentKeyframe + 2));

        return toReturn;
    }

    private float getCurrentProgress(float time) {
        float timeIntoKeyframe = time - currentKeyframes.get(1).getTime();
        float transitionTime = currentKeyframes.get(2).getTime() - currentKeyframes.get(1).getTime();

        return timeIntoKeyframe / transitionTime;
    }

    public Quaternionf getCurrentRotation() {
        return currentRotation;
    }

    private Quaternionf calculateLinear(float progress, float rotation) {
        CameraRotationKeyframe keyframe1 = currentKeyframes.get(1);
        CameraRotationKeyframe keyframe2 = currentKeyframes.get(2);

        Quaternionf start = KeyframeUtil.getOrientedRotation(keyframe1.getRotation(), rotation);
        Quaternionf end = KeyframeUtil.getOrientedRotation(keyframe2.getRotation(), rotation);

        return start.slerp(end, progress);
    }

    private Quaternionf calculateSmooth(float progress, float rotation) {
        CameraRotationKeyframe keyframe1 = currentKeyframes.get(1);
        CameraRotationKeyframe keyframe2 = currentKeyframes.get(2);

        Quaternionf start = KeyframeUtil.getOrientedRotation(keyframe1.getRotation(), rotation);
        Quaternionf end = KeyframeUtil.getOrientedRotation(keyframe2.getRotation(), rotation);

        return start.nlerp(end, progress);
    }
}
