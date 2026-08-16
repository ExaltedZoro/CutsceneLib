package net.exaltedzoro.cutscenelib.event;

import net.exaltedzoro.cutscenelib.CutsceneLibClient;
import net.exaltedzoro.cutscenelib.cutscene.Cutscene;
import net.exaltedzoro.cutscenelib.cutscene.CutsceneManager;
import net.exaltedzoro.cutscenelib.cutscene.track.CameraRotationTrack;
import net.exaltedzoro.cutscenelib.cutscene.track.Track;
import net.exaltedzoro.cutscenelib.entity.CutsceneCameraEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

public class CutsceneEvents {

    @SubscribeEvent
    private static void renderCutscene(RenderFrameEvent.Pre event) {
        if (Minecraft.getInstance().getCameraEntity() instanceof CutsceneCameraEntity cameraEntity && !Minecraft.getInstance().isPaused()) {
            //CutsceneLib.LOGGER.info(cameraEntity.blockPosition().toString());
        }
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

            for (Track<?> track : activeCutscene.getData().getTracks()) {
                if (track instanceof CameraRotationTrack rotationTrack) {
                    Vec3 rotation = rotationTrack.getCurrentRotation();
                    event.setRoll(Mth.wrapDegrees((float) rotation.z()));

                    break;
                }
            }
        }
    }
}
