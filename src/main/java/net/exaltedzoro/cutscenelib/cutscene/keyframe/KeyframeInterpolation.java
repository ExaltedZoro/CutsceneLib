package net.exaltedzoro.cutscenelib.cutscene.keyframe;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum KeyframeInterpolation implements StringRepresentable {
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
    CUT;

    public static final Codec<KeyframeInterpolation> CODEC = StringRepresentable.fromEnum(KeyframeInterpolation::values);

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
