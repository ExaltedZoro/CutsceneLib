package net.exaltedzoro.cutscenelib.entity;

import net.exaltedzoro.cutscenelib.CutsceneLib;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, CutsceneLib.MODID);

    public static final Supplier<EntityType<CutsceneCameraEntity>> CUTSCENE_CAMERA =
            ENTITY_TYPES.register("cutscene_camera", () -> EntityType.Builder.of(CutsceneCameraEntity::new, MobCategory.AMBIENT)
                    .sized(0, 0).build("cutscene_camera"));

    public static void register(IEventBus bus) {
        ENTITY_TYPES.register(bus);
    }
}
