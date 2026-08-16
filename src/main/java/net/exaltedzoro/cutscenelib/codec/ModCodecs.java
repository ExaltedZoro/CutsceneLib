package net.exaltedzoro.cutscenelib.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.exaltedzoro.cutscenelib.cutscene.track.Track;
import net.exaltedzoro.cutscenelib.registry.ModRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.Function;

public class ModCodecs {
    public static final Codec<Track<?>> TRACK_CODEC = ModRegistries.TRACK_TYPE_REGISTRY.byNameCodec().dispatch(
            Track::type,
            Function.identity()
    );

    public static final Codec<Quaternionf> QUATERNION_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("x").forGetter(q -> q.getEulerAnglesXYZ(new Vector3f()).x()),
                    Codec.FLOAT.fieldOf("y").forGetter(q -> q.getEulerAnglesXYZ(new Vector3f()).y()),
                    Codec.FLOAT.fieldOf("z").forGetter(q -> q.getEulerAnglesXYZ(new Vector3f()).z())
            ).apply(instance, (x, y, z) -> new Quaternionf().rotateXYZ(
                    (float) Math.toRadians(x),
                    (float) Math.toRadians(y),
                    (float) Math.toRadians(z)
            ))
    );

    public static final StreamCodec<ByteBuf, Vec3> VEC3_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE,
            Vec3::x,
            ByteBufCodecs.DOUBLE,
            Vec3::y,
            ByteBufCodecs.DOUBLE,
            Vec3::z,
            Vec3::new
    );
}
