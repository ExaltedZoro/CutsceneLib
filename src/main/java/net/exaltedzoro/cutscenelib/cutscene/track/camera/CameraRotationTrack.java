package net.exaltedzoro.cutscenelib.cutscene.track.camera;

import com.mojang.serialization.MapCodec;
import net.exaltedzoro.cutscenelib.CutsceneLib;
import net.exaltedzoro.cutscenelib.cutscene.Cutscene;
import net.exaltedzoro.cutscenelib.cutscene.keyframe.camera.CameraRotationKeyframe;
import net.exaltedzoro.cutscenelib.cutscene.keyframe.KeyframeUtil;
import net.exaltedzoro.cutscenelib.cutscene.track.Track;
import net.exaltedzoro.cutscenelib.entity.CutsceneCameraEntity;
import net.exaltedzoro.cutscenelib.registry.ModTrackTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class CameraRotationTrack extends Track<CameraRotationKeyframe> {

    private ArrayList<CameraRotationKeyframe> currentKeyframes = new ArrayList<>();

    private Vec3 currentRotation;

    public CameraRotationTrack(List<CameraRotationKeyframe> keyframes) {
        super(new ArrayList<>(keyframes));
        currentRotation = keyframes.getFirst().getRotation();
    }

    @Override
    public MapCodec<? extends Track<?>> type() {
        return ModTrackTypes.CAMERA_ROTATION_TRACK_CODEC;
    }

    /**
     * Duplicates the end keyframes on the track. This is useful for anything that uses Catmull-Rom splines so that edge cases are covered by default.
     */
    private void duplicateEnds() {
        keyframes.addFirst((CameraRotationKeyframe) keyframes.getFirst().copy());
        keyframes.addLast((CameraRotationKeyframe) keyframes.getLast().copy());
    }

    @Override
    public void evaluate(float time, Cutscene cutscene) {
        CutsceneLib.LOGGER.info("Time: {}", time);
        // Only reevaluate current keyframes if necessary
        if (time >= currentKeyframes.get(2).getTime() || !cutscene.isInitialised()) {
            recacheCurrentKeyframes(time);
        }

        Vec3 newRotation = Vec3.ZERO;
        float progress = getCurrentProgress(time);

        if (keyframes.size() <= 3) {
            newRotation = keyframes.get(1).getRotation();
        } else {
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
        }

        if (Minecraft.getInstance().getCameraEntity() instanceof CutsceneCameraEntity cameraEntity) {
            cameraEntity.absRotateTo((float) currentRotation.y(), (float) currentRotation.x());
            //cameraEntity.setXRot((float) newRotation.x());
            cameraEntity.setYRot((float) newRotation.y() % 360f);
        }
        currentRotation = newRotation;
    }

    @Override
    public void finalise() {
        // Sort the keyframes in ascending time order, then duplicate the ends
        sortKeyframes();
        duplicateEnds();

        recacheCurrentKeyframes(0);
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
        int currentKeyframe;

        if (time >= keyframes.get(keyframes.size() - 2).getTime()) {
            currentKeyframe = keyframes.size() - 2;
        } else {
            currentKeyframe = getKeyframeIndex(time);
        }

        ArrayList<CameraRotationKeyframe> toReturn;

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

    private CameraRotationKeyframe getLastUniqueKeyframe() {
        return keyframes.get(keyframes.size() - 2);
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

    public Vec3 getCurrentRotation() {
        return currentRotation;
    }

    private Vec3 calculateLinear(float progress, float rotation) {
        CameraRotationKeyframe keyframe1 = currentKeyframes.get(1);
        CameraRotationKeyframe keyframe2 = currentKeyframes.get(2);

        Vec3 start = KeyframeUtil.getOrientedRotation(keyframe1.getRotation(), rotation);
        Vec3 end = KeyframeUtil.getOrientedRotation(keyframe2.getRotation(), rotation);

        return start.lerp(end, progress);
    }

    private Vec3 calculateSmooth(float progress, float rotation) {
        CameraRotationKeyframe keyframe1 = currentKeyframes.get(0);
        CameraRotationKeyframe keyframe2 = currentKeyframes.get(1);
        CameraRotationKeyframe keyframe3 = currentKeyframes.get(2);
        CameraRotationKeyframe keyframe4 = currentKeyframes.get(3);

        Vec3 rotation1 = KeyframeUtil.getOrientedRotation(keyframe1.getRotation(), rotation);
        Vec3 rotation2 = KeyframeUtil.getOrientedRotation(keyframe2.getRotation(), rotation);
        Vec3 rotation3 = KeyframeUtil.getOrientedRotation(keyframe3.getRotation(), rotation);
        Vec3 rotation4 = KeyframeUtil.getOrientedRotation(keyframe4.getRotation(), rotation);

        float x = Mth.catmullrom(progress, (float) rotation1.x(), (float) rotation2.x(), (float) rotation3.x(), (float) rotation4.x());
        float y = Mth.catmullrom(progress, (float) rotation1.y(), (float) rotation2.y(), (float) rotation3.y(), (float) rotation4.y());
        float z = Mth.catmullrom(progress, (float) rotation1.z(), (float) rotation2.z(), (float) rotation3.z(), (float) rotation4.z());

        return new Vec3(x, y, z);
    }
}
