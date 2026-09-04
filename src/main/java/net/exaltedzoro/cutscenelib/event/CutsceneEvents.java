package net.exaltedzoro.cutscenelib.event;

import net.exaltedzoro.cutscenelib.CutsceneLib;
import net.exaltedzoro.cutscenelib.CutsceneLibClient;
import net.exaltedzoro.cutscenelib.cutscene.Cutscene;
import net.exaltedzoro.cutscenelib.cutscene.CutsceneManager;
import net.exaltedzoro.cutscenelib.cutscene.track.camera.CameraRotationShakeTrack;
import net.exaltedzoro.cutscenelib.cutscene.track.camera.CameraRotationTrack;
import net.exaltedzoro.cutscenelib.cutscene.track.FOVTrack;
import net.exaltedzoro.cutscenelib.cutscene.track.Track;
import net.exaltedzoro.cutscenelib.entity.CutsceneCameraEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.common.util.TriState;

public class CutsceneEvents {

    @SubscribeEvent
    private static void renderCutscene(RenderFrameEvent.Pre event) {
        if (Minecraft.getInstance().isPaused()) return;

        CutsceneLibClient.MANAGER.tick(event.getPartialTick().getGameTimeDeltaTicks());
    }

    /**
     * This event hook would not be necessary if Minecraft entities were allowed to have roll as part of their rotation.
     * However, since this is required for roll, it may as well be used to set all the rotation values.
     */
    @SubscribeEvent
    private static void setCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        CutsceneManager manager = CutsceneLibClient.MANAGER;
        if (manager.hasActiveCutscene() && manager.getActiveCutscene().isInitialised()) {
            Cutscene activeCutscene = manager.getActiveCutscene();

            float x = 0;
            float z = 0;

            for (Track<?> track : activeCutscene.getData().getTracks()) {
                if (track instanceof CameraRotationTrack rotationTrack) {
                    Vec3 camRotation = rotationTrack.getCurrentRotation();
                    x += (float) camRotation.x();
                    z += (float) camRotation.z();
                } else if (track instanceof CameraRotationShakeTrack shakeTrack) {
                    Vec3 shake = shakeTrack.getCurrentNoise();
                    x += (float) shake.x();
                    z += (float) shake.z();
                }
            }

            event.setPitch(x);
            event.setRoll(z);
        }
    }

    @SubscribeEvent
    public static void setCutsceneFOV(ViewportEvent.ComputeFov event) {
        CutsceneManager manager = CutsceneLibClient.MANAGER;
        if (manager.hasActiveCutscene() &&  manager.getActiveCutscene().isInitialised()) {
            Cutscene activeCutscene = manager.getActiveCutscene();

            for (Track<?> track : activeCutscene.getData().getTracks()) {
                if (track instanceof FOVTrack fovTrack) {
                    event.setFOV(fovTrack.getCurrentFOV());
                }
            }
        }
    }

    @SubscribeEvent
    public static void hideHandDuringCutscene(RenderHandEvent event) {
        CutsceneManager manager = CutsceneLibClient.MANAGER;
        if (manager.hasActiveCutscene() && Minecraft.getInstance().getCameraEntity() instanceof CutsceneCameraEntity) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void hidePlayerDuringCutscene(RenderPlayerEvent.Pre event) {
        CutsceneManager manager = CutsceneLibClient.MANAGER;
        if (manager.hasActiveCutscene() &&
                Minecraft.getInstance().getCameraEntity() instanceof CutsceneCameraEntity &&
                event.getEntity().is(Minecraft.getInstance().player) && manager.getActiveCutscene().getData().shouldHidePlayer())
        {
            event.setCanceled(true);
        }
    }
}
