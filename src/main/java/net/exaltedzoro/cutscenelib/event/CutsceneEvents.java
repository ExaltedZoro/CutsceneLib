package net.exaltedzoro.cutscenelib.event;

import net.exaltedzoro.cutscenelib.cutscene.Cutscene;
import net.exaltedzoro.cutscenelib.cutscene.track.CameraRotationTrack;
import net.exaltedzoro.cutscenelib.cutscene.track.Track;
import net.exaltedzoro.cutscenelib.entity.CutsceneCameraEntity;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import org.joml.Vector3f;

public class CutsceneEvents {

    @SubscribeEvent
    private static void renderCutscene(RenderFrameEvent.Pre event) {
        if (Minecraft.getInstance().getCameraEntity() instanceof CutsceneCameraEntity cameraEntity) {
            Cutscene activeCutscene = cameraEntity.getActiveCutscene();
            assert activeCutscene != null;
            float partialTick = event.getPartialTick().getGameTimeDeltaTicks();

            activeCutscene.tick(partialTick);
        }
    }

    /**
     * This event hook would not be necessary if Minecraft entities were allowed to have roll as part of their rotation.
     * However, since this is required for roll, it may as well be used to set all the rotation values.
     */
    @SubscribeEvent
    private static void setCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        if (Minecraft.getInstance().getCameraEntity() instanceof CutsceneCameraEntity cameraEntity) {
            Cutscene activeCutscene = cameraEntity.getActiveCutscene();
            assert activeCutscene != null;

            for (Track<?> track : activeCutscene.getData().getTracks()) {
                if (track instanceof CameraRotationTrack rotationTrack) {
                    Vector3f rotation = rotationTrack.getCurrentRotation().getEulerAnglesXYZ(new Vector3f());
                    event.setPitch(rotation.x());
                    event.setYaw(rotation.y());
                    event.setRoll(rotation.z());
                }
            }
        }
    }
}
