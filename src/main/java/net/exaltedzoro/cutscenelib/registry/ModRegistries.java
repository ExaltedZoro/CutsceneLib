package net.exaltedzoro.cutscenelib.registry;

import com.mojang.serialization.MapCodec;
import net.exaltedzoro.cutscenelib.CutsceneLib;
import net.exaltedzoro.cutscenelib.cutscene.CutsceneData;
import net.exaltedzoro.cutscenelib.cutscene.track.Track;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;


public class ModRegistries {
    public static final ResourceKey<Registry<MapCodec<? extends Track<?>>>> TRACK_TYPE_REGISTRY_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(CutsceneLib.MODID, "track_type"));
    public static final Registry<MapCodec<? extends Track<?>>> TRACK_TYPE_REGISTRY = new RegistryBuilder<>(TRACK_TYPE_REGISTRY_KEY).create();

    public static final ResourceKey<Registry<CutsceneData>> CUTSCENE_REGISTRY_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(CutsceneLib.MODID, "cutscenes"));

    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        event.register(TRACK_TYPE_REGISTRY);
    }

    @SubscribeEvent
    public static void registerCutsceneData(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(
                CUTSCENE_REGISTRY_KEY,
                CutsceneData.CODEC,
                CutsceneData.CODEC
        );
    }
}
