package net.exaltedzoro.cutscenelib.cutscene.keyframe;

import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public class KeyframeUtil {

    /**
     * Rotates a position around the origin on the y-axis
     * @param position The position to be rotated (in local space for a cutscene with a non-zero origin)
     * @param angle The angle that the position will be rotated by, in degrees
     * @return position rotated around the y-axis by angle degrees
     */
    public static Vec3 getOrientedPosition(Vec3 position, float angle) {
        double angleRad = Math.toRadians(angle);

        double xNew = Math.cos(angleRad) * position.x() - Math.sin(angleRad) * position.z();
        double zNew = Math.sin(angleRad) * position.x() + Math.cos(angleRad) * position.z();

        return new Vec3(xNew, position.y(), zNew);
    }

    public static Quaternionf getOrientedRotation(Quaternionf rotation, float angle) {
        float y = rotation.y();

        y += (float) Math.PI * (angle / 180);

        return new Quaternionf(rotation.x(), y, rotation.z(), rotation.w());
    }
}
