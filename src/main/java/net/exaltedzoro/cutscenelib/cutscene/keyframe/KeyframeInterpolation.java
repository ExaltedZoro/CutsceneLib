package net.exaltedzoro.cutscenelib.cutscene.keyframe;

public enum KeyframeInterpolation {
    /**
     * Interpolate in a straight line between each keyframe
     */
    LINEAR,
    /**
     * Interpolate on a smooth curve between each keyframe, using Centripetal Catmull-Rom interpolation
     */
    SMOOTH,
    /**
     * Cut instantly between each keyframe, no interpolation
     */
    CUT
}
