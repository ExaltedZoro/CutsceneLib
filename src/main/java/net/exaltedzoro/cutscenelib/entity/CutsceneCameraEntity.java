package net.exaltedzoro.cutscenelib.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class CutsceneCameraEntity extends Entity {

    private UUID uuid = UUID.nameUUIDFromBytes("CutsceneLibCamera".getBytes(StandardCharsets.UTF_8));

    public CutsceneCameraEntity(EntityType<? extends Entity> entityType, Level level) {
        super(entityType, level);
        setUUID(uuid);
    }

    @Override
    public void tick() {

    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {}

    @Override
    public boolean isNoGravity() {
        return false;
    }
}
