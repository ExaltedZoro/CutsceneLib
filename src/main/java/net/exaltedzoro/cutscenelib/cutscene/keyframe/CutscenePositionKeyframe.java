package net.exaltedzoro.cutscenelib.cutscene.keyframe;

import net.minecraft.world.phys.Vec3;

public record CutscenePositionKeyframe(Vec3 position, int tick, KeyframeInterpolation interpolation) {
}
