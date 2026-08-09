package net.exaltedzoro.cutscenelib.cutscene.track;

import net.exaltedzoro.cutscenelib.cutscene.Cutscene;
import net.exaltedzoro.cutscenelib.cutscene.keyframe.CameraPositionKeyframe;
import net.exaltedzoro.cutscenelib.cutscene.keyframe.KeyframeUtil;
import net.exaltedzoro.cutscenelib.entity.CutsceneCameraEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

public class CameraPositionTrack extends Track<CameraPositionKeyframe> {

    private ArrayList<CameraPositionKeyframe> currentKeyframes = new ArrayList<>();

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
            Vec3 newPosition = Vec3.ZERO;
            float progress = getCurrentProgress(time);

            switch (currentKeyframes.get(1).getInterpolation()) {
                case LINEAR -> {
                    newPosition = calculateLinear(progress, cutscene.getOrigin(), cutscene.getRotation());
                }
                case SMOOTH ->  {
                    newPosition = calculateSmooth(progress, cutscene.getOrigin(), cutscene.getRotation());
                }
                case CUT -> {
                    newPosition = currentKeyframes.get(1).getPosition().add(cutscene.getOrigin());
                }
            }

            cameraEntity.setPos(newPosition);
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
    private ArrayList<CameraPositionKeyframe> getCurrentKeyframes(float time) {
        int currentKeyframe = getKeyframeIndex(time);
        ArrayList<CameraPositionKeyframe> toReturn = new ArrayList<>();
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

    private Vec3 calculateLinear(float progress, Vec3 origin, float rotation) {
        CameraPositionKeyframe start =  keyframes.get(1);
        CameraPositionKeyframe end = keyframes.get(2);

        Vec3 startPos = KeyframeUtil.getOrientedPosition(start.getPosition(), rotation).add(origin);
        Vec3 endPos = KeyframeUtil.getOrientedPosition(end.getPosition(), rotation).add(origin);

        double x = Mth.lerp(progress, startPos.x(), endPos.x());
        double y = Mth.lerp(progress, startPos.y(), endPos.y());
        double z = Mth.lerp(progress, startPos.z(), endPos.z());

        return new Vec3(x, y, z);
    }

    private Vec3 calculateSmooth(float progress, Vec3 origin, float rotation) {
        CameraPositionKeyframe keyframe1 = currentKeyframes.get(0);
        CameraPositionKeyframe keyframe2 = currentKeyframes.get(1);
        CameraPositionKeyframe keyframe3 = currentKeyframes.get(2);
        CameraPositionKeyframe keyframe4 = currentKeyframes.get(3);

        Vec3 point1 = KeyframeUtil.getOrientedPosition(keyframe1.getPosition(), rotation).add(origin);
        Vec3 point2 = KeyframeUtil.getOrientedPosition(keyframe2.getPosition(), rotation).add(origin);
        Vec3 point3 = KeyframeUtil.getOrientedPosition(keyframe3.getPosition(), rotation).add(origin);
        Vec3 point4 = KeyframeUtil.getOrientedPosition(keyframe4.getPosition(), rotation).add(origin);

        float x = Mth.catmullrom(progress, (float) point1.x(), (float) point2.x(), (float) point3.x(), (float) point4.x());
        float y = Mth.catmullrom(progress, (float) point1.y(), (float) point2.y(), (float) point3.y(), (float) point4.y());
        float z = Mth.catmullrom(progress, (float) point1.z(), (float) point2.z(), (float) point3.z(), (float) point4.z());

        return new Vec3(x, y, z);
    }
}
