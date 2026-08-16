package net.exaltedzoro.cutscenelib.networking;

import io.netty.buffer.ByteBuf;
import net.exaltedzoro.cutscenelib.CutsceneLib;
import net.exaltedzoro.cutscenelib.CutsceneLibClient;
import net.exaltedzoro.cutscenelib.codec.ModCodecs;
import net.exaltedzoro.cutscenelib.cutscene.CutsceneData;
import net.exaltedzoro.cutscenelib.registry.ModRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CutsceneTriggerPayload(ResourceLocation cutsceneID, Vec3 origin, float rotation, long startTime) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CutsceneTriggerPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CutsceneLib.MODID, "cutscene_trigger"));

    public static final StreamCodec<ByteBuf, CutsceneTriggerPayload> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            CutsceneTriggerPayload::cutsceneID,
            ModCodecs.VEC3_STREAM_CODEC,
            CutsceneTriggerPayload::origin,
            ByteBufCodecs.FLOAT,
            CutsceneTriggerPayload::rotation,
            ByteBufCodecs.VAR_LONG,
            CutsceneTriggerPayload::startTime,
            CutsceneTriggerPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleData(final CutsceneTriggerPayload data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            CutsceneLib.LOGGER.info("Cutscene packet received");
            CutsceneData cutsceneData = context.player().registryAccess().registryOrThrow(ModRegistries.CUTSCENE_REGISTRY_KEY).get(data.cutsceneID);
            CutsceneLibClient.MANAGER.scheduleCutscene(cutsceneData, data.origin(), data.rotation(), data.startTime());
        });
    }
}
