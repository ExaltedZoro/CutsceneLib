package net.exaltedzoro.cutscenelib.cutscene.track.camera;

import com.mojang.serialization.MapCodec;
import net.exaltedzoro.cutscenelib.Config;
import net.exaltedzoro.cutscenelib.CutsceneLib;
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

public class CameraRotationShakeTrack extends Track<CameraShakeKeyframe> {

    private Vec3 currentNoise = Vec3.ZERO;

    // These values are entirely arbitrary, and used only to alter the base noise seed
    private final int xSeedModifier = 6769;
    private final int ySeedModifier = 9000;
    private final int zSeedModifier = 8008135;

    // Used to scale the noise to an appropriate level for camera rotation
    private final float totalModifier = 1;

    private CameraShakeKeyframe activeKeyframe = null;

    public CameraRotationShakeTrack(List<CameraShakeKeyframe> keyframes) {
        super(new ArrayList<>(keyframes));
    }

    @Override
    public MapCodec<? extends Track<?>> type() {
        return ModTrackTypes.CAMERA_ROTATION_SHAKE_TRACK_CODEC;
    }

    public Vec3 getCurrentNoise() {
        return currentNoise;
    }

    @Override
    public void evaluate(float time, Cutscene cutscene) {
        Registry<CutsceneData> registry = Minecraft.getInstance().level.registryAccess().registryOrThrow(ModRegistries.CUTSCENE_REGISTRY_KEY);
        ResourceLocation location = registry.getKey(cutscene.getData());
        int noiseSeed = location.toString().hashCode();
        //CutsceneLib.LOGGER.info("Seed: {}", noiseSeed);

        // Find new keyframe to activate if one is available
        if (activeKeyframe == null) {
            for (CameraShakeKeyframe keyframe : getKeyframes()) {
                if (time >= keyframe.getTime() && time <= keyframe.getTime() + keyframe.getDuration()) {
                    //CutsceneLib.LOGGER.info("Activating keyframe with time {} at {}", keyframe.getTime(), time);
                    //CutsceneLib.LOGGER.info("Test noise: {}", SimplexNoise.noise(100, 10));
                    activeKeyframe = keyframe;
                    break;
                }
            }
        }

        if (activeKeyframe != null) {
            if (activeKeyframe.getTime() + activeKeyframe.getDuration() <= time) {
                activeKeyframe = null;
                currentNoise = Vec3.ZERO;
            } else {
                currentNoise = generateNoise(noiseSeed, time);
                CutsceneCameraEntity cameraEntity = (CutsceneCameraEntity) Minecraft.getInstance().getCameraEntity();
                float x = cameraEntity.getXRot();
                float y = cameraEntity.getYRot();

                x += (float) currentNoise.x();
                y += (float) currentNoise.y();

                cameraEntity.setXRot(x);
                cameraEntity.setYRot(y);
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

        // CutsceneLib.LOGGER.info("X Seed: {}, Time: {}, Speed: {}, Intensity: {}, Fade: {}", seedX, time, activeKeyframe.getSpeed(), intensity, fadeProgress);

        float xNoise = SimplexNoise.noise(seedX, time * activeKeyframe.getSpeed() * 0.25f) * intensity * fadeProgress * totalModifier;
        float yNoise = SimplexNoise.noise(seedY, time * activeKeyframe.getSpeed() * 0.25f) * intensity * fadeProgress * totalModifier;
        float zNoise = SimplexNoise.noise(seedZ, time * activeKeyframe.getSpeed() * 0.25f) * intensity * fadeProgress * totalModifier;

        noise = noise.add(xNoise, yNoise, zNoise);
        // CutsceneLib.LOGGER.info("Noise: {}", noise);

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
