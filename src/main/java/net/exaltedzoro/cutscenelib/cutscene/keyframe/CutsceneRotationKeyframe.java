package net.exaltedzoro.cutscenelib.cutscene.keyframe;

import org.joml.Quaternionf;

public record CutsceneRotationKeyframe(Quaternionf rotation, int tick, KeyframeInterpolation interpolation) {
}
