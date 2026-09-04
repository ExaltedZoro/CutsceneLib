package net.exaltedzoro.cutscenelib.cutscene.track.camera;

import com.mojang.serialization.MapCodec;
import net.exaltedzoro.cutscenelib.Config;
import net.exaltedzoro.cutscenelib.cutscene.Cutscene;
import net.exaltedzoro.cutscenelib.cutscene.CutsceneData;
import net.exaltedzoro.cutscenelib.cutscene.keyframe.camera.CameraShakeKeyframe;
import net.exaltedzoro.cutscenelib.cutscene.track.Track;
import net.exaltedzoro.cutscenelib.entity.CutsceneCameraEntity;
import net.exaltedzoro.cutscenelib.registry.ModRegistries;
import net.exaltedzoro.cutscenelib.registry.ModTrackTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.SimplexNoise;

import java.util.ArrayList;
import java.util.List;

public class CameraPositionShakeTrack extends Track<CameraShakeKeyframe> {

    // These values are entirely arbitrary, and used only to alter the base noise seed
    private final int xSeedModifier = 2119;
    private final int ySeedModifier = 6967;
    private final int zSeedModifier = 42042;

    private CameraShakeKeyframe activeKeyframe;

    public CameraPositionShakeTrack(List<CameraShakeKeyframe> keyframes) {
        super(new ArrayList<>(keyframes));
    }

    @Override
    public MapCodec<? extends Track<?>> type() {
        return ModTrackTypes.CAMERA_POSITION_SHAKE_TRACK_CODEC;
    }

    @Override
    public void evaluate(float time, Cutscene cutscene) {
        Registry<CutsceneData> registry = Minecraft.getInstance().level.registryAccess().registryOrThrow(ModRegistries.CUTSCENE_REGISTRY_KEY);
        ResourceLocation location = registry.getKey(cutscene.getData());
        int noiseSeed = location.toString().hashCode();

        // Find new keyframe to activate if one is available
        if (activeKeyframe == null) {
            for (CameraShakeKeyframe keyframe : getKeyframes()) {
                if (time >= keyframe.getTime() && time <= keyframe.getTime() + keyframe.getDuration()) {
                    activeKeyframe = keyframe;
                    break;
                }
            }
        }

        if (activeKeyframe != null) {
            if (activeKeyframe.getTime() + activeKeyframe.getDuration() >= time) {
                activeKeyframe = null;
            } else {
                Vec3 noise = generateNoise(noiseSeed, time);

                CutsceneCameraEntity cameraEntity = (CutsceneCameraEntity) Minecraft.getInstance().getCameraEntity();
                Vec3 cameraPosition = cameraEntity.position();
                cameraEntity.absMoveTo(cameraPosition.x() + noise.x(), cameraPosition.y() + noise.y(), cameraPosition.z() + noise.z());
            }
        }
    }

    @Override
    public void finalise() {
        sortKeyframes();
    }

    private Vec3 generateNoise(int seed, float time) {
        Vec3 noise = new Vec3(0, 0, 0);
        float configIntensity = Config.CAMERA_SHAKE_INTENSITY.get();
        float intensity = configIntensity * activeKeyframe.getIntensity();

        float fadeProgress = getFadeInFadeOutProgress(time);

        float seedX = (float) seed / xSeedModifier;
        float seedY = (float) seed / ySeedModifier;
        float seedZ = (float) seed / zSeedModifier;

        float xNoise = SimplexNoise.noise(seedX, time * activeKeyframe.getSpeed()) * intensity * fadeProgress;
        float yNoise = SimplexNoise.noise(seedY, time * activeKeyframe.getSpeed()) * intensity * fadeProgress;
        float zNoise = SimplexNoise.noise(seedZ, time * activeKeyframe.getSpeed()) * intensity * fadeProgress;

        noise.add(xNoise, yNoise, zNoise);

        return noise;
    }

    private float getFadeInFadeOutProgress(float time) {
        float timeFromStart = time - activeKeyframe.getTime();
        float timeToEnd = activeKeyframe.getDuration() - timeFromStart;
        if (timeFromStart <= activeKeyframe.getFadeIn()) {
            return timeFromStart / activeKeyframe.getFadeIn();
        } else if (timeToEnd <= activeKeyframe.getFadeOut()) {
            return timeToEnd / activeKeyframe.getFadeOut();
        } else {
            return 1;
        }
    }
}
