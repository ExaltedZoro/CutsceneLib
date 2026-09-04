package net.exaltedzoro.cutscenelib.cutscene;

import net.exaltedzoro.cutscenelib.CutsceneLib;
import net.exaltedzoro.cutscenelib.entity.CutsceneCameraEntity;
import net.exaltedzoro.cutscenelib.entity.ModEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;

public class CutsceneManager {
    private Cutscene activeCutscene = Cutscene.EMPTY;

    private ArrayList<Cutscene> queue =  new ArrayList<>();

    private long frame = 0;

    public Cutscene getActiveCutscene() {
        return activeCutscene;
    }

    private void setActiveCutscene(Cutscene activeCutscene) {
        this.activeCutscene = activeCutscene;
    }

    public boolean hasActiveCutscene() {
        return getActiveCutscene() != Cutscene.EMPTY;
    }

    public void scheduleCutscene(CutsceneData data, Vec3 origin, float rotation, long startTime) {
        Cutscene newCutscene = new Cutscene(data, origin, rotation, startTime);
        sortCutscenes();

        queue.add(newCutscene);
    }

    public void tick(float partialTick) {
        Level level = Minecraft.getInstance().level;
        if (level != null) {
            long gameTime = level.getGameTime();

            // If there is already a cutscene playing
            if (hasActiveCutscene() && Minecraft.getInstance().getCameraEntity() instanceof CutsceneCameraEntity) {
                frame += 1;
                // CutsceneLib.LOGGER.info("Frame: {}, Gametime: {}, Partial tick: {}", frame, gameTime, partialTick);
                getActiveCutscene().tick(partialTick);
            } else if (!queue.isEmpty()) {

                if (gameTime >= queue.getFirst().getStartTime()) {
                    startCutscene(queue.removeFirst(), level);
                    getActiveCutscene().tick(partialTick);
                }
            } else if (Minecraft.getInstance().getCameraEntity() instanceof CutsceneCameraEntity && Minecraft.getInstance().player != null) {
                Minecraft.getInstance().setCameraEntity(Minecraft.getInstance().player);
            }

            // If all cutscene tracks have finished, end the cutscene
            if (hasActiveCutscene() && getActiveCutscene().getData().getLength() + getActiveCutscene().getStartTime() <= gameTime) {
                endCutscene();
            }
        }
    }

    private void startCutscene(Cutscene cutscene, Level level) {
        setActiveCutscene(cutscene);

        CutsceneLib.LOGGER.info("Starting Cutscene");

        CutsceneCameraEntity cameraEntity = new CutsceneCameraEntity(ModEntities.CUTSCENE_CAMERA.get(), level);
        cameraEntity.absMoveTo(cutscene.getOrigin().x(), cutscene.getOrigin().y(), cutscene.getOrigin().z());
        cameraEntity.setXRot(0);
        cameraEntity.setYRot(0);
        Minecraft.getInstance().setCameraEntity(cameraEntity);
        cutscene.initialise();
        frame = 0;
    }

    private void endCutscene() {
        CutsceneLib.LOGGER.info("Cutscene ended, resetting camera entity");
        setActiveCutscene(Cutscene.EMPTY);

        Player player = Minecraft.getInstance().player;
        CutsceneCameraEntity cameraEntity = (CutsceneCameraEntity) Minecraft.getInstance().getCameraEntity();
        Minecraft.getInstance().setCameraEntity(player);
        cameraEntity.discard();
    }

    private void sortCutscenes() {
        queue.sort(Comparator.comparingLong(Cutscene::getStartTime));
    }
}
