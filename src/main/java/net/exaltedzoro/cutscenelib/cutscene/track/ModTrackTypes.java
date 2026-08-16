package net.exaltedzoro.cutscenelib.cutscene.track;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.exaltedzoro.cutscenelib.CutsceneLib;
import net.exaltedzoro.cutscenelib.cutscene.keyframe.CameraPositionKeyframe;
import net.exaltedzoro.cutscenelib.cutscene.keyframe.CameraRotationKeyframe;
import net.exaltedzoro.cutscenelib.cutscene.keyframe.SoundKeyframe;
import net.exaltedzoro.cutscenelib.registry.ModRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

public class ModTrackTypes {
    public static final MapCodec<CameraPositionTrack> CAMERA_POSITION_TRACK_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    CameraPositionKeyframe.CODEC.listOf().fieldOf("keyframes").forGetter(CameraPositionTrack::getKeyframes)
            ).apply(instance, CameraPositionTrack::new)
    );

    public static final MapCodec<CameraRotationTrack> CAMERA_ROTATION_TRACK_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    CameraRotationKeyframe.CODEC.listOf().fieldOf("keyframes").forGetter(CameraRotationTrack::getKeyframes)
            ).apply(instance, CameraRotationTrack::new)
    );

    public static final MapCodec<SoundTrack> SOUND_TRACK_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    SoundKeyframe.CODEC.listOf().fieldOf("keyframes").forGetter(SoundTrack::getKeyframes)
            ).apply(instance, SoundTrack::new)
    );

    @SubscribeEvent
    public static void registerTrackTypes(RegisterEvent event) {
        event.register(ModRegistries.TRACK_TYPE_REGISTRY_KEY, registry -> {
            registry.register(ResourceLocation.fromNamespaceAndPath(CutsceneLib.MODID, "camera_position"), CAMERA_POSITION_TRACK_CODEC);
            registry.register(ResourceLocation.fromNamespaceAndPath(CutsceneLib.MODID, "camera_rotation"), CAMERA_ROTATION_TRACK_CODEC);
            registry.register(ResourceLocation.fromNamespaceAndPath(CutsceneLib.MODID, "sound"), SOUND_TRACK_CODEC);
        });
    }
}
