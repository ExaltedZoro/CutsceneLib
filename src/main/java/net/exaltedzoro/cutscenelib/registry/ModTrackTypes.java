package net.exaltedzoro.cutscenelib.registry;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.exaltedzoro.cutscenelib.CutsceneLib;
import net.exaltedzoro.cutscenelib.cutscene.keyframe.camera.CameraPositionKeyframe;
import net.exaltedzoro.cutscenelib.cutscene.keyframe.camera.CameraRotationKeyframe;
import net.exaltedzoro.cutscenelib.cutscene.keyframe.FOVKeyframe;
import net.exaltedzoro.cutscenelib.cutscene.keyframe.SoundKeyframe;
import net.exaltedzoro.cutscenelib.cutscene.keyframe.camera.CameraShakeKeyframe;
import net.exaltedzoro.cutscenelib.cutscene.track.camera.CameraPositionShakeTrack;
import net.exaltedzoro.cutscenelib.cutscene.track.camera.CameraPositionTrack;
import net.exaltedzoro.cutscenelib.cutscene.track.camera.CameraRotationShakeTrack;
import net.exaltedzoro.cutscenelib.cutscene.track.camera.CameraRotationTrack;
import net.exaltedzoro.cutscenelib.cutscene.track.FOVTrack;
import net.exaltedzoro.cutscenelib.cutscene.track.SoundTrack;
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

    public static final MapCodec<CameraPositionShakeTrack> CAMERA_POSITION_SHAKE_TRACK_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    CameraShakeKeyframe.CODEC.listOf().fieldOf("keyframes").forGetter(CameraPositionShakeTrack::getKeyframes)
            ).apply(instance, CameraPositionShakeTrack::new)
    );

    public static final MapCodec<CameraRotationShakeTrack> CAMERA_ROTATION_SHAKE_TRACK_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    CameraShakeKeyframe.CODEC.listOf().fieldOf("keyframes").forGetter(CameraRotationShakeTrack::getKeyframes)
            ).apply(instance, CameraRotationShakeTrack::new)
    );

    public static final MapCodec<FOVTrack> FOV_TRACK_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    FOVKeyframe.CODEC.listOf().fieldOf("keyframes").forGetter(FOVTrack::getKeyframes)
            ).apply(instance, FOVTrack::new)
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
            registry.register(ResourceLocation.fromNamespaceAndPath(CutsceneLib.MODID, "camera_position_shake"), CAMERA_POSITION_SHAKE_TRACK_CODEC);
            registry.register(ResourceLocation.fromNamespaceAndPath(CutsceneLib.MODID, "camera_rotation_shake"), CAMERA_ROTATION_SHAKE_TRACK_CODEC);
            registry.register(ResourceLocation.fromNamespaceAndPath(CutsceneLib.MODID, "sound"), SOUND_TRACK_CODEC);
            registry.register(ResourceLocation.fromNamespaceAndPath(CutsceneLib.MODID, "fov"), FOV_TRACK_CODEC);
        });
    }
}
